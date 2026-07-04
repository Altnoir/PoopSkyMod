package com.altnoir.poopsky.common.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BreedingChestRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PRecipes.BREEDING_CHEST.folder();

    private final String parent1;
    private final String parent2;
    private final String result;
    private float chance = 0.2f;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public BreedingChestRecipeBuilder(String parent1, String parent2, String result) {
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.result = result;
    }

    public static BreedingChestRecipeBuilder breedingChest(String parent1, String parent2, String result) {
        return new BreedingChestRecipeBuilder(parent1, parent2, result);
    }

    public BreedingChestRecipeBuilder chance(float chance) {
        this.chance = chance;
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
        return ItemStack.EMPTY.getItem();
    }

    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        ResourceLocation recipeId = PoopSky.loc(RECIPE_TYPE + "/" + id);
        save(recipeOutput, recipeId);
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        ensureValid(id);
        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(advancementBuilder::addCriterion);

        BreedingChestRecipe recipe = new BreedingChestRecipe(parent1, parent2, result, chance);
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}