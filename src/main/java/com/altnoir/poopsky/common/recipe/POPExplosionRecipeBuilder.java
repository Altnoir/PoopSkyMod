package com.altnoir.poopsky.common.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PRecipes;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class POPExplosionRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PRecipes.POP_EXPLOSION.folder();

    private final Ingredient input;
    private final POPExplosionRecipe.Output output;
    private int radius = 0;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

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
        this.criteria.put(name, criterion);
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
        ensureValid(id);
        ResourceLocation advancementId = PoopSky.loc(id.getPath());

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        criteria.forEach(advancementBuilder::addCriterion);

        POPExplosionRecipe recipe = new POPExplosionRecipe(input, radius, output);
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
    }
}