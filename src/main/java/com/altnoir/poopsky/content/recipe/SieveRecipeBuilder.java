package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class SieveRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PoRecipes.SIEVE.folder();

    private final Ingredient input;
    private final int processingTime;
    private final List<SieveRecipe.ChanceItemStack> outputs = new ArrayList<>();

    public SieveRecipeBuilder(Ingredient input, int processingTime) {
        this.input = input;
        this.processingTime = processingTime;
    }

    public static SieveRecipeBuilder sieve(ItemLike input, int processingTime) {
        return new SieveRecipeBuilder(Ingredient.of(input), processingTime);
    }

    public static SieveRecipeBuilder sieve(Ingredient input, int processingTime) {
        return new SieveRecipeBuilder(input, processingTime);
    }

    public static SieveRecipeBuilder sieve(TagKey<Item> tag, int processingTime) {
        return new SieveRecipeBuilder(Ingredient.of(tag), processingTime);
    }

    public SieveRecipeBuilder addOutput(ItemLike item) {
        return addOutput(item, 1);
    }

    public SieveRecipeBuilder addOutput(ItemLike item, float chance) {
        return addOutput(item, 1, chance);
    }

    public SieveRecipeBuilder addOutput(ItemLike item, int count) {
        return addOutput(item, count, 1.0F);
    }

    public SieveRecipeBuilder addOutput(ItemLike item, int count, float chance) {
        this.outputs.add(new SieveRecipe.ChanceItemStack(new ItemStack(item, count), chance));
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
        if (outputs.isEmpty()) {
            throw new IllegalStateException("Sieve recipe has no outputs");
        }
        return outputs.getFirst().stack().getItem();
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput) {
        ItemStack[] items = input.getItems();
        if (items.length > 0) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(items[0].getItem());
            save(recipeOutput, itemId.getPath());
        } else {
            RecipeBuilder.super.save(recipeOutput);
        }
    }

    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        ResourceLocation recipeId = PoopSky.loc(RECIPE_TYPE + "/" + id);
        save(recipeOutput, recipeId);
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        ensureValid(id);
        SieveRecipe recipe = new SieveRecipe(input, List.copyOf(outputs), processingTime);
        recipeOutput.accept(id, recipe, null);
    }

    public static ResourceLocation getDefaultRecipeId(ItemLike input) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(input.asItem());
        return PoopSky.loc(RECIPE_TYPE + "/" + itemId.getPath());
    }

    private void ensureValid(ResourceLocation id) {
        if (outputs.isEmpty()) {
            throw new IllegalStateException("No outputs defined for recipe " + id);
        }
        if (processingTime <= 0) {
            throw new IllegalStateException("Processing time must be positive for recipe " + id);
        }
    }
}
