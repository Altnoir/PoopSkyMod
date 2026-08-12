package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.client.inventory.BreedingChestMenu;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.PFlyRecipes;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;

public class BreedingChestBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_FECES = 0;
    public static final int SLOT_FLY_1 = 1;
    public static final int SLOT_FLY_2 = 2;
    public static final int SLOT_OUTPUT_1 = 3;
    public static final int SLOT_OUTPUT_2 = 4;
    public static final int SLOT_OUTPUT_3 = 5;
    public static final int TOTAL_SLOTS = 6;

    private static final int BASE_TICK_INTERVAL = 1200; // 基础繁殖间隔（tick）
    private static final int SCAN_INTERVAL = 80;         // 环境扫描间隔（tick）
    private static final int SCAN_RANGE = 2;             // 扫描范围（5x5x5）

    private static final int SCAN_EDGE = SCAN_RANGE * 2 + 1;
    private static final int SCAN_BLOCKS_PER_TICK = 10;
    private static final int TOTAL_SCAN_BLOCKS = SCAN_EDGE * SCAN_EDGE * SCAN_EDGE;
    private int progress = 0;
    private int currentInterval = BASE_TICK_INTERVAL;
    private int currentPoopBonus = 0;
    private int currentMaggotsBonus = 0;
    private int scanCooldown = 0;
    private int scanIndex = -1;
    private int scanPoop;
    private int scanMaggots;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BreedingChestBlockEntity.this.progress;
                case 1 -> BreedingChestBlockEntity.this.currentInterval;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                BreedingChestBlockEntity.this.progress = value;
            } else if (index == 1) {
                BreedingChestBlockEntity.this.currentInterval = value;
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
            if (slot == SLOT_FECES) return stack.is(PoTags.Items.POOPS);
            if (slot == SLOT_FLY_1 || slot == SLOT_FLY_2) return FlyItem.isFlyItem(stack);
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == SLOT_FECES) return 88;
            if (slot == SLOT_FLY_1 || slot == SLOT_FLY_2) return 1;
            return super.getSlotLimit(slot);
        }
    };

    // 自动化：上面/侧面 = 输入（粪便 + 苍蝇）
    private final IItemHandler topSideHandler = new RangedWrapper(itemHandler, SLOT_FECES, SLOT_FECES + 1) {
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

    public BreedingChestBlockEntity(BlockPos pos, BlockState blockState) {
        super(PoBlockEntityType.BREEDING_CHEST.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BreedingChestBlockEntity be) {
        if (level.isClientSide()) return;

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

        if (be.scanIndex >= 0) {
            be.continueScan(level, pos);
        }

        if (be.scanIndex < 0 && be.scanCooldown <= 0) {
            be.scanCooldown = SCAN_INTERVAL;
            be.startScan();
            be.continueScan(level, pos);
        } else {
            be.scanCooldown--;
        }

        // 检查粪便数量是否满足当前产量需求
        int neededFeces = 1 + be.currentMaggotsBonus;
        if (feces.getCount() < neededFeces) {
            be.progress = 0;
            be.currentInterval = BASE_TICK_INTERVAL;
            return;
        }

        be.currentInterval = Math.max(20, BASE_TICK_INTERVAL - be.currentPoopBonus * 10);
        be.progress++;

        if (be.progress >= be.currentInterval) {
            be.breed();
            be.progress = 0;
        }

        if (level.getRandom().nextDouble() < 0.005) {
            level.playSound(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, PoSoundEvents.BLOCK_BREEDING_CHEST_WORK.get(), SoundSource.BLOCKS, 1.0F, 1.2F);
        }

        be.setChanged();
    }

    private void startScan() {
        scanIndex = 0;
        scanPoop = 0;
        scanMaggots = 0;
    }

    private void continueScan(Level level, BlockPos pos) {
        int checked = 0;
        while (checked < SCAN_BLOCKS_PER_TICK && scanIndex < TOTAL_SCAN_BLOCKS) {
            int index = scanIndex++;
            int dz = index % SCAN_EDGE - SCAN_RANGE;
            int dy = (index / SCAN_EDGE) % SCAN_EDGE - SCAN_RANGE;
            int dx = index / (SCAN_EDGE * SCAN_EDGE) - SCAN_RANGE;
            BlockPos checkPos = pos.offset(dx, dy, dz);
            if (!checkPos.equals(pos)) {
                BlockState state = level.getBlockState(checkPos);
                if (state.is(PoTags.Blocks.BREEDING_CHEST_ACCELERATOR)) {
                    scanPoop++;
                }
                if (state.is(PoTags.Blocks.BREEDING_CHEST_PARALLELISM)) {
                    scanMaggots++;
                }
            }
            checked++;
        }

        if (scanIndex >= TOTAL_SCAN_BLOCKS) {
            scanIndex = -1;
            currentPoopBonus = scanPoop;
            currentMaggotsBonus = scanMaggots;
        }
    }

    private boolean areOutputsFull() {
        for (int i = SLOT_OUTPUT_1; i <= SLOT_OUTPUT_3; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }

    private void breed() {
        ItemStack fly1 = itemHandler.getStackInSlot(SLOT_FLY_1);
        ItemStack fly2 = itemHandler.getStackInSlot(SLOT_FLY_2);

        FlyType.Type type1 = FlyItem.getFlyType(fly1);
        FlyType.Type type2 = FlyItem.getFlyType(fly2);

        // 产卵数量：基础1个 + 每个蛆块+1
        int count = 1 + currentMaggotsBonus;

        // 消耗对应数量的粪便
        ItemStack feces = itemHandler.getStackInSlot(SLOT_FECES);
        feces.shrink(count);
        if (feces.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_FECES, ItemStack.EMPTY);
        }

        // 每个产物独立进行变异判定
        for (int j = 0; j < count; j++) {
            PFlyRecipes.MutationResult result = PFlyRecipes.tryMutate(level, type1, type2);
            ItemStack flyProduct = FlyItem.withType(result.result());
            flyProduct.setCount(1);

            // 尝试放入输出槽
            for (int i = SLOT_OUTPUT_1; i <= SLOT_OUTPUT_3; i++) {
                ItemStack remainder = tryInsert(i, flyProduct.copy());
                if (remainder.isEmpty()) {
                    break;
                }
                flyProduct = remainder;
            }
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
        return Component.translatable("container.poopsky.breeding_chest");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new BreedingChestMenu(id, playerInventory, createContainerProxy(), data);
    }

    private Container createContainerProxy() {
        return new SimpleContainer(TOTAL_SLOTS) {
            @Override
            public ItemStack getItem(int slot) {
                return itemHandler.getStackInSlot(slot);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if (stack.getCount() > itemHandler.getSlotLimit(slot)) {
                    stack = stack.copy();
                    stack.setCount(itemHandler.getSlotLimit(slot));
                }
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
                BreedingChestBlockEntity.this.setChanged();
            }

            @Override
            public boolean stillValid(Player player) {
                return Container.stillValidBlockEntity(BreedingChestBlockEntity.this, player);
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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output.child("inventory"));
        output.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("inventory").ifPresent(itemHandler::deserialize);
        progress = input.getIntOr("progress", 0);
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }
}
