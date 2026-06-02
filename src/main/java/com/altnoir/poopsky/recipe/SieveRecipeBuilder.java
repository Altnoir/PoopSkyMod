package com.altnoir.poopsky.recipe;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SieveRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PSRecipes.SIEVE_RECIPE_FOLDER;

    private final Ingredient input;
    private final int processingTime;
    private final List<SieveRecipe.ChanceItemStack> outputs = new ArrayList<>();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

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

    public SieveRecipeBuilder addOutput(ItemLike item) {
        return addOutput(item, 1);
    }

    public SieveRecipeBuilder addOutput(ItemLike item, float chance) {
        return addOutput(item,1, chance);
    }

    public SieveRecipeBuilder addOutput(ItemLike item, int count) {
        return addOutput(item, count,1.0F);
    }

    public SieveRecipeBuilder addOutput(ItemLike item,int count, float chance) {
        this.outputs.add(new SieveRecipe.ChanceItemStack(new ItemStack(item, count), chance));
        return this;
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
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

    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        ResourceLocation recipeId = PoopSky.loc(RECIPE_TYPE + "/" + id);
        save(recipeOutput, recipeId);
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        ensureValid(id);
        ResourceLocation advancementId = PoopSky.loc(id.getPath());

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        criteria.forEach(advancementBuilder::addCriterion);

        SieveRecipe recipe = new SieveRecipe(input, List.copyOf(outputs), processingTime);
        recipeOutput.accept(id, recipe, advancementBuilder.build(advancementId.withPrefix("recipes/")));
    }

    public static ResourceLocation getDefaultRecipeId(ItemLike input) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(input.asItem());
        return PoopSky.loc(RECIPE_TYPE + "/" + itemId.getPath());
    }

    private void ensureValid(ResourceLocation id) {
        if (criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
        if (outputs.isEmpty()) {
            throw new IllegalStateException("No outputs defined for recipe " + id);
        }
        if (processingTime <= 0) {
            throw new IllegalStateException("Processing time must be positive for recipe " + id);
        }
    }
}
