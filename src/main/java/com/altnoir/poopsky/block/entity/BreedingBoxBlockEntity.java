package com.altnoir.poopsky.block.entity;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.client.inventory.BreedingBoxMenu;
import com.altnoir.poopsky.init.PBlockEntityType;
import com.altnoir.poopsky.init.PFlyRecipes;
import com.altnoir.poopsky.init.PFlyTypes;
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

public class BreedingBoxBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_FECES = 0;
    public static final int SLOT_FLY_1 = 1;
    public static final int SLOT_FLY_2 = 2;
    public static final int SLOT_OUTPUT_1 = 3;
    public static final int SLOT_OUTPUT_2 = 4;
    public static final int SLOT_OUTPUT_3 = 5;
    public static final int TOTAL_SLOTS = 6;

    private static final int BASE_TICK_INTERVAL = 400; // 基础繁殖间隔（tick）
    private static final int MAX_ENVIRONMENT_BONUS = 10;

    private int progress = 0;
    private int currentInterval = BASE_TICK_INTERVAL;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BreedingBoxBlockEntity.this.progress;
                case 1 -> BreedingBoxBlockEntity.this.currentInterval;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                BreedingBoxBlockEntity.this.progress = value;
            } else if (index == 1) {
                BreedingBoxBlockEntity.this.currentInterval = value;
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
            if (slot == SLOT_FECES) return stack.is(PTags.Items.POOPS);
            if (slot == SLOT_FLY_1 || slot == SLOT_FLY_2) return FlyItem.isFlyItem(stack);
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }
    };

    // 自动化：上面/侧面 = 输入（粪便 + 苍蝇）
    private final IItemHandler topSideHandler = new RangedWrapper(itemHandler, SLOT_FECES, SLOT_FLY_2 + 1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };

    // 自动化：下面 = 输出
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

    public BreedingBoxBlockEntity(BlockPos pos, BlockState blockState) {
        super(PBlockEntityType.BREEDING_BOX.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BreedingBoxBlockEntity be) {
        if (level.isClientSide) return;

        ItemStack fly1 = be.itemHandler.getStackInSlot(SLOT_FLY_1);
        ItemStack fly2 = be.itemHandler.getStackInSlot(SLOT_FLY_2);
        ItemStack feces = be.itemHandler.getStackInSlot(SLOT_FECES);

        if (fly1.isEmpty() || fly2.isEmpty() || feces.isEmpty()) {
            be.progress = 0;
            be.currentInterval = BASE_TICK_INTERVAL;
            return;
        }

        if (!FlyItem.isFlyItem(fly1) || !FlyItem.isFlyItem(fly2)) {
            be.progress = 0;
            be.currentInterval = BASE_TICK_INTERVAL;
            return;
        }

        // 所有输出槽满则停止
        if (be.areOutputsFull()) return;

        // 环境加速：附近的粪便块越多，繁殖越快
        int envBonus = be.getEnvironmentBonus(level, pos);
        be.currentInterval = Math.max(40, BASE_TICK_INTERVAL - envBonus * 30);
        be.progress++;

        if (be.progress >= be.currentInterval) {
            be.breed();
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
        for (int i = SLOT_OUTPUT_1; i <= SLOT_OUTPUT_3; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }

    private void breed() {
        // 消耗一个粪便
        ItemStack feces = itemHandler.getStackInSlot(SLOT_FECES);
        feces.shrink(1);
        if (feces.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_FECES, ItemStack.EMPTY);
        }

        ItemStack fly1 = itemHandler.getStackInSlot(SLOT_FLY_1);
        ItemStack fly2 = itemHandler.getStackInSlot(SLOT_FLY_2);

        PFlyTypes.FlyType type1 = FlyItem.getFlyType(fly1);
        PFlyTypes.FlyType type2 = FlyItem.getFlyType(fly2);

        // 变异判定
        PFlyRecipes.MutationResult result = PFlyRecipes.tryMutate(level, type1, type2);

        // 产卵数量：基础1个 + 环境加成
        int envBonus = getEnvironmentBonus(level, worldPosition);
        int count = Math.max(1, 1 + envBonus / 3);

        ItemStack flyProduct = FlyItem.withType(result.result());
        flyProduct.setCount(count);

        // 尝试放入输出槽
        for (int i = SLOT_OUTPUT_1; i <= SLOT_OUTPUT_3; i++) {
            ItemStack remainder = tryInsert(i, flyProduct.copy());
            if (remainder.isEmpty()) {
                break;
            }
            flyProduct = remainder;
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
        return Component.translatable("container.poopsky.breeding_box");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new BreedingBoxMenu(id, playerInventory, createContainerProxy(), data);
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
                BreedingBoxBlockEntity.this.setChanged();
            }

            @Override
            public boolean stillValid(Player player) {
                return Container.stillValidBlockEntity(BreedingBoxBlockEntity.this, player);
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
