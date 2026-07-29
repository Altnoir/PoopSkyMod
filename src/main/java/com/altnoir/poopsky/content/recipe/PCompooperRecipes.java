package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;

public class PCompooperRecipes {

    /**
     * Get compooper recipe result by fluid type and input
     *
     * @param level     Level
     * @param fluidType Fluid type (water, lava, powder_snow, urine)
     * @param input     Input item
     * @return Matching output, or ItemStack.EMPTY if no match
     */
    public static ItemStack getResult(Level level, String fluidType, ItemStack input) {
        if (level == null) return ItemStack.EMPTY;

        List<RecipeHolder<CompooperRecipe>> recipes = level.getRecipeManager()
                .getAllRecipesFor(PoRecipes.COMPOOPER.type().get());

        for (var holder : recipes) {
            CompooperRecipe recipe = holder.value();
            if (recipe.matchesFluid(fluidType) && recipe.matchesInput(input)) {
                return recipe.output().copy();
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Check if a matching recipe exists
     */
    public static boolean hasRecipe(Level level, String fluidType, ItemStack input) {
        return !getResult(level, fluidType, input).isEmpty();
    }
}
