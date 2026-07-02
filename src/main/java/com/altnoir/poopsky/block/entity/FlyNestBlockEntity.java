package com.altnoir.poopsky.block.entity;

import com.altnoir.poopsky.client.inventory.FlyNestMenu;
import com.altnoir.poopsky.common.FlyType;
import com.altnoir.poopsky.init.PBlockEntityType;
import com.altnoir.poopsky.init.PFlyRecipes;
import com.altnoir.poopsky.init.PSoundEvents;
import com.altnoir.poopsky.item.p.FlyItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;

public class FlyNestBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT_1 = 1;
    public static final int SLOT_OUTPUT_2 = 2;
    public static final int SLOT_OUTPUT_3 = 3;
    public static final int SLOT_OUTPUT_4 = 4;
    public static final int TOTAL_SLOTS = 5;

    private static final int BASE_TICK_INTERVAL = 850;  // 基础生产间隔（tick），1只苍蝇=44.5秒
    private static final int STACK_BONUS_PER_ITEM = 10;  // 输入堆叠每多1个减少的tick数（0.5秒=10tick）
    private static final int MIN_INTERVAL = 20;          // 最快生产间隔（1秒=20tick）

    private int progress = 0;
    private int currentInterval = BASE_TICK_INTERVAL;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> FlyNestBlockEntity.this.progress;
                case 1 -> FlyNestBlockEntity.this.currentInterval;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                FlyNestBlockEntity.this.progress = value;
            } else if (index == 1) {
                FlyNestBlockEntity.this.currentInterval = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            syncToClient();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == SLOT_INPUT) return FlyItem.isFlyItem(stack);
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == SLOT_INPUT ? 88 : 64;
        }
    };

    // 自动化：上面/侧面 = 输入（只接受苍蝇）
    private final IItemHandler topSideHandler = new RangedWrapper(itemHandler, SLOT_INPUT, SLOT_INPUT + 1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return super.isItemValid(slot, stack) && FlyItem.isFlyItem(stack);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };

    // 自动化：下面 = 输出（只允许提取）
    private final IItemHandler bottomHandler = new RangedWrapper(itemHandler, SLOT_OUTPUT_1, TOTAL_SLOTS) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }
    };

    public FlyNestBlockEntity(BlockPos pos, BlockState blockState) {
        super(PBlockEntityType.FLY_NEST.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FlyNestBlockEntity be) {
        if (level.isClientSide) return;

        ItemStack inputStack = be.itemHandler.getStackInSlot(SLOT_INPUT);
        if (inputStack.isEmpty() || !FlyItem.isFlyItem(inputStack)) {
            be.progress = 0;
            be.currentInterval = BASE_TICK_INTERVAL;
            return;
        }

        // 所有输出槽满则停止生产
        if (be.areOutputsFull()) return;

        // 堆叠加速：输入堆叠每多1个减少0.5秒（10tick）
        int stackBonus = (inputStack.getCount() - 1) * STACK_BONUS_PER_ITEM;
        be.currentInterval = Math.max(MIN_INTERVAL, BASE_TICK_INTERVAL - stackBonus);
        be.progress++;

        if (be.progress >= be.currentInterval) {
            be.produce();
            be.progress = 0;
        }

        be.setChanged();
    }

    private boolean areOutputsFull() {
        for (int i = SLOT_OUTPUT_1; i <= SLOT_OUTPUT_4; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }

    private void produce() {
        ItemStack inputStack = itemHandler.getStackInSlot(SLOT_INPUT);
        FlyType.Type type = FlyItem.getFlyType(inputStack);
        ItemStack product = PFlyRecipes.getProduct(level, type);
        if (product.isEmpty()) return;

        ItemStack toInsert = product.copy();
        for (int i = SLOT_OUTPUT_1; i <= SLOT_OUTPUT_4; i++) {
            toInsert = tryInsert(i, toInsert);
            if (toInsert.isEmpty()) break;
        }
    }

    private ItemStack tryInsert(int slot, ItemStack stack) {
        ItemStack current = itemHandler.getStackInSlot(slot);
        if (current.isEmpty()) {
            itemHandler.setStackInSlot(slot, stack.copy());
            return ItemStack.EMPTY;
        }
        if (ItemStack.isSameItemSameComponents(current, stack)) {
            int space = current.getMaxStackSize() - current.getCount();
            int toAdd = Math.min(space, stack.getCount());
            if (toAdd > 0) {
                current.grow(toAdd);
                itemHandler.setStackInSlot(slot, current);
                stack.shrink(toAdd);
            }
        }
        return stack;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.poopsky.fly_nest");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new FlyNestMenu(id, playerInventory, createContainerProxy(), data);
    }

    private Container createContainerProxy() {
        return new SimpleContainer(TOTAL_SLOTS) {
            @Override
            public ItemStack getItem(int slot) {
                return itemHandler.getStackInSlot(slot);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                itemHandler.setStackInSlot(slot, stack);
            }

            @Override
            public ItemStack removeItem(int slot, int amount) {
                return itemHandler.extractItem(slot, amount, false);
            }

            @Override
            public ItemStack removeItemNoUpdate(int slot) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
                return stack;
            }

            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return itemHandler.isItemValid(slot, stack);
            }

            @Override
            public void setChanged() {
                FlyNestBlockEntity.this.setChanged();
            }

            @Override
            public boolean stillValid(Player player) {
                return Container.stillValidBlockEntity(FlyNestBlockEntity.this, player);
            }

            @Override
            public void stopOpen(Player player) {
                super.stopOpen(player);
                if (level != null && !level.isClientSide) {
                    level.playSound(null, worldPosition, PSoundEvents.BLOCK_FLY_NEST_CLOSE.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
                }
            }

            @Override
            public int getContainerSize() {
                return TOTAL_SLOTS;
            }

            @Override
            public boolean isEmpty() {
                for (int i = 0; i < TOTAL_SLOTS; i++) {
                    if (!itemHandler.getStackInSlot(i).isEmpty()) return false;
                }
                return true;
            }
        };
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public IItemHandler getTopSideHandler() {
        return topSideHandler;
    }

    public IItemHandler getBottomHandler() {
        return bottomHandler;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }
}
