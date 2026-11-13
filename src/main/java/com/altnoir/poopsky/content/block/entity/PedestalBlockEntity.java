package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.init.PoBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PedestalBlockEntity extends BlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            contentsVersion++;
            setChanged();
            if (level != null && !level.isClientSide) {
                BlockState state = getBlockState();
                level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
            }
        }
    };
    private final IItemHandler input = new RangedWrapper(inventory, 0, 1) {
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };
    private final IItemHandler output = new RangedWrapper(inventory, 0, 1) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    };
    private long contentsVersion;
    private UUID identity = UUID.randomUUID();

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.PEDESTAL.get(), pos, state);
    }

    public IItemHandler getHandler(@Nullable Direction direction) {
        if (direction == Direction.UP) return input;
        if (direction == Direction.DOWN) return output;
        return inventory;
    }

    public ItemStack getItem() {
        return inventory.getStackInSlot(0);
    }

    public long getContentsVersion() {
        return contentsVersion;
    }

    public UUID getIdentity() {
        return identity;
    }

    public boolean insertOne(ItemStack stack) {
        return inventory.insertItem(0, stack.copyWithCount(1), false).isEmpty();
    }

    public ItemStack removeItem() {
        return inventory.extractItem(0, 1, false);
    }

    public void dropContents() {
        if (level != null && !level.isClientSide) {
            ItemStack item = removeItem();
            if (!item.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.7D,
                        worldPosition.getZ() + 0.5D, item);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putUUID("identity", identity);
        tag.putLong("contents_version", contentsVersion);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        if (tag.hasUUID("identity")) identity = tag.getUUID("identity");
        contentsVersion = tag.getLong("contents_version");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
