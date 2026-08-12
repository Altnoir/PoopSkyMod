package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * 繁育箱变异配方：两个父本苍蝇品种 -> 子代品种 + 概率
 */
public record BreedingChestRecipe(String parent1, String parent2, String result, float chance) implements Recipe<RecipeInput> {
    public static final MapCodec<BreedingChestRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("parent1").forGetter(BreedingChestRecipe::parent1),
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("parent2").forGetter(BreedingChestRecipe::parent2),
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("result").forGetter(BreedingChestRecipe::result),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(BreedingChestRecipe::chance)
    ).apply(inst, BreedingChestRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BreedingChestRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, BreedingChestRecipe::parent1,
                    ByteBufCodecs.STRING_UTF8, BreedingChestRecipe::parent2,
                    ByteBufCodecs.STRING_UTF8, BreedingChestRecipe::result,
                    ByteBufCodecs.FLOAT, BreedingChestRecipe::chance,
                    BreedingChestRecipe::new);

    public static final RecipeSerializer<BreedingChestRecipe> SERIALIZER =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    /**
     * 双向匹配：parent1+parent2 或 parent2+parent1。
     */
    public boolean matches(String p1, String p2) {
        return (parent1.equals(p1) && parent2.equals(p2))
                || (parent1.equals(p2) && parent2.equals(p1));
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false; // 不使用标准 RecipeInput 匹配
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
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
    public RecipeSerializer<BreedingChestRecipe> getSerializer() {
        return PoRecipes.BREEDING_CHEST.serializer().get();
    }

    @Override
    public RecipeType<BreedingChestRecipe> getType() {
        return PoRecipes.BREEDING_CHEST.type().get();
    }
}
