package com.altnoir.poopsky.fabric.port.extension;

import com.altnoir.poopsky.fabric.port.util.ItemAbility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IItemExtension {
    private Item self() {
        return (Item) this;
    }

    /**
     * Queries if an item can perform the given action.
     * See {@link com.altnoir.poopsky.fabric.port.util.ItemAbilities} for a description of each stock action
     *
     * @param stack       The stack being used
     * @param itemAbility The action being queried
     * @return True if the stack can perform the action
     */
    default boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return false;
    }
}
