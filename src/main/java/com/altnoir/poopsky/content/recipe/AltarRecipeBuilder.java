package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AltarRecipeBuilder implements RecipeBuilder {
    private static final String RECIPE_TYPE = PoRecipes.ALTAR.folder();

    private final Ingredient input;
    private final ItemStack result;
    private final List<Ingredient> pedestals = new ArrayList<>();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private FluidStack fluid = FluidStack.EMPTY;
    private int duration = 200;
    private String command = "";
    private String description = "";

    private AltarRecipeBuilder(Ingredient input, ItemStack result) {
        this.input = input;
        this.result = result;
    }

    public static AltarRecipeBuilder altar(ItemLike input, ItemLike result) {
        return new AltarRecipeBuilder(Ingredient.of(input), new ItemStack(result));
    }

    public static AltarRecipeBuilder altar(Ingredient input, ItemLike result) {
        return new AltarRecipeBuilder(input, new ItemStack(result));
    }

    public static AltarRecipeBuilder altar(ItemLike input) {
        return new AltarRecipeBuilder(Ingredient.of(input), ItemStack.EMPTY);
    }

    public AltarRecipeBuilder pedestals(ItemLike... items) {
        for (ItemLike item : items) {
            pedestals.add(Ingredient.of(item));
        }
        return this;
    }

    public AltarRecipeBuilder pedestal(Ingredient ingredient) {
        pedestals.add(ingredient);
        return this;
    }

    public AltarRecipeBuilder pedestal(TagKey<Item> tag) {
        pedestals.add(Ingredient.of(tag));
        return this;
    }

    public AltarRecipeBuilder fluid(Fluid fluid, int amount) {
        this.fluid = new FluidStack(fluid, amount);
        return this;
    }

    public AltarRecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public AltarRecipeBuilder command(String command) {
        this.command = command;
        return this;
    }

    public AltarRecipeBuilder description(String description) {
        this.description = description;
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

        AltarRecipe recipe = new AltarRecipe(input, List.copyOf(pedestals), fluid, duration, result.copy(), command, description);
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (pedestals.isEmpty() || pedestals.size() > 8) {
            throw new IllegalStateException("Altar recipe must have 1 to 8 pedestals: " + id);
        }
        if (fluid.isEmpty()) {
            throw new IllegalStateException("Altar recipe has no fluid: " + id);
        }
        if (duration <= 0) {
            throw new IllegalStateException("Altar recipe duration must be positive: " + id);
        }
        if (result.isEmpty() && command.isBlank()) {
            throw new IllegalStateException("Altar recipe has no result or command: " + id);
        }
    }
}
