package com.altnoir.poopsky.content.recipe;

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
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class FlyBarrelRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PRecipes.FLY_BARREL.folder();

    private final String flyTypeId;
    private final ItemStack result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public FlyBarrelRecipeBuilder(String flyTypeId, ItemStack result) {
        this.flyTypeId = flyTypeId;
        this.result = result;
    }

    public static FlyBarrelRecipeBuilder flyBarrel(String flyTypeId, ItemStack result) {
        return new FlyBarrelRecipeBuilder(flyTypeId, result);
    }

    public static FlyBarrelRecipeBuilder flyBarrel(String flyTypeId, ItemLike result) {
        return new FlyBarrelRecipeBuilder(flyTypeId, new ItemStack(result));
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
        return result.getItem();
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

        FlyBarrelRecipe recipe = new FlyBarrelRecipe(flyTypeId, result.copy());
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}