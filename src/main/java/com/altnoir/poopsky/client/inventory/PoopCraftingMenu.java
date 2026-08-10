package com.altnoir.poopsky.client.inventory;

import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class PoopCraftingMenu extends CraftingMenu {
    private final ContainerLevelAccess access;
    private boolean consumed;

    public PoopCraftingMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(containerId, playerInventory, access);
        this.access = access;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, PoBlocks.POOP_CRAFTING_TABLE.get());
    }

    public void consumeCraftingTable() {
        if (consumed) {
            return;
        }
        consumed = true;
        this.access.execute((level, pos) -> {
            if (level.getBlockState(pos).is(PoBlocks.POOP_CRAFTING_TABLE.get())) {
                level.destroyBlock(pos, false);
            }
        });
    }
}
