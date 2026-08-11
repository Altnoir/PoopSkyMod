package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
    public static final MapCodec<SieveRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("input").forGetter(SieveRecipe::input),
                    ChanceItemStack.CODEC.listOf().fieldOf("outputs").forGetter(SieveRecipe::outputs),
                    Codec.INT.optionalFieldOf("processingTime", 200).forGetter(SieveRecipe::processingTime)
            ).apply(instance, SieveRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SieveRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, SieveRecipe::input,
                    ChanceItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), SieveRecipe::outputs,
                    ByteBufCodecs.VAR_INT, SieveRecipe::processingTime,
                    SieveRecipe::new);

    public static final RecipeSerializer<SieveRecipe> SERIALIZER =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return input.test(recipeInput.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeType<SieveRecipe> getType() {
        return PoRecipes.SIEVE.type().get();
    }

    @Override
    public RecipeSerializer<SieveRecipe> getSerializer() {
        return PoRecipes.SIEVE.serializer().get();
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
                        ItemStack.CODEC.fieldOf("item").forGetter(ChanceItemStack::stack),
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
}
