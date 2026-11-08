package com.altnoir.poopsky.impl.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;

/** 保持普通、喷溅及滞留药水容器和自定义 NBT，仅替换药水类型。 */
public final class PoBrewing {
    private PoBrewing() {}
    public static void addMix(Potion from, Item reagent, Potion to) {
        BrewingRecipeRegistry.addRecipe(new IBrewingRecipe() {
            @Override public boolean isInput(ItemStack input) {
                return (input.is(Items.POTION) || input.is(Items.SPLASH_POTION) || input.is(Items.LINGERING_POTION)) && PotionUtils.getPotion(input) == from;
            }
            @Override public boolean isIngredient(ItemStack ingredient) { return ingredient.is(reagent); }
            @Override public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
                return isInput(input) && isIngredient(ingredient) ? PotionUtils.setPotion(input.copyWithCount(1), to) : ItemStack.EMPTY;
            }
        });
    }
}
