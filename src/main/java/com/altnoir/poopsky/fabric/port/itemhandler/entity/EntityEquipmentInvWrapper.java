package com.altnoir.poopsky.fabric.port.itemhandler.entity;

import com.altnoir.poopsky.fabric.port.itemhandler.IItemHandlerModifiable;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityEquipmentInvWrapper implements IItemHandlerModifiable {
    protected final LivingEntity entity;

    protected final List<EquipmentSlot> slots;

    public EntityEquipmentInvWrapper(final LivingEntity entity, final EquipmentSlot.Type slotType) {
        this.entity = entity;

        final List<EquipmentSlot> slots = new ArrayList<EquipmentSlot>();

        for (final EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == slotType) {
                slots.add(slot);
            }
        }

        this.slots = ImmutableList.copyOf(slots);
    }

    @Override
    public int getSlots() {
        return slots.size();
    }

    @Override
    public ItemStack getStackInSlot(final int slot) {
        return entity.getItemBySlot(validateSlotIndex(slot));
    }

    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        final EquipmentSlot equipmentSlot = validateSlotIndex(slot);

        final ItemStack existing = entity.getItemBySlot(equipmentSlot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                entity.setItemSlot(equipmentSlot, reachedLimit ? stack.copyWithCount(limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
        }

        return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        final EquipmentSlot equipmentSlot = validateSlotIndex(slot);

        final ItemStack existing = entity.getItemBySlot(equipmentSlot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        final int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                entity.setItemSlot(equipmentSlot, ItemStack.EMPTY);
            }

            return existing;
        } else {
            if (!simulate) {
                entity.setItemSlot(equipmentSlot, existing.copyWithCount(existing.getCount() - toExtract));
            }

            return existing.copyWithCount(toExtract);
        }
    }

    @Override
    public int getSlotLimit(final int slot) {
        final EquipmentSlot equipmentSlot = validateSlotIndex(slot);
        return equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR ? 1 : Item.ABSOLUTE_MAX_STACK_SIZE;
    }

    protected int getStackLimit(final int slot, final ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public void setStackInSlot(final int slot, final ItemStack stack) {
        final EquipmentSlot equipmentSlot = validateSlotIndex(slot);
        if (ItemStack.matches(entity.getItemBySlot(equipmentSlot), stack))
            return;
        entity.setItemSlot(equipmentSlot, stack);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    protected EquipmentSlot validateSlotIndex(final int slot) {
        if (slot < 0 || slot >= slots.size())
            throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + slots.size() + ")");

        return slots.get(slot);
    }
}
