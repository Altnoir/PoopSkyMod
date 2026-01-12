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
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class RearingChamberRecipeBuilder implements RecipeBuilder {
    private final SizedIngredient ingredient;
    private final ItemStack result;
    private final int processingTime;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private static final String RECIPE_TYPE = PSRecipes.REARING_CHAMBER_TYPE.getId().getPath();

    public RearingChamberRecipeBuilder(SizedIngredient ingredient, ItemStack result, int processingTime) {
        this.ingredient = ingredient;
        this.result = result;
        this.processingTime = processingTime;
    }

    @Override
    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        this.criteria.put(s, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String s) {
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    public void save(@NotNull RecipeOutput recipeOutput) {
        this.save(recipeOutput, getDefaultRecipeId(this.getResult()));
    }

    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        ResourceLocation resourceLocation = getDefaultRecipeId(this.getResult());
        ResourceLocation resourceLocation1 = ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, RECIPE_TYPE + "/" + id);
        if (resourceLocation1.equals(resourceLocation)) {
            throw new IllegalStateException(
                    "Recipe " + id + " should remove its 'save' argument as it is equal to default one");
        } else {
            this.save(recipeOutput, resourceLocation1);
        }
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        ensureValid(id);
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, id.getPath());

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        criteria.forEach(advancementBuilder::addCriterion);

        RearingChamberRecipe recipe = new RearingChamberRecipe(ingredient, result, processingTime);
        recipeOutput.accept(id, recipe, advancementBuilder.build(advancementId.withPrefix("recipes/")));
    }

    public static ResourceLocation getDefaultRecipeId(ItemLike itemLike) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(itemLike.asItem());
        return ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, RECIPE_TYPE + "/" + itemId.getPath());
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}