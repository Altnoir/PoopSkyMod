package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AnalPressingRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PoRecipes.ANAL_PRESSING.folder();

    private final Ingredient input;
    private final Block output;
    private Block replaceTarget = Blocks.STONE;
    private int radius = 1;

    public AnalPressingRecipeBuilder(Ingredient input, Block output) {
        this.input = input;
        this.output = output;
    }

    public static AnalPressingRecipeBuilder analPressing(ItemLike input, Block output) {
        return new AnalPressingRecipeBuilder(Ingredient.of(input), output);
    }

    public AnalPressingRecipeBuilder replaceTarget(Block replaceTarget) {
        this.replaceTarget = replaceTarget;
        return this;
    }

    public AnalPressingRecipeBuilder radius(int radius) {
        this.radius = radius;
        return this;
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
        return output.asItem();
    }

    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        ResourceLocation recipeId = PoopSky.loc(RECIPE_TYPE + "/" + id);
        save(recipeOutput, recipeId);
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        AnalPressingRecipe recipe = new AnalPressingRecipe(input, output, replaceTarget, radius);
        recipeOutput.accept(id, recipe, null);
    }
}
