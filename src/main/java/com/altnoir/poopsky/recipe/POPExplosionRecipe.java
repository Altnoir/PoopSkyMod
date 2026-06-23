package com.altnoir.poopsky.recipe;

import com.altnoir.poopsky.init.PRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record POPExplosionRecipe(Ingredient input, Block output) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return input.test(recipeInput.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider registries) {
        return new ItemStack(output.asItem());
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(output.asItem());
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
        return PRecipes.EXPLOSION_TRANSFORM_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PRecipes.EXPLOSION_TRANSFORM_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<POPExplosionRecipe> {

        public static final MapCodec<POPExplosionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(POPExplosionRecipe::input),
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output").forGetter(POPExplosionRecipe::output)
                ).apply(instance, POPExplosionRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, POPExplosionRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, POPExplosionRecipe::input,
                        ByteBufCodecs.registry(Registries.BLOCK), POPExplosionRecipe::output,
                        POPExplosionRecipe::new);

        @Override
        public MapCodec<POPExplosionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, POPExplosionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}