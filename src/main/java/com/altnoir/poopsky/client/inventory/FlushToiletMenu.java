package com.altnoir.poopsky.client.inventory;

import com.altnoir.poopsky.init.PoMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

        // 1 slot in the center
        this.addSlot(new Slot(container, 0, 80, 20));

        // Player inventory
        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 51 + k * 18));
            }
        }

        // Player hotbar
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 109));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < SLOT_COUNT) {
                // From flush toilet slot to player inventory
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, HOTBAR_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory to flush toilet slot
                if (!this.moveItemStackTo(itemstack1, 0, SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.flushToilet.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.flushToilet.stopOpen(player);
    }
}