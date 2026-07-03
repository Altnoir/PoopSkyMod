package com.altnoir.poopsky.common.recipe;

import com.altnoir.poopsky.common.block.ToiletType;
import com.altnoir.poopsky.init.PComponents;
import com.altnoir.poopsky.init.PRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record ToiletShapedRecipe(ShapedRecipe delegate, ToiletType toiletType) implements CraftingRecipe {

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return delegate.matches(input, level);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = delegate.assemble(input, registries);
        result.set(PComponents.TOILET_TYPE.get(), toiletType);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return delegate.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        ItemStack result = delegate.getResultItem(registries).copy();
        result.set(PComponents.TOILET_TYPE.get(), toiletType);
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return delegate.getIngredients();
    }

    @Override
    public boolean showNotification() {
        return delegate.showNotification();
    }

    @Override
    public String getGroup() {
        return delegate.getGroup();
    }

    @Override
    public CraftingBookCategory category() {
        return delegate.category();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PRecipes.TOILET_SHAPED_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Serializer implements RecipeSerializer<ToiletShapedRecipe> {

        public static final MapCodec<ToiletShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RecipeSerializer.SHAPED_RECIPE.codec().fieldOf("delegate").forGetter(ToiletShapedRecipe::delegate),
                        ToiletType.CODEC.fieldOf("toilet_type").forGetter(ToiletShapedRecipe::toiletType)
                ).apply(instance, ToiletShapedRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ToiletShapedRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public ToiletShapedRecipe decode(RegistryFriendlyByteBuf buf) {
                ShapedRecipe shaped = RecipeSerializer.SHAPED_RECIPE.streamCodec().decode(buf);
                ToiletType type = ToiletType.STREAM_CODEC.decode(buf);
                return new ToiletShapedRecipe(shaped, type);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, ToiletShapedRecipe recipe) {
                RecipeSerializer.SHAPED_RECIPE.streamCodec().encode(buf, recipe.delegate());
                ToiletType.STREAM_CODEC.encode(buf, recipe.toiletType());
            }
        };

        @Override
        public MapCodec<ToiletShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ToiletShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}