package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FlyBarrelRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PoRecipes.FLY_BARREL.folder();

    private final String flyTypeId;
    private final FlyBarrelRecipe.Output result;

    public FlyBarrelRecipeBuilder(String flyTypeId, FlyBarrelRecipe.Output result) {
        this.flyTypeId = flyTypeId;
        this.result = result;
    }

    public static FlyBarrelRecipeBuilder flyBarrel(String flyTypeId, ResourceLocation result) {
        return new FlyBarrelRecipeBuilder(flyTypeId, new FlyBarrelRecipe.Output(result, 1));
    }

    public static FlyBarrelRecipeBuilder flyBarrel(String flyTypeId, ItemStack result) {
        return new FlyBarrelRecipeBuilder(flyTypeId,
                new FlyBarrelRecipe.Output(BuiltInRegistries.ITEM.getKey(result.getItem()), result.getCount()));
    }

    public static FlyBarrelRecipeBuilder flyBarrel(String flyTypeId, ItemLike result) {
        return flyBarrel(flyTypeId, BuiltInRegistries.ITEM.getKey(result.asItem()));
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return BuiltInRegistries.ITEM.get(result.id());
    }

    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        ResourceLocation recipeId = PoopSky.loc(RECIPE_TYPE + "/" + id);
        save(recipeOutput, recipeId);
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        FlyBarrelRecipe recipe = new FlyBarrelRecipe(flyTypeId, result);
        recipeOutput.accept(id, recipe, null);
    }
}
