package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.content.recipe.ToiletShapedRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

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
    public List<SlotDisplay> getIngredients(RecipeHolder<ToiletShapedRecipe> holder) {
        return holder.value().delegate().getIngredients()
                .stream()
                .map(ingredient -> ingredient.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE))
                .toList();
    }

    @Override
    public void setRecipe(RecipeHolder<ToiletShapedRecipe> holder, IRecipeLayoutBuilder builder, ICraftingGridHelper helper, IFocusGroup focusGroup) {
        var recipe = holder.value();
        helper.createAndSetIngredients(builder, recipe.delegate().getIngredients().stream()
                .map(ingredient -> ingredient.orElseGet(() -> Ingredient.of(HolderSet.empty())))
                .toList(), getWidth(holder), getHeight(holder));
        var input = recipe.delegate().getIngredients().stream()
                .map(ingredient -> ingredient.flatMap(value -> value.items().findFirst())
                        .map(ItemStack::new)
                        .orElse(ItemStack.EMPTY))
                .toList();
        helper.createAndSetOutputs(builder, List.of(recipe.assemble(CraftingInput.of(getWidth(holder), getHeight(holder), input))));
    }
}
