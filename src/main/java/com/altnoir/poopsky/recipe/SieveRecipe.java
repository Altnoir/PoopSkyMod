package com.altnoir.poopsky.recipe;

import com.altnoir.poopsky.init.PRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record SieveRecipe(Ingredient input, List<ChanceItemStack> outputs, int processingTime) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return input.test(recipeInput.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.getFirst().stack().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    @Override
    public RecipeType<?> getType() {
        return PRecipes.SIEVE_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PRecipes.SIEVE_SERIALIZER.get();
    }

    public List<ItemStack> rollOutputs(RandomSource random) {
        List<ItemStack> rolledOutputs = new ArrayList<>();
        for (ChanceItemStack entry : outputs) {
            if (entry.roll(random)) {
                rolledOutputs.add(entry.stack().copy());
            }
        }
        return rolledOutputs;
    }

    public record ChanceItemStack(ItemStack stack, float chance) {
        public static final Codec<ChanceItemStack> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ItemStack.STRICT_CODEC.fieldOf("item").forGetter(ChanceItemStack::stack),
                        Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(ChanceItemStack::chance)
                ).apply(instance, ChanceItemStack::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ChanceItemStack> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC, ChanceItemStack::stack,
                        ByteBufCodecs.FLOAT, ChanceItemStack::chance,
                        ChanceItemStack::new);

        public boolean roll(RandomSource random) {
            return chance > 0.0F && random.nextFloat() < chance;
        }
    }

    public static class Serializer implements RecipeSerializer<SieveRecipe> {

        public static final MapCodec<SieveRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SieveRecipe::input),
                        ChanceItemStack.CODEC.listOf().fieldOf("outputs").forGetter(SieveRecipe::outputs),
                        Codec.INT.optionalFieldOf("processingTime", 200).forGetter(SieveRecipe::processingTime)
                ).apply(instance, SieveRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SieveRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, SieveRecipe::input,
                        ChanceItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), SieveRecipe::outputs,
                        ByteBufCodecs.VAR_INT, SieveRecipe::processingTime,
                        SieveRecipe::new);

        @Override
        public MapCodec<SieveRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SieveRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
