package com.altnoir.poopsky.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public record RearingChamberRecipe(SizedIngredient ingredient, ItemStack result,
                                   int processingTime) implements Recipe<RearingChamberRecipe.Input> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(ingredient.ingredient());
        return ingredients;
    }

    @Override
    public boolean matches(RearingChamberRecipe.Input input, Level level) {
        if (level.isClientSide) return false;

        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(RearingChamberRecipe.Input input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PSRecipes.REARING_CHAMBER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PSRecipes.REARING_CHAMBER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<RearingChamberRecipe> {
        public static final MapCodec<RearingChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(builder ->
                builder.group(
                        SizedIngredient.FLAT_CODEC.fieldOf("ingredient").forGetter(RearingChamberRecipe::ingredient),
                        ItemStack.CODEC.fieldOf("result").forGetter(RearingChamberRecipe::result),
                        Codec.INT.fieldOf("processingTime").forGetter(RearingChamberRecipe::processingTime)
                ).apply(builder, RearingChamberRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, RearingChamberRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC, RearingChamberRecipe::ingredient,
                ItemStack.STREAM_CODEC, RearingChamberRecipe::result,
                StreamCodec.of(RegistryFriendlyByteBuf::writeInt, RegistryFriendlyByteBuf::readInt), RearingChamberRecipe::processingTime,
                RearingChamberRecipe::new
        );

        @Override
        public MapCodec<RearingChamberRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RearingChamberRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }


    public record Input(ItemStack[] items) implements RecipeInput {
        public Input(ItemStack item) {
            this(new ItemStack[]{item});
        }

        @Override
        public ItemStack getItem(int index) {
            return index >= 0 && index < items.length ? items[index] : ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return items.length;
        }
    }
}