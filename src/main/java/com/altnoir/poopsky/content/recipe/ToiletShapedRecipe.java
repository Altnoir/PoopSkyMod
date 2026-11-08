package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoRecipes;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class ToiletShapedRecipe extends ShapedRecipe {
    private final ShapedRecipe delegate;
    private final ToiletType toiletType;

    public ToiletShapedRecipe(ShapedRecipe delegate, ToiletType toiletType) {
        super(delegate.getId(), delegate.getGroup(), delegate.category(), delegate.getWidth(), delegate.getHeight(),
                delegate.getIngredients(), delegate.getResultItem(null).copy(), delegate.showNotification());
        this.delegate = delegate;
        this.toiletType = toiletType;
    }
    public ShapedRecipe delegate() { return delegate; }
    public ToiletType toiletType() { return toiletType; }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        ItemStack result = super.assemble(input, registries);
        PoComponents.TOILET_TYPE.set(result, toiletType);
        return result;
    }
    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        ItemStack result = super.getResultItem(registries).copy();
        PoComponents.TOILET_TYPE.set(result, toiletType);
        return result;
    }
    @Override
    public RecipeSerializer<?> getSerializer() { return PoRecipes.TOILET_SHAPED_SERIALIZER.get(); }

    public static class Serializer implements RecipeSerializer<ToiletShapedRecipe> {
        @Override
        public ToiletShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
            ShapedRecipe shaped = RecipeSerializer.SHAPED_RECIPE.fromJson(id, GsonHelper.getAsJsonObject(json, "delegate"));
            String typeId = GsonHelper.getAsString(json, "toilet_type");
            ToiletType type = ToiletType.byId(typeId);
            if (type == null) throw new JsonSyntaxException("Unknown toilet type: " + typeId);
            return new ToiletShapedRecipe(shaped, type);
        }
        @Override
        public ToiletShapedRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ShapedRecipe shaped = RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buffer);
            return new ToiletShapedRecipe(shaped, ToiletType.STREAM_CODEC.decode(buffer));
        }
        @Override
        public void toNetwork(FriendlyByteBuf buffer, ToiletShapedRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe.delegate());
            ToiletType.STREAM_CODEC.encode(buffer, recipe.toiletType());
        }
    }
}
