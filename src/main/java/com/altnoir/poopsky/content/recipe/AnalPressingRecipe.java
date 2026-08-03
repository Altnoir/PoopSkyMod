package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Blocks;

public record AnalPressingRecipe(Ingredient input, Block output, Block replaceTarget, int radius) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return input.test(recipeInput.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider registries) {
        return new ItemStack(output);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(output);
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
        return PoRecipes.ANAL_PRESSING.type().get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PoRecipes.ANAL_PRESSING.serializer().get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public void applyConversion(Level level, BlockPos centerPos) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos targetPos = centerPos.offset(dx, 0, dz);
                if (level.getBlockState(targetPos).is(replaceTarget)) {
                    level.setBlockAndUpdate(targetPos, output.defaultBlockState());
                }
            }
        }
    }

    public static class Serializer implements RecipeSerializer<AnalPressingRecipe> {

        public static final MapCodec<AnalPressingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(AnalPressingRecipe::input),
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output").forGetter(AnalPressingRecipe::output),
                        BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("replace_target", Blocks.STONE).forGetter(AnalPressingRecipe::replaceTarget),
                        Codec.INT.optionalFieldOf("radius", 1).forGetter(AnalPressingRecipe::radius)
                ).apply(instance, AnalPressingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AnalPressingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, AnalPressingRecipe::input,
                        ByteBufCodecs.registry(Registries.BLOCK), AnalPressingRecipe::output,
                        ByteBufCodecs.registry(Registries.BLOCK), AnalPressingRecipe::replaceTarget,
                        ByteBufCodecs.VAR_INT, AnalPressingRecipe::radius,
                        AnalPressingRecipe::new);

        @Override
        public MapCodec<AnalPressingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AnalPressingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
