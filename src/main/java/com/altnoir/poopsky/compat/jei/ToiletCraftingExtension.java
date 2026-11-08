package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.content.recipe.ToiletShapedRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

public class ToiletCraftingExtension implements ICraftingCategoryExtension {
    private final ToiletShapedRecipe recipe;
    public ToiletCraftingExtension(ToiletShapedRecipe recipe) { this.recipe = recipe; }
    @Override public int getWidth() { return recipe.delegate().getWidth(); }
    @Override public int getHeight() { return recipe.delegate().getHeight(); }
    @Override public ResourceLocation getRegistryName() { return recipe.getId(); }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper helper, IFocusGroup focusGroup) {
        helper.createAndSetIngredients(builder, recipe.getIngredients(), getWidth(), getHeight());
        helper.createAndSetOutputs(builder, List.of(recipe.getResultItem(null)));
    }
}
