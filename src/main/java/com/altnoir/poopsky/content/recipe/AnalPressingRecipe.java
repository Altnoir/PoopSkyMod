package com.altnoir.poopsky.content.recipe;

import net.minecraft.world.Container;
import net.minecraft.resources.ResourceLocation;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.ByteBufCodecs;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record AnalPressingRecipe(ResourceLocation id, Ingredient input, Block output, Block replaceTarget, int radius) implements Recipe<Container> {
    public AnalPressingRecipe(Ingredient input, Block output, Block replaceTarget, int radius) { this(new ResourceLocation("poopsky", "unregistered"), input, output, replaceTarget, radius); }
    @Override public ResourceLocation getId() { return id; }
    private AnalPressingRecipe withId(ResourceLocation id) { return new AnalPressingRecipe(id, input, output, replaceTarget, radius); }

    @Override
    public boolean matches(Container recipeInput, Level level) {
        return input.test(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(Container recipeInput, net.minecraft.core.RegistryAccess registries) {
        return new ItemStack(output);
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.RegistryAccess registries) {
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
                        PoRecipeCodecs.INGREDIENT.fieldOf("input").forGetter(AnalPressingRecipe::input),
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output").forGetter(AnalPressingRecipe::output),
                        BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("replace_target", Blocks.STONE).forGetter(AnalPressingRecipe::replaceTarget),
                        Codec.INT.optionalFieldOf("radius", 1).forGetter(AnalPressingRecipe::radius)
                ).apply(instance, AnalPressingRecipe::new));

        public static final StreamCodec<FriendlyByteBuf, AnalPressingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.INGREDIENT, AnalPressingRecipe::input,
                        ByteBufCodecs.registry(Registries.BLOCK), AnalPressingRecipe::output,
                        ByteBufCodecs.registry(Registries.BLOCK), AnalPressingRecipe::replaceTarget,
                        ByteBufCodecs.VAR_INT, AnalPressingRecipe::radius,
                        AnalPressingRecipe::new);

        @Override
        public AnalPressingRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
            return CODEC.codec().parse(com.mojang.serialization.JsonOps.INSTANCE, json)
                    .getOrThrow(false, error -> { throw new com.google.gson.JsonSyntaxException(error); }).withId(id);
        }

        @Override
        public AnalPressingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return STREAM_CODEC.decode(buffer).withId(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AnalPressingRecipe recipe) {
            STREAM_CODEC.encode(buffer, recipe);
        }

        public MapCodec<AnalPressingRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<FriendlyByteBuf, AnalPressingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}