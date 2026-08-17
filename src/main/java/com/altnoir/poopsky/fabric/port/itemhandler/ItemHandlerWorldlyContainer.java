package com.altnoir.poopsky.fabric.port.itemhandler;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemHandlerWorldlyContainer implements WorldlyContainer {
    private final BlockEntity blockEntity;
    private final IItemHandlerModifiable itemHandler;
    private final int[] inputSlots;
    private final int[] outputSlots;

    public ItemHandlerWorldlyContainer(
            BlockEntity blockEntity,
            IItemHandlerModifiable itemHandler,
            int[] inputSlots,
            int[] outputSlots) {
        this.blockEntity = blockEntity;
        this.itemHandler = itemHandler;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
    }

    @Override
    public int getContainerSize() {
        return itemHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return itemHandler.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = itemHandler.getStackInSlot(slot);

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack current = itemHandler.getStackInSlot(slot);

        if (stack.isEmpty()) {
            itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
            setChanged();
            return;
        }

        if (!current.isEmpty()
                && ItemStack.isSameItemSameComponents(current, stack)
                && stack.getCount() >= current.getCount()) {

            int added = stack.getCount() - current.getCount();

            if (added > 0) {
                ItemStack toInsert = stack.copy();
                toInsert.setCount(added);
                itemHandler.insertItem(slot, toInsert, false);
            }

            setChanged();
            return;
        }

        int limit = Math.min(itemHandler.getSlotLimit(slot), stack.getMaxStackSize());
        ItemStack result = stack.copy();
        result.setCount(Math.min(result.getCount(), limit));

        itemHandler.setStackInSlot(slot, result);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.getLevel() != null
                && !blockEntity.isRemoved()
                && player.distanceToSqr(
                blockEntity.getBlockPos().getX() + 0.5,
                blockEntity.getBlockPos().getY() + 0.5,
                blockEntity.getBlockPos().getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }

        setChanged();
    }

    @Override
    public void setChanged() {
        blockEntity.setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? outputSlots : inputSlots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        if (!contains(inputSlots, slot)) {
            return false;
        }

        if (!itemHandler.isItemValid(slot, stack)) {
            return false;
        }

        ItemStack current = itemHandler.getStackInSlot(slot);
        int limit = Math.min(itemHandler.getSlotLimit(slot), stack.getMaxStackSize());

        return current.isEmpty() || current.getCount() < limit;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return contains(outputSlots, slot);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (!itemHandler.isItemValid(slot, stack)) {
            return false;
        }

        ItemStack current = itemHandler.getStackInSlot(slot);
        int limit = Math.min(itemHandler.getSlotLimit(slot), stack.getMaxStackSize());

        return current.isEmpty() || current.getCount() < limit;
    }

    private static boolean contains(int[] slots, int slot) {
        for (int value : slots) {
            if (value == slot) {
                return true;
            }
        }
        return false;
    }
}