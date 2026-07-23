package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class POPExplosionRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PoRecipes.POP_EXPLOSION.folder();

    private final Ingredient input;
    private final POPExplosionRecipe.Output output;
    private int radius = 0;

    public POPExplosionRecipeBuilder(Ingredient input, POPExplosionRecipe.Output output) {
        this.input = input;
        this.output = output;
    }

    public static POPExplosionRecipeBuilder transform(ItemLike input, ItemLike output) {
        if (output instanceof Block block) {
            return transform(Ingredient.of(input), block);
        }
        if (output instanceof Item item) {
            return transform(Ingredient.of(input), item);
        }
        throw new IllegalArgumentException("Output must be a Block or Item, got: " + output.getClass().getSimpleName());
    }

    public static POPExplosionRecipeBuilder transform(Ingredient input, Block output) {
        return new POPExplosionRecipeBuilder(input, new POPExplosionRecipe.Output(output, null));
    }

    public static POPExplosionRecipeBuilder transform(Ingredient input, Item output) {
        return new POPExplosionRecipeBuilder(input, new POPExplosionRecipe.Output(null, output));
    }

    public POPExplosionRecipeBuilder withRadius(int radius) {
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
        return output.toItemStack().getItem();
    }

    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        ResourceLocation recipeId = PoopSky.loc(RECIPE_TYPE + "/" + id);
        save(recipeOutput, recipeId);
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        POPExplosionRecipe recipe = new POPExplosionRecipe(input, radius, output);
        recipeOutput.accept(id, recipe, null);
    }

    public static ResourceLocation getDefaultRecipeId(ItemLike input) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(input.asItem());
        return PoopSky.loc(RECIPE_TYPE + "/" + itemId.getPath());
    }

}
