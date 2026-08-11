package com.altnoir.poopsky.client.inventory;

import com.altnoir.poopsky.content.block.p.PortableToiletBlock;
import com.altnoir.poopsky.init.PoMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class PortableToiletMenu extends AbstractContainerMenu {
    private static final int SLOT_COUNT = 1;

    private final Container trash;
    private final Slot trashSlot;
    private final ContainerLevelAccess access;

    public PortableToiletMenu(MenuType<PortableToiletMenu> menuType, int containerId, Inventory playerInventory) {
        this(menuType, containerId, playerInventory, new SimpleContainer(SLOT_COUNT), ContainerLevelAccess.NULL);
    }

    public PortableToiletMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        this(PoMenuTypes.PORTABLE_TOILET.get(), containerId, playerInventory, new SimpleContainer(SLOT_COUNT), access);
    }

    private PortableToiletMenu(MenuType<PortableToiletMenu> menuType, int containerId, Inventory playerInventory, Container container, ContainerLevelAccess access) {
        super(menuType, containerId);
        checkContainerSize(container, SLOT_COUNT);
        this.trash = container;
        this.access = access;
        container.startOpen(playerInventory.player);

        this.trashSlot = this.addSlot(new Slot(container, 0, 80, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }

            @Override
            public boolean mayPickup(Player player) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 51 + row * 18));
            }
        }

        for (int hotbar = 0; hotbar < 9; hotbar++) {
            this.addSlot(new Slot(playerInventory, hotbar, 8 + hotbar * 18, 109));
        }
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput input, Player player) {
        if (slotId == 0) {
            if (input == ContainerInput.PICKUP && (button == 0 || button == 1)) {
                ItemStack carried = this.getCarried();
                if (!carried.isEmpty()) {
                    int count = button == 1 ? 1 : carried.getCount();
                    this.trashSlot.setByPlayer(carried.copyWithCount(count));
                    ItemStack remainder = carried.copy();
                    remainder.shrink(count);
                    this.setCarried(remainder);
                }
            } else if (input == ContainerInput.SWAP && button >= 0 && button < player.getInventory().getContainerSize()) {
                ItemStack hotbar = player.getInventory().getItem(button);
                if (!hotbar.isEmpty()) {
                    this.trashSlot.setByPlayer(hotbar.copy());
                    player.getInventory().setItem(button, ItemStack.EMPTY);
                }
            }
            return;
        }
        super.clicked(slotId, button, input, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index == 0) {
            return ItemStack.EMPTY;
        }
        Slot source = this.slots.get(index);
        if (!source.hasItem()) {
            return ItemStack.EMPTY;
        }
        this.trashSlot.setByPlayer(source.getItem().copy());
        source.set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.access.evaluate(
                (level, pos) -> level.getBlockState(pos).getBlock() instanceof PortableToiletBlock && player.isWithinBlockInteractionRange(pos, 4.0),
                true
        );
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.trash.clearContent();
        this.trash.stopOpen(player);
    }
}
