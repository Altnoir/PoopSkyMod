package com.altnoir.poopsky.block.entity;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.init.PFlyRecipes;
import com.altnoir.poopsky.init.PFlyTypes;
import com.altnoir.poopsky.init.PBlockEntityType;
import com.altnoir.poopsky.item.p.FlyItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

    private static final int BASE_TICK_INTERVAL = 200;  // 基础生产间隔（tick），环境加速会让这个值减小
    private static final int MAX_ENVIRONMENT_BONUS = 10; // 最大环境加速（附近粪便块数量上限）
    private static final int BASE_PRODUCT_COUNT = 1;     // 一次产出数量（受环境加速影响）
    private static final int MAX_PRODUCT_COUNT = 4;

    private int progress = 0;

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
            return 64;
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
            return;
        }

        // 所有输出槽满则停止生产
        if (be.areOutputsFull()) return;

        // 环境加速：附近的粪便块越多，生产越快
        int envBonus = be.getEnvironmentBonus(level, pos);
        int currentInterval = Math.max(20, BASE_TICK_INTERVAL - envBonus * 15);
        be.progress++;

        if (be.progress >= currentInterval) {
            be.produce();
            be.progress = 0;
        }

        be.setChanged();
    }

    private int getEnvironmentBonus(Level level, BlockPos pos) {
        int bonus = 0;
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            if (checkPos.equals(pos)) continue;
            BlockState state = level.getBlockState(checkPos);
            if (state.is(PTags.Blocks.POOP_BLOCKS)) {
                bonus++;
                if (bonus >= MAX_ENVIRONMENT_BONUS) return bonus;
            }
        }
        return bonus;
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
        PFlyTypes.FlyType type = FlyItem.getFlyType(inputStack);
        ItemStack product = PFlyRecipes.getProduct(level, type);
        if (product.isEmpty()) return;

        int envBonus = getEnvironmentBonus(level, worldPosition);
        int count = Math.min(BASE_PRODUCT_COUNT + envBonus / 3, MAX_PRODUCT_COUNT);

        ItemStack toInsert = product.copyWithCount(count);
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
        return new com.altnoir.poopsky.inventory.FlyNestMenu(id, playerInventory, createContainerProxy());
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
