package com.altnoir.poopsky.compat.jei;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;

public record CompooperRecipe(Ingredient input, ItemStack output, BlockState states) {
}