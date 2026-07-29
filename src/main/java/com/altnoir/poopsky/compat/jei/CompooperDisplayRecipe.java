package com.altnoir.poopsky.compat.jei;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Manual compooper recipe for JEI display only (not a real recipe)
 */
public record CompooperDisplayRecipe(ItemStack input, ItemStack output, BlockState compooperState) {
}
