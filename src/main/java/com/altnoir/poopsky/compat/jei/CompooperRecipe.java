package com.altnoir.poopsky.compat.jei;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record CompooperRecipe(Ingredient input, ItemStack output, ItemStack catalyst) {
}