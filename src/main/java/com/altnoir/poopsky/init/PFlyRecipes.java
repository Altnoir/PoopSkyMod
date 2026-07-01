package com.altnoir.poopsky.init;

import com.altnoir.poopsky.common.FlyType;
import com.altnoir.poopsky.recipe.BreedingBoxRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

public class PFlyRecipes {

    public record JeiMutationRecipe(FlyType.Type parent1, FlyType.Type parent2, FlyType.Type result, float chance) {}

    public static ItemStack getProduct(Level level, FlyType.Type type) {
        if (level == null) return ItemStack.EMPTY;
        return level.getRecipeManager()
                .getAllRecipesFor(PRecipes.FLY_NEST_TYPE.get())
                .stream()
                .filter(holder -> holder.value().matches(type.id()))
                .findFirst()
                .map(holder -> holder.value().result().copy())
                .orElse(ItemStack.EMPTY);
    }

    public static MutationResult tryMutate(Level level, FlyType.Type parent1, FlyType.Type parent2) {
        if (level == null) return fallbackResult(parent1, parent2);

        List<RecipeHolder<BreedingBoxRecipe>> recipes = level.getRecipeManager()
                .getAllRecipesFor(PRecipes.BREEDING_BOX_TYPE.get());

        Random random = new Random();
        for (var holder : recipes) {
            BreedingBoxRecipe recipe = holder.value();
            if (recipe.matches(parent1.id(), parent2.id())) {
                if (random.nextFloat() < recipe.chance()) {
                return new MutationResult(FlyType.byId(recipe.result()), true);
                }
                return new MutationResult(random.nextBoolean() ? parent1 : parent2, false);
            }
        }

        return fallbackResult(parent1, parent2);
    }

    private static MutationResult fallbackResult(FlyType.Type parent1, FlyType.Type parent2) {
        if (parent1.equals(parent2)) {
            return new MutationResult(parent1, false);
        }
        return new MutationResult(new Random().nextBoolean() ? parent1 : parent2, false);
    }

    public record MutationResult(FlyType.Type result, boolean isMutation) {}
}