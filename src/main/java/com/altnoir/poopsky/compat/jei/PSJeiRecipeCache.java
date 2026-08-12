package com.altnoir.poopsky.compat.jei;

import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.List;

public final class PSJeiRecipeCache {
    private static RecipeMap recipes = RecipeMap.EMPTY;

    private PSJeiRecipeCache() {
    }

    public static void update(RecipesReceivedEvent event) {
        recipes = event.getRecipeMap();
    }

    public static <I extends RecipeInput, R extends Recipe<I>> List<RecipeHolder<R>> getAll(RecipeType<R> type) {
        return List.copyOf(recipes.byType(type));
    }
}
