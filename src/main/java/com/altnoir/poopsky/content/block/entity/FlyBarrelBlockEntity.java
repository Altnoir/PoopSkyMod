package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.client.inventory.FlyBarrelMenu;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.PFlyRecipes;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ContainerUser;
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
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public class FlyBarrelBlockEntity extends BlockEntity implements MenuProvider {
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
                case 0 -> FlyBarrelBlockEntity.this.progress;
                case 1 -> FlyBarrelBlockEntity.this.currentInterval;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                FlyBarrelBlockEntity.this.progress = value;
            } else if (index == 1) {
                FlyBarrelBlockEntity.this.currentInterval = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
            syncToClient();
        }

        @Override
        public boolean isValid(int slot, ItemResource resource) {
            if (slot == SLOT_INPUT) return !resource.isEmpty() && FlyItem.isFlyItem(resource.toStack(1));
            return false;
        }

        @Override
        protected int getCapacity(int slot, ItemResource resource) {
            return Math.min(slot == SLOT_INPUT ? 88 : 64, resource.getMaxStackSize());
        }
    };

    // 自动化：上面/侧面 = 输入（只接受苍蝇）
    private final ResourceHandler<ItemResource> topSideHandler = new RangedResourceHandler<>(itemHandler, SLOT_INPUT, SLOT_INPUT + 1) {
        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return super.isValid(slot, resource) && !resource.isEmpty() && FlyItem.isFlyItem(resource.toStack(1));
        }

        @Override
        public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }
    };

    // 自动化：下面 = 输出（只允许提取）
    private final ResourceHandler<ItemResource> bottomHandler = new RangedResourceHandler<>(itemHandler, SLOT_OUTPUT_1, TOTAL_SLOTS) {
        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return false;
        }

        @Override
        public int insert(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }
    };

    public FlyBarrelBlockEntity(BlockPos pos, BlockState blockState) {
        super(PoBlockEntityType.FLY_BARREL.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FlyBarrelBlockEntity be) {
        if (level.isClientSide()) return;

        ItemStack inputStack = be.getStackInSlot(SLOT_INPUT);
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

        if (level.getRandom().nextDouble() < 0.005) {
            level.playSound(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, PoSoundEvents.BLOCK_FLY_BARREL_WORK.get(), SoundSource.BLOCKS, 1.0F, 1.2F);
        }

        be.setChanged();
    }

    private boolean areOutputsFull() {
        for (int i = SLOT_OUTPUT_1; i <= SLOT_OUTPUT_4; i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }

    private void produce() {
        ItemStack inputStack = getStackInSlot(SLOT_INPUT);
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
        ItemStack current = getStackInSlot(slot);
        if (current.isEmpty()) {
            setStackInSlot(slot, stack.copy());
            return ItemStack.EMPTY;
        }
        if (ItemStack.isSameItemSameComponents(current, stack)) {
            int space = current.getMaxStackSize() - current.getCount();
            int toAdd = Math.min(space, stack.getCount());
            if (toAdd > 0) {
                current.grow(toAdd);
                setStackInSlot(slot, current);
                stack.shrink(toAdd);
            }
        }
        return stack;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.poopsky.fly_barrel");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new FlyBarrelMenu(id, playerInventory, createContainerProxy(), data);
    }

    private Container createContainerProxy() {
        return new SimpleContainer(TOTAL_SLOTS) {
            @Override
            public ItemStack getItem(int slot) {
                return getStackInSlot(slot);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                setStackInSlot(slot, stack);
            }

            @Override
            public ItemStack removeItem(int slot, int amount) {
                return extractItem(slot, amount, false);
            }

            @Override
            public ItemStack removeItemNoUpdate(int slot) {
                ItemStack stack = getStackInSlot(slot);
                setStackInSlot(slot, ItemStack.EMPTY);
                return stack;
            }

            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return isItemValid(slot, stack);
            }

            @Override
            public void setChanged() {
                FlyBarrelBlockEntity.this.setChanged();
            }

            @Override
            public boolean stillValid(Player player) {
                return Container.stillValidBlockEntity(FlyBarrelBlockEntity.this, player);
            }

            @Override
            public void stopOpen(ContainerUser containerUser) {
                super.stopOpen(containerUser);
                if (level != null && !level.isClientSide()) {
                    level.playSound(null, worldPosition, PoSoundEvents.BLOCK_FLY_BARREL_CLOSE.get(), SoundSource.BLOCKS, 0.5F, 0.7F);
                }
            }

            @Override
            public int getContainerSize() {
                return TOTAL_SLOTS;
            }

            @Override
            public boolean isEmpty() {
                for (int i = 0; i < TOTAL_SLOTS; i++) {
                    if (!getStackInSlot(i).isEmpty()) return false;
                }
                return true;
            }
        };
    }

    public ItemStacksResourceHandler getItemHandler() {
        return itemHandler;
    }

    public ResourceHandler<ItemResource> getTopSideHandler() {
        return topSideHandler;
    }

    public ResourceHandler<ItemResource> getBottomHandler() {
        return bottomHandler;
    }

    public ItemStack getStackInSlot(int slot) {
        ItemResource resource = itemHandler.getResource(slot);
        long amount = itemHandler.getAmountAsLong(slot);
        return resource.isEmpty() || amount <= 0 ? ItemStack.EMPTY : resource.toStack((int) amount);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            itemHandler.set(slot, ItemResource.EMPTY, 0);
        } else {
            itemHandler.set(slot, ItemResource.of(stack), stack.getCount());
        }
    }

    private ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemResource resource = itemHandler.getResource(slot);
        if (resource.isEmpty() || amount <= 0) return ItemStack.EMPTY;
        try (Transaction tx = Transaction.openRoot()) {
            int extracted = itemHandler.extract(slot, resource, amount, tx);
            if (!simulate) tx.commit();
            return resource.toStack(extracted);
        }
    }

    private boolean isItemValid(int slot, ItemStack stack) {
        return itemHandler.isValid(slot, ItemResource.of(stack));
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
