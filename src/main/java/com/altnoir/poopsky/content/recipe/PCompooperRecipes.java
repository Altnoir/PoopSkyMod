package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public final class PCompooperRecipes {
    private PCompooperRecipes() {
    }

    public static ItemStack getResult(Level level, String fluidType, ItemStack input) {
        if (level == null || input.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return PoRecipeLookup.all(level, PoRecipes.COMPOOPER.type().get())
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.matchesFluid(fluidType) && recipe.matchesInput(input))
                .findFirst()
                .map(recipe -> recipe.output().copy())
                .orElse(ItemStack.EMPTY);
    }
}
