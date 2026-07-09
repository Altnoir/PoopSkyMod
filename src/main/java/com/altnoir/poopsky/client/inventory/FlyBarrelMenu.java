package com.altnoir.poopsky.client.inventory;

import com.altnoir.poopsky.init.PMenuTypes;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 苍蝇窝GUI菜单。
 * 布局：
 *   顶部左侧1个输入槽（接苍蝇物品，不消耗）
 *   顶部右侧4个输出槽
 *   玩家背包 + 快捷栏
 */
public class FlyBarrelMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT_1 = 1;
    private static final int OUTPUT_SLOT_2 = 2;
    private static final int OUTPUT_SLOT_3 = 3;
    private static final int OUTPUT_SLOT_4 = 4;
    private static final int SLOT_COUNT = 5;
    private static final int INV_SLOT_START = 5;
    private static final int INV_SLOT_END = 32;
    private static final int HOTBAR_SLOT_START = 32;
    private static final int HOTBAR_SLOT_END = 41;

    private final Container flyBarrel;
    private final ContainerData data;

    // 客户端用构造函数
    public FlyBarrelMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(2));
    }

    // 服务端用构造函数
    public FlyBarrelMenu(int containerId, Inventory playerInventory, Container container) {
        this(containerId, playerInventory, container, new SimpleContainerData(2));
    }

    public FlyBarrelMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(PMenuTypes.FLY_BARREL.get(), containerId);
        checkContainerSize(container, SLOT_COUNT);
        checkContainerDataCount(data, 2);
        this.flyBarrel = container;
        this.data = data;
        container.startOpen(playerInventory.player);
        this.addDataSlots(data);

        // 输入槽（苍蝇，不消耗）
        this.addSlot(new Slot(container, INPUT_SLOT, 26, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FlyItem.isFlyItem(stack);
            }
        });

        // 4个输出槽
        for (int i = 0; i < 4; i++) {
            final int slotIndex = OUTPUT_SLOT_1 + i;
            this.addSlot(new Slot(container, slotIndex, 80 + i * 18, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        // 玩家背包
        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 51 + k * 18));
            }
        }

        // 玩家快捷栏
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

            if (index >= INPUT_SLOT && index < SLOT_COUNT) {
                // 从苍蝇窝移出到玩家背包
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, HOTBAR_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (FlyItem.isFlyItem(itemstack1)) {
                // 从玩家背包移到输入槽
                if (!this.moveItemStackTo(itemstack1, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= INV_SLOT_START && index < HOTBAR_SLOT_START) {
                // 背包间移动
                if (!this.moveItemStackTo(itemstack1, HOTBAR_SLOT_START, HOTBAR_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= HOTBAR_SLOT_START && index < HOTBAR_SLOT_END) {
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, HOTBAR_SLOT_START, false)) {
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
        return this.flyBarrel.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.flyBarrel.stopOpen(player);
    }

    public Container getContainer() {
        return flyBarrel;
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }
}
