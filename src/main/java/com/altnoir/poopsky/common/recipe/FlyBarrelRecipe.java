package com.altnoir.poopsky.common.recipe;

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
public record FlyBarrelRecipe(String flyTypeId, ItemStack result) implements Recipe<RecipeInput> {

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
        return PRecipes.FLY_BARREL.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return PRecipes.FLY_BARREL.type().get();
    }

    public static class Serializer implements RecipeSerializer<FlyBarrelRecipe> {
        public static final MapCodec<FlyBarrelRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                net.minecraft.util.ExtraCodecs.NON_EMPTY_STRING.fieldOf("fly_type").forGetter(FlyBarrelRecipe::flyTypeId),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(FlyBarrelRecipe::result)
        ).apply(inst, FlyBarrelRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FlyBarrelRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, FlyBarrelRecipe::flyTypeId,
                        ItemStack.STREAM_CODEC, FlyBarrelRecipe::result,
                        FlyBarrelRecipe::new);

        @Override
        public MapCodec<FlyBarrelRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FlyBarrelRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
