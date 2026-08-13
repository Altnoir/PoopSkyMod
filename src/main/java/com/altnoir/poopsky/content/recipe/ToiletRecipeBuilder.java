package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.content.ToiletType;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToiletRecipeBuilder implements RecipeBuilder {

    private final ShapedRecipeBuilder delegate;
    private final ToiletType toiletType;

    private ToiletRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemLike toiletBlock, ToiletType toiletType) {
        this.delegate = ShapedRecipeBuilder.shaped(items, category, toiletBlock);
        this.toiletType = toiletType;
    }

    public static ToiletRecipeBuilder shaped(HolderGetter<Item> items, RecipeCategory category, ItemLike toiletBlock, ToiletType toiletType) {
        return new ToiletRecipeBuilder(items, category, toiletBlock, toiletType);
    }

    public ToiletRecipeBuilder pattern(String pattern) {
        delegate.pattern(pattern);
        return this;
    }

    public ToiletRecipeBuilder define(char symbol, ItemLike item) {
        delegate.define(symbol, item);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        delegate.unlockedBy(name, criterion);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String group) {
        delegate.group(group);
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return delegate.defaultId();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
        delegate.save(new RecipeOutput() {
            @Override
            public void accept(ResourceKey<Recipe<?>> recipeId, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
                if (recipe instanceof ShapedRecipe shaped) {
                    recipeOutput.accept(recipeId, new ToiletShapedRecipe(shaped, toiletType), advancement);
                } else {
                    recipeOutput.accept(recipeId, recipe, advancement);
                }
            }

            @Override
            public void accept(ResourceKey<Recipe<?>> recipeId, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
                if (recipe instanceof ShapedRecipe shaped) {
                    recipeOutput.accept(recipeId, new ToiletShapedRecipe(shaped, toiletType), advancement, conditions);
                } else {
                    recipeOutput.accept(recipeId, recipe, advancement, conditions);
                }
            }

            @Override
            public Advancement.Builder advancement() {
                return recipeOutput.advancement();
            }

            @Override
            public void includeRootAdvancement() {
                recipeOutput.includeRootAdvancement();
            }
        }, id);
    }
}
