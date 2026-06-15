package com.altnoir.poopsky.inventory;

import com.altnoir.poopsky.init.PMenuTypes;
import com.altnoir.poopsky.item.p.FlyItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 苍蝇窝GUI菜单。
 * 布局：
 *   1个输入槽（左中，接苍蝇物品，不消耗）
 *   4个输出槽（右侧2×2）
 *   玩家背包 + 快捷栏
 */
public class FlyNestMenu extends AbstractContainerMenu {
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

    private final Container flyNest;

    // 客户端用构造函数
    public FlyNestMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(SLOT_COUNT));
    }

    // 服务端用构造函数
    public FlyNestMenu(int containerId, Inventory playerInventory, Container container) {
        super(PMenuTypes.FLY_NEST.get(), containerId);
        checkContainerSize(container, SLOT_COUNT);
        this.flyNest = container;
        container.startOpen(playerInventory.player);

        // 输入槽（苍蝇，不消耗）
        this.addSlot(new Slot(container, INPUT_SLOT, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FlyItem.isFlyItem(stack);
            }
        });

        // 4个输出槽（2×2网格）
        this.addSlot(new Slot(container, OUTPUT_SLOT_1, 116, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, OUTPUT_SLOT_2, 134, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, OUTPUT_SLOT_3, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, OUTPUT_SLOT_4, 134, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // 玩家背包
        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        // 玩家快捷栏
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
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
        return this.flyNest.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.flyNest.stopOpen(player);
    }

    public Container getContainer() {
        return flyNest;
    }
}
