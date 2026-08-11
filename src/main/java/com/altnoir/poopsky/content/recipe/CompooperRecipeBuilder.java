package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CompooperRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PoRecipes.COMPOOPER.folder();

    private final String fluidType;
    private final ItemStack input;
    private final ItemStack output;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public CompooperRecipeBuilder(String fluidType, ItemStack input, ItemStack output) {
        this.fluidType = fluidType;
        this.input = input;
        this.output = output;
    }

    public static CompooperRecipeBuilder compooper(String fluidType, ItemLike input, ItemLike output) {
        return new CompooperRecipeBuilder(fluidType, new ItemStack(input), new ItemStack(output));
    }

    public static CompooperRecipeBuilder compooper(String fluidType, ItemLike input, ItemLike output, int count) {
        return new CompooperRecipeBuilder(fluidType, new ItemStack(input), new ItemStack(output, count));
    }

    public static CompooperRecipeBuilder compooper(String fluidType, ItemStack input, ItemStack output) {
        return new CompooperRecipeBuilder(fluidType, input, output);
    }

    public static CompooperRecipeBuilder compooper(String fluidType, ItemLike input, ItemStack output) {
        return new CompooperRecipeBuilder(fluidType, new ItemStack(input), output);
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
        return output.getItem();
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput) {
        String itemId = PoopSky.getItemPath(output.getItem());
        save(recipeOutput, itemId);
    }

    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        Identifier recipeId = PoopSky.loc(RECIPE_TYPE + "/" + fluidType + "/" + id);
        save(recipeOutput, recipeId);
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull Identifier id) {
        ensureValid(id);
        Identifier advancementId = PoopSky.loc(id.getPath());

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        criteria.forEach(advancementBuilder::addCriterion);

        CompooperRecipe recipe = new CompooperRecipe(fluidType, input, output);
        recipeOutput.accept(id, recipe, advancementBuilder.build(advancementId.withPrefix("recipes/")));
    }

    public static Identifier getDefaultRecipeId(String fluidType, ItemLike input) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(input.asItem());
        return PoopSky.loc(RECIPE_TYPE + "/" + fluidType + "/" + itemId.getPath());
    }

    private void ensureValid(Identifier id) {
        if (criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
        if (fluidType == null || fluidType.isEmpty()) {
            throw new IllegalStateException("No fluid type defined for recipe " + id);
        }
    }
}
