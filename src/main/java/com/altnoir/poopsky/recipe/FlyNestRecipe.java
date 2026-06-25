package com.altnoir.poopsky.recipe;

import com.altnoir.poopsky.init.PRecipes;
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
 * 苍蝇窝产出配方：fly_type -> 产物 ItemStack
 */
public record FlyNestRecipe(String flyTypeId, ItemStack result) implements Recipe<RecipeInput> {

    public boolean matches(String flyTypeId) {
        return this.flyTypeId.equals(flyTypeId);
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false; // 不使用标准 RecipeInput 匹配
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
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
        return PRecipes.FLY_NEST_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PRecipes.FLY_NEST_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FlyNestRecipe> {
        public static final MapCodec<FlyNestRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                net.minecraft.util.ExtraCodecs.NON_EMPTY_STRING.fieldOf("fly_type").forGetter(FlyNestRecipe::flyTypeId),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(FlyNestRecipe::result)
        ).apply(inst, FlyNestRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FlyNestRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, FlyNestRecipe::flyTypeId,
                        ItemStack.STREAM_CODEC, FlyNestRecipe::result,
                        FlyNestRecipe::new);

        @Override
        public MapCodec<FlyNestRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FlyNestRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
