package com.altnoir.poopsky.fabric.port.extension;

import com.altnoir.poopsky.fabric.port.util.ItemAbilities;
import com.altnoir.poopsky.fabric.port.util.ItemAbility;
import net.minecraft.world.item.*;

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
        Item item = self();
        if (item instanceof AxeItem) {
            return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);
        }
        if (item instanceof HoeItem) {
            return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility);
        }
        if (item instanceof ShovelItem) {
            return ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility);
        }
        if (item instanceof PickaxeItem) {
            return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility);
        }
        if (item instanceof SwordItem) {
            return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
        }
        if (item instanceof ShearsItem) {
            return ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(itemAbility);
        }
        if (item instanceof ShieldItem) {
            return ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(itemAbility);
        }
        if (item instanceof FishingRodItem) {
            return ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS.contains(itemAbility);
        }
        if (item instanceof TridentItem) {
            return ItemAbilities.DEFAULT_TRIDENT_ACTIONS.contains(itemAbility);
        }
        if (item instanceof BrushItem) {
            return ItemAbilities.DEFAULT_BRUSH_ACTIONS.contains(itemAbility);
        }
        if (item instanceof FlintAndSteelItem) {
            return ItemAbilities.DEFAULT_FLINT_ACTIONS.contains(itemAbility);
        }
        if (stack.is(Items.FIRE_CHARGE)) {
            return ItemAbilities.DEFAULT_FIRECHARGE_ACTIONS.contains(itemAbility);
        }
        if (stack.is(Items.SPYGLASS)) {
            return ItemAbilities.DEFAULT_SPYGLASS_ACTIONS.contains(itemAbility);
        }
        return false;
    }
}
