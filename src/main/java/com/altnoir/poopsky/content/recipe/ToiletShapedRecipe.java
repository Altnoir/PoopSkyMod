package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;


public class ToiletShapedRecipe extends ShapedRecipe {
    private final ShapedRecipe delegate;
    private final ToiletType toiletType;

    public ToiletShapedRecipe(ShapedRecipe delegate, ToiletType toiletType) {
        super(
                delegate.getGroup(),
                delegate.category(),
                delegate.pattern,
                delegate.getResultItem(null).copy(),
                delegate.showNotification()
        );
        this.delegate = delegate;
        this.toiletType = toiletType;
    }

    public ShapedRecipe delegate() {
        return delegate;
    }

    public ToiletType toiletType() {
        return toiletType;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = super.assemble(input, registries);
        result.set(PoComponents.TOILET_TYPE.get(), toiletType);
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        ItemStack result = super.getResultItem(registries).copy();
        result.set(PoComponents.TOILET_TYPE.get(), toiletType);
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PoRecipes.TOILET_SHAPED_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static final MapCodec<ToiletShapedRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    RecipeSerializer.SHAPED_RECIPE.codec().fieldOf("delegate").forGetter(ToiletShapedRecipe::delegate),
                    ToiletType.CODEC.fieldOf("toilet_type").forGetter(ToiletShapedRecipe::toiletType)
            ).apply(instance, ToiletShapedRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ToiletShapedRecipe> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ToiletShapedRecipe decode(RegistryFriendlyByteBuf buf) {
            ShapedRecipe shaped = RecipeSerializer.SHAPED_RECIPE.streamCodec().decode(buf);
            ToiletType type = ToiletType.STREAM_CODEC.decode(buf);
            return new ToiletShapedRecipe(shaped, type);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ToiletShapedRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.streamCodec().encode(buf, recipe.delegate());
            ToiletType.STREAM_CODEC.encode(buf, recipe.toiletType());
        }
    };

    public static final RecipeSerializer<ToiletShapedRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
}
