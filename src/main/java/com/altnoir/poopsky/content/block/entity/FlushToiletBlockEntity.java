package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.client.inventory.FlushToiletMenu;
import com.altnoir.poopsky.init.PoBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public class FlushToiletBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_COUNT = 1;

    private BlockPos linkedPos;
    private String linkedDim;

    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
            syncToClient();
        }
    };

    private final ResourceHandler<ItemResource> bottomHandler = new RangedResourceHandler<>(itemHandler, 0, 1) {
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

    public FlushToiletBlockEntity(BlockPos pos, BlockState blockState) {
        super(PoBlockEntityType.FLUSH_TOILET.get(), pos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.poopsky.flush_toilet");
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new FlushToiletMenu(id, playerInventory, createContainerProxy());
    }

    private Container createContainerProxy() {
        return new SimpleContainer(SLOT_COUNT) {
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
                return true;
            }

            @Override
            public void setChanged() {
                FlushToiletBlockEntity.this.setChanged();
            }

            @Override
            public boolean stillValid(Player player) {
                return Container.stillValidBlockEntity(FlushToiletBlockEntity.this, player);
            }

            @Override
            public int getContainerSize() {
                return SLOT_COUNT;
            }

            @Override
            public boolean isEmpty() {
                for (int i = 0; i < SLOT_COUNT; i++) {
                    if (!getStackInSlot(i).isEmpty()) return false;
                }
                return true;
            }
        };
    }

    public ItemStacksResourceHandler getItemHandler() {
        return itemHandler;
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

    public void clearContents() {
        setStackInSlot(0, ItemStack.EMPTY);
    }

    public String getLinkedDim() {
        return linkedDim;
    }

    public BlockPos getLinkedPos() {
        return linkedPos;
    }

    public void clearLinkedBlock() {
        if (level == null || level.isClientSide()) return;
        if (linkedPos == null || linkedDim == null || linkedDim.isBlank()) return;

        var targetDimension = Identifier.tryParse(linkedDim);
        if (targetDimension == null) return;

        var server = ((ServerLevel) level).getServer();
        var targetWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, targetDimension));
        if (targetWorld == null) return;

        var chunkPos = ChunkPos.containing(this.linkedPos);
        targetWorld.getChunkSource().getChunk(chunkPos.x(), chunkPos.z(), ChunkStatus.FULL, true);

        if (targetWorld.getBlockEntity(linkedPos) instanceof FlushToiletBlockEntity be) {
            be.setLinkedPos(BlockPos.ZERO, "");
        }
    }

    public void setLinkedPos(BlockPos pos, String dim) {
        this.linkedPos = pos;
        this.linkedDim = dim;
        this.setChanged();
    }

    public void setLinkedPos(BlockPos pos, ServerLevel serverLevel) {
        this.linkedPos = pos;
        this.linkedDim = serverLevel.dimension().identifier().toString();
        this.setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output.child("inventory"));
        if (linkedPos != null && linkedDim != null) {
            output.putLong("LinkedPos", linkedPos.asLong());
            output.putString("LinkedDim", linkedDim);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("inventory").ifPresent(itemHandler::deserialize);
        input.getLong("LinkedPos").ifPresent(value -> this.linkedPos = BlockPos.of(value));
        input.getString("LinkedDim").ifPresent(value -> this.linkedDim = value);
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }
}
