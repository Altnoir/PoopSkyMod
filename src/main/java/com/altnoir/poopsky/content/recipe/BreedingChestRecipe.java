package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 繁育箱变异配方：两个父本苍蝇品种 -> 子代品种 + 概率
 */
public record BreedingChestRecipe(String parent1, String parent2, String result, float chance) implements Recipe<RecipeInput> {

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
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PoRecipes.BREEDING_CHEST.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return PoRecipes.BREEDING_CHEST.type().get();
    }

    public static class Serializer implements RecipeSerializer<BreedingChestRecipe> {
        public static final MapCodec<BreedingChestRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                net.minecraft.util.ExtraCodecs.NON_EMPTY_STRING.fieldOf("parent1").forGetter(BreedingChestRecipe::parent1),
                net.minecraft.util.ExtraCodecs.NON_EMPTY_STRING.fieldOf("parent2").forGetter(BreedingChestRecipe::parent2),
                net.minecraft.util.ExtraCodecs.NON_EMPTY_STRING.fieldOf("result").forGetter(BreedingChestRecipe::result),
                com.mojang.serialization.Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(BreedingChestRecipe::chance)
        ).apply(inst, BreedingChestRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BreedingChestRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, BreedingChestRecipe::parent1,
                        ByteBufCodecs.STRING_UTF8, BreedingChestRecipe::parent2,
                        ByteBufCodecs.STRING_UTF8, BreedingChestRecipe::result,
                        ByteBufCodecs.FLOAT, BreedingChestRecipe::chance,
                        BreedingChestRecipe::new);

        @Override
        public MapCodec<BreedingChestRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BreedingChestRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
