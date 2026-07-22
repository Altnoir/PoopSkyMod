package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.client.inventory.FlushToiletMenu;
import com.altnoir.poopsky.init.PoBlockEntityType;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class FlushToiletBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_COUNT = 1;

    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            syncToClient();
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

    public void clearContents() {
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
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