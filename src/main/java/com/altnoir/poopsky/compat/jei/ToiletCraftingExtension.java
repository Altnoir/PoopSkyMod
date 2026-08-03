package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.content.recipe.ToiletShapedRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class ToiletCraftingExtension implements ICraftingCategoryExtension<ToiletShapedRecipe> {

    @Override
    public int getWidth(RecipeHolder<ToiletShapedRecipe> holder) {
        return holder.value().delegate().getWidth();
    }

    @Override
    public int getHeight(RecipeHolder<ToiletShapedRecipe> holder) {
        return holder.value().delegate().getHeight();
    }

    @Override
    public void setRecipe(RecipeHolder<ToiletShapedRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper helper, IFocusGroup focusGroup) {
        var recipe = holder.value();
        helper.createAndSetIngredients(builder, recipe.getIngredients(), getWidth(holder), getHeight(holder));
        helper.createAndSetOutputs(builder, List.of(recipe.getResultItem(null)));
    }
}