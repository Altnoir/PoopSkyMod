package com.altnoir.poopsky.compat.jei;

import net.minecraft.world.item.ItemStack;

public record BreedingBoxJeiRecipe(ItemStack flyInput1, ItemStack flyInput2, ItemStack fecesInput, ItemStack resultFly, ItemStack fallbackFly1, ItemStack fallbackFly2, float chance) {}
