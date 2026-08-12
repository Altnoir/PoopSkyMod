package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * 苍蝇窝产出配方：fly_type -> 产物 ItemStack
 */
public record FlyBarrelRecipe(String flyTypeId, Output result) implements Recipe<RecipeInput> {
    public static final MapCodec<FlyBarrelRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("fly_type").forGetter(FlyBarrelRecipe::flyTypeId),
            Output.CODEC.fieldOf("result").forGetter(FlyBarrelRecipe::result)
    ).apply(inst, FlyBarrelRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlyBarrelRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, FlyBarrelRecipe::flyTypeId,
                    Output.STREAM_CODEC, FlyBarrelRecipe::result,
                    FlyBarrelRecipe::new);

    public static final RecipeSerializer<FlyBarrelRecipe> SERIALIZER =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public boolean matches(String flyTypeId) {
        return this.flyTypeId.equals(flyTypeId);
    }

    public ItemStack resultStack() {
        return result.toStack();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false; // 不使用标准 RecipeInput 匹配
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
        return resultStack();
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
    public RecipeSerializer<FlyBarrelRecipe> getSerializer() {
        return PoRecipes.FLY_BARREL.serializer().get();
    }

    @Override
    public RecipeType<FlyBarrelRecipe> getType() {
        return PoRecipes.FLY_BARREL.type().get();
    }

    public record Output(Identifier id, int count) {
        public static final Codec<Output> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Identifier.CODEC.fieldOf("id").forGetter(Output::id),
                ExtraCodecs.intRange(1, 99).fieldOf("count").forGetter(Output::count)
        ).apply(inst, Output::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Output> STREAM_CODEC =
                StreamCodec.composite(
                        Identifier.STREAM_CODEC, Output::id,
                        ByteBufCodecs.VAR_INT, Output::count,
                        Output::new);

        public ItemStack toStack() {
            return new ItemStack(BuiltInRegistries.ITEM.getValue(id), count);
        }
    }

}
