package com.altnoir.poopsky.content.recipe;

import net.minecraft.world.Container;
import net.minecraft.resources.ResourceLocation;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.ByteBufCodecs;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record CompooperRecipe(ResourceLocation id, String fluidType, ItemStack input, ItemStack output) implements Recipe<Container> {
    public CompooperRecipe(String fluidType, ItemStack input, ItemStack output) { this(new ResourceLocation("poopsky", "unregistered"), fluidType, input, output); }
    @Override public ResourceLocation getId() { return id; }
    private CompooperRecipe withId(ResourceLocation id) { return new CompooperRecipe(id, fluidType, input, output); }

    @Override
    public boolean matches(Container recipeInput, Level level) {
        return false; // Not used for custom matching
    }

    @Override
    public ItemStack assemble(Container recipeInput, net.minecraft.core.RegistryAccess registries) {
        return output.copy();
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.RegistryAccess registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.of(input.getItem()));
        return list;
    }

    @Override
    public RecipeType<?> getType() {
        return PoRecipes.COMPOOPER.type().get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PoRecipes.COMPOOPER.serializer().get();
    }

    public boolean matchesFluid(String type) {
        return this.fluidType.equals(type);
    }

    /**
     * 检查输入物品是否匹配，支持组件匹配
     */
    public boolean matchesInput(ItemStack stack) {
        if (!ItemStack.isSameItemSameTags(input, stack)) {
            return false;
        }
        return stack.getCount() >= input.getCount();
    }

    public static class Serializer implements RecipeSerializer<CompooperRecipe> {

        public static final MapCodec<CompooperRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.fieldOf("fluid_type").forGetter(CompooperRecipe::fluidType),
                        PoRecipeCodecs.ITEM_STACK.fieldOf("input").forGetter(CompooperRecipe::input),
                        PoRecipeCodecs.ITEM_STACK.fieldOf("output").forGetter(CompooperRecipe::output)
                ).apply(instance, CompooperRecipe::new));

        public static final StreamCodec<FriendlyByteBuf, CompooperRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, CompooperRecipe::fluidType,
                        com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.ITEM_STACK, CompooperRecipe::input,
                        com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.ITEM_STACK, CompooperRecipe::output,
                        CompooperRecipe::new);

        @Override
        public CompooperRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
            return CODEC.codec().parse(com.mojang.serialization.JsonOps.INSTANCE, json)
                    .getOrThrow(false, error -> { throw new com.google.gson.JsonSyntaxException(error); }).withId(id);
        }

        @Override
        public CompooperRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return STREAM_CODEC.decode(buffer).withId(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CompooperRecipe recipe) {
            STREAM_CODEC.encode(buffer, recipe);
        }

        public MapCodec<CompooperRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<FriendlyByteBuf, CompooperRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
