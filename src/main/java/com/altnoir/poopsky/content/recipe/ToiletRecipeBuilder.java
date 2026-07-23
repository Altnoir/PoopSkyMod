package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.content.ToiletType;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
//import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToiletRecipeBuilder implements RecipeBuilder {

    private final ShapedRecipeBuilder delegate;
    private final ToiletType toiletType;

    private ToiletRecipeBuilder(RecipeCategory category, ItemLike toiletBlock, ToiletType toiletType) {
        this.delegate = ShapedRecipeBuilder.shaped(category, toiletBlock);
        this.toiletType = toiletType;
    }

    public static ToiletRecipeBuilder shaped(RecipeCategory category, ItemLike toiletBlock, ToiletType toiletType) {
        return new ToiletRecipeBuilder(category, toiletBlock, toiletType);
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
    public @NotNull Item getResult() {
        return delegate.getResult();
    }

    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        delegate.save(new RecipeOutput() {
            @Override
            public void accept(ResourceLocation recipeId, net.minecraft.world.item.crafting.Recipe<?> recipe, @Nullable net.minecraft.advancements.AdvancementHolder advancement) {
                if (recipe instanceof ShapedRecipe shaped) {
                    recipeOutput.accept(recipeId, new ToiletShapedRecipe(shaped, toiletType), advancement);
                } else {
                    recipeOutput.accept(recipeId, recipe, advancement);
                }
            }
//
//            @Override
//            public void accept(ResourceLocation recipeId, net.minecraft.world.item.crafting.Recipe<?> recipe, @Nullable net.minecraft.advancements.AdvancementHolder advancement, ICondition... conditions) {
//                if (recipe instanceof ShapedRecipe shaped) {
//                    recipeOutput.accept(recipeId, new ToiletShapedRecipe(shaped, toiletType), advancement, conditions);
//                } else {
//                    recipeOutput.accept(recipeId, recipe, advancement, conditions);
//                }
//            }

            @Override
            public Advancement.Builder advancement() {
                return recipeOutput.advancement();
            }
        }, id);
    }
}