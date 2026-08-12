package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class FlyBarrelRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PoRecipes.FLY_BARREL.folder();

    private final String flyTypeId;
    private final FlyBarrelRecipe.Output result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public FlyBarrelRecipeBuilder(String flyTypeId, FlyBarrelRecipe.Output result) {
        this.flyTypeId = flyTypeId;
        this.result = result;
    }

    public static FlyBarrelRecipeBuilder flyBarrel(String flyTypeId, Identifier result) {
        return new FlyBarrelRecipeBuilder(flyTypeId, new FlyBarrelRecipe.Output(result, 1));
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
        return ResourceKey.create(Registries.RECIPE, PoopSky.loc(RECIPE_TYPE + "/" + flyTypeId));
    }

    @Override
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

        FlyBarrelRecipe recipe = new FlyBarrelRecipe(flyTypeId, result);
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.identifier().withPrefix("recipes/")));
    }

    private void ensureValid(Identifier id) {
        if (criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}
