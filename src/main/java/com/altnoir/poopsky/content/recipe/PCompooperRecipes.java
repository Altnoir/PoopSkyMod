package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;

public class PCompooperRecipes {
    public static ItemStack getResult(Level level, String fluidType, ItemStack input) {
        if (level == null) return ItemStack.EMPTY;

        List<RecipeHolder<CompooperRecipe>> recipes = level.recipeAccess().getAllRecipesFor(PoRecipes.COMPOOPER.type().get());

        for (var holder : recipes) {
            CompooperRecipe recipe = holder.value();
            if (recipe.matchesFluid(fluidType) && recipe.matchesInput(input)) {
                return recipe.output().copy();
            }
        }

        return ItemStack.EMPTY;
    }
}
