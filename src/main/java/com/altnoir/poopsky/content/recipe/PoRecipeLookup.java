package com.altnoir.poopsky.content.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public final class PoRecipeLookup {
    private PoRecipeLookup() {
    }

    public static <T extends Recipe<?>> List<RecipeHolder<T>> all(Level level, RecipeType<T> type) {
        if (level.getServer() == null) {
            return List.of();
        }
        return level.getServer().getRecipeManager().getRecipes().stream()
                .filter(holder -> holder.value().getType() == type)
                .map(PoRecipeLookup::<T>cast)
                .toList();
    }

    private static <T extends Recipe<?>> RecipeHolder<T> cast(RecipeHolder<?> holder) {
        return (RecipeHolder<T>) holder;
    }
}
