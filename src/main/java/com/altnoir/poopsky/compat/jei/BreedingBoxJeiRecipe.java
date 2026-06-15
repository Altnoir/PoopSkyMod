package com.altnoir.poopsky.compat.jei;

import net.minecraft.world.item.ItemStack;

public record BreedingBoxJeiRecipe(ItemStack flyInput1, ItemStack flyInput2, ItemStack fecesInput, ItemStack resultFly, float chance) {}
