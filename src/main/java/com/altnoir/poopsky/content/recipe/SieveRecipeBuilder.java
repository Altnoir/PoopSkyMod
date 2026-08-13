package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SieveRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PoRecipes.SIEVE.folder();

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

    public static SieveRecipeBuilder sieve(HolderGetter<Item> items, TagKey<Item> tag, int processingTime) {
        return new SieveRecipeBuilder(Ingredient.of(items.getOrThrow(tag)), processingTime);
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
        this.outputs.add(new SieveRecipe.ChanceItemStack(new ItemStackTemplate(item.asItem(), count), chance));
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
    public ResourceKey<Recipe<?>> defaultId() {
        Item item = input.getValues()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Sieve recipe has no input items"))
                .value();
        return ResourceKey.create(Registries.RECIPE, getDefaultRecipeId(item));
    }

    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        Identifier recipeId = PoopSky.loc(RECIPE_TYPE + "/" + id);
        save(recipeOutput, ResourceKey.create(Registries.RECIPE, recipeId));
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> id) {
        ensureValid(id.identifier());

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        criteria.forEach(advancementBuilder::addCriterion);

        SieveRecipe recipe = new SieveRecipe(input, List.copyOf(outputs), processingTime);
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.identifier().withPrefix("recipes/")));
    }

    public static Identifier getDefaultRecipeId(ItemLike input) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(input.asItem());
        return PoopSky.loc(RECIPE_TYPE + "/" + itemId.getPath());
    }

    private void ensureValid(Identifier id) {
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
