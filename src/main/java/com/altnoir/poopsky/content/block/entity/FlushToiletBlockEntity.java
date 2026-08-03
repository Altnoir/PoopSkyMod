package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.client.inventory.FlushToiletMenu;
import com.altnoir.poopsky.fabric.port.itemhandler.IItemHandler;
import com.altnoir.poopsky.fabric.port.itemhandler.ItemStackHandler;
import com.altnoir.poopsky.fabric.port.itemhandler.RangedWrapper;
import com.altnoir.poopsky.init.PoBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import org.jetbrains.annotations.Nullable;

public class FlushToiletBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_COUNT = 1;

    private BlockPos linkedPos;
    private String linkedDim;

    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            syncToClient();
        }
    };

    private final IItemHandler bottomHandler = new RangedWrapper(itemHandler, 0, 1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
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
                    if (!itemHandler.getStackInSlot(i).isEmpty()) return false;
                }
                return true;
            }
        };
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }


    public IItemHandler getBottomHandler() {
        return bottomHandler;
    }


    public void clearContents() {
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
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

        var targetDimension = ResourceLocation.tryParse(linkedDim);
        if (targetDimension == null) return;

        var server = ((ServerLevel) level).getServer();
        var targetWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, targetDimension));
        if (targetWorld == null) return;

        var chunkPos = new ChunkPos(this.getLinkedPos());
        targetWorld.getChunkSource().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);

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
        this.linkedDim = serverLevel.dimension().location().toString();
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        if (linkedPos != null && linkedDim != null) {
            tag.putLong("LinkedPos", linkedPos.asLong());
            tag.putString("LinkedDim", linkedDim);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        if (tag.contains("LinkedPos")) {
            this.linkedPos = BlockPos.of(tag.getLong("LinkedPos"));
            this.linkedDim = tag.getString("LinkedDim");
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        if (tag.contains("LinkedPos")) {
            this.linkedPos = BlockPos.of(tag.getLong("LinkedPos"));
            this.linkedDim = tag.getString("LinkedDim");
        }
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }
}
