package com.altnoir.poopsky.client.inventory;

import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.init.PoMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FlushToiletMenu extends AbstractContainerMenu {
    private static final int SLOT_COUNT = 1;
    private static final int INV_SLOT_START = 1;
    private static final int HOTBAR_SLOT_END = 37;

    private final Container flushToilet;

    public FlushToiletMenu(MenuType<FlushToiletMenu> menuType, int containerId, Inventory playerInventory) {
        this(menuType, containerId, playerInventory, new SimpleContainer(SLOT_COUNT));
    }

    public FlushToiletMenu(int containerId, Inventory playerInventory, Container container) {
        this(PoMenuTypes.FLUSH_TOILET.get(), containerId, playerInventory, container);
    }

    private FlushToiletMenu(MenuType<FlushToiletMenu> menuType, int containerId, Inventory playerInventory, Container container) {
        super(menuType, containerId);
        checkContainerSize(container, SLOT_COUNT);
        this.flushToilet = container;
        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, 0, 80, 20));

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 51 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 109));
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId == 0) {
            Slot slot = this.slots.get(slotId);
            ItemStack slotItem = slot.getItem();
            ItemStack carried = this.getCarried();

            if (!slotItem.isEmpty() && !slotItem.is(PoTags.Items.FLUSH_TOILET_SAVE) && !carried.isEmpty()) {
                slot.setByPlayer(carried.copy());
                this.setCarried(ItemStack.EMPTY);
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack source = slot.getItem();
        ItemStack result = source.copy();

        if (index < SLOT_COUNT) {
            if (!moveItemStackTo(source, INV_SLOT_START, HOTBAR_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            Slot flushSlot = this.slots.getFirst();
            ItemStack slotItem = flushSlot.getItem();

            if (!canReplaceSlot(slotItem, source)) {
                return ItemStack.EMPTY;
            }

            replaceSlotItem(flushSlot, slot, source);
            return result;
        }

        if (source.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (source.getCount() == result.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, source);
        return result;
    }

    private boolean canReplaceSlot(ItemStack slotItem, ItemStack newItem) {
        if (slotItem.isEmpty()) return true;
        if (!slotItem.is(PoTags.Items.FLUSH_TOILET_SAVE)) return true;
        return !ItemStack.isSameItemSameComponents(slotItem, newItem);
    }

    private void replaceSlotItem(Slot flushSlot, Slot sourceSlot, ItemStack newItem) {
        ItemStack oldItem = flushSlot.getItem();

        if (!oldItem.isEmpty() && oldItem.is(PoTags.Items.FLUSH_TOILET_SAVE)) {
            sourceSlot.setByPlayer(oldItem.copy());
        }

        flushSlot.setByPlayer(newItem.copy());
        newItem.shrink(newItem.getCount());
    }

    @Override
    public boolean stillValid(Player player) {
        return flushToilet.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        flushToilet.stopOpen(player);
    }
}