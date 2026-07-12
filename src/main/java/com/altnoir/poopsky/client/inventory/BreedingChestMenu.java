package com.altnoir.poopsky.client.inventory;

import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.init.PoMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/**
 * 繁育箱GUI菜单。
 * 布局：
 *   顶部左侧粪便消耗槽 + 两只苍蝇槽（不消耗）
 *   顶部右侧3个输出槽
 *   玩家背包 + 快捷栏
 */
public class BreedingChestMenu extends AbstractContainerMenu {
    // 输入槽
    private static final int FECES_SLOT = 0;
    private static final int FLY_SLOT_1 = 1;
    private static final int FLY_SLOT_2 = 2;
    // 输出槽
    private static final int OUTPUT_SLOT_1 = 3;
    private static final int SLOT_COUNT = 6;
    private static final int INV_SLOT_START = 6;
    private static final int HOTBAR_SLOT_START = 33;
    private static final int HOTBAR_SLOT_END = 42;

    private final Container breedingChest;
    private final ContainerData data;

    public BreedingChestMenu(MenuType<BreedingChestMenu> menuType, int containerId, Inventory playerInventory) {
        this(menuType, containerId, playerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(2));
    }

    public BreedingChestMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        this(PoMenuTypes.BREEDING_CHEST.get(), containerId, playerInventory, container, data);
    }

    private BreedingChestMenu(MenuType<BreedingChestMenu> menuType, int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, containerId);
        checkContainerSize(container, SLOT_COUNT);
        checkContainerDataCount(data, 2);
        this.breedingChest = container;
        this.data = data;
        container.startOpen(playerInventory.player);
        this.addDataSlots(data);

        // 粪便消耗槽
        this.addSlot(new Slot(container, FECES_SLOT, 8, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(PoTags.Items.POOPS);
            }
        });

        // 苍蝇槽1（不消耗）
        this.addSlot(new Slot(container, FLY_SLOT_1, 44, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FlyItem.isFlyItem(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        // 苍蝇槽2（不消耗）
        this.addSlot(new Slot(container, FLY_SLOT_2, 62, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FlyItem.isFlyItem(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        // 3个输出槽
        for (int i = 0; i < 3; i++) {
            final int slotIndex = OUTPUT_SLOT_1 + i;
            this.addSlot(new Slot(container, slotIndex, 116 + i * 18, 20) {
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

            if (index >= FECES_SLOT && index < SLOT_COUNT) {
                // 从繁育箱移出到玩家背包
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, HOTBAR_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.is(PoTags.Items.POOPS)) {
                // 粪便 -> 粪便槽
                if (!this.moveItemStackTo(itemstack1, FECES_SLOT, FECES_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (FlyItem.isFlyItem(itemstack1)) {
                // 苍蝇 -> 苍蝇槽
                if (!this.moveItemStackTo(itemstack1, FLY_SLOT_1, FLY_SLOT_2 + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= INV_SLOT_START && index < HOTBAR_SLOT_START) {
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
        return this.breedingChest.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.breedingChest.stopOpen(player);
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }
}