package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record ToiletShapedRecipe(ShapedRecipe delegate, ToiletType toiletType) implements CraftingRecipe {
    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.delegate.matches(input, level);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack result = this.delegate.assemble(input);
        result.set(PoComponents.TOILET_TYPE.get(), this.toiletType);
        return result;
    }

    @Override
    public boolean showNotification() {
        return this.delegate.showNotification();
    }

    @Override
    public String group() {
        return this.delegate.group();
    }

    @Override
    public PlacementInfo placementInfo() {
        return this.delegate.placementInfo();
    }

    @Override
    public CraftingBookCategory category() {
        return this.delegate.category();
    }

    @Override
    public RecipeSerializer<ToiletShapedRecipe> getSerializer() {
        return PoRecipes.TOILET_SHAPED_SERIALIZER.get();
    }

    public static final MapCodec<ToiletShapedRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ShapedRecipe.MAP_CODEC.fieldOf("delegate").forGetter(ToiletShapedRecipe::delegate),
                    ToiletType.CODEC.fieldOf("toilet_type").forGetter(ToiletShapedRecipe::toiletType)
            ).apply(instance, ToiletShapedRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ToiletShapedRecipe> STREAM_CODEC = StreamCodec.composite(
            ShapedRecipe.STREAM_CODEC,
            ToiletShapedRecipe::delegate,
            ToiletType.STREAM_CODEC,
            ToiletShapedRecipe::toiletType,
            ToiletShapedRecipe::new
    );

    public static final RecipeSerializer<ToiletShapedRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
}
