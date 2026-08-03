package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record CompooperRecipe(String fluidType, ItemStack input, ItemStack output) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return false; // Not used for custom matching
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
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

    @Override
    public boolean isSpecial() {
        return true;
    }

    public boolean matchesFluid(String type) {
        return this.fluidType.equals(type);
    }

    /**
     * 检查输入物品是否匹配，支持组件匹配
     */
    public boolean matchesInput(ItemStack stack) {
        if (!ItemStack.isSameItemSameComponents(input, stack)) {
            return false;
        }
        return stack.getCount() >= input.getCount();
    }

    public static class Serializer implements RecipeSerializer<CompooperRecipe> {

        public static final MapCodec<CompooperRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.fieldOf("fluid_type").forGetter(CompooperRecipe::fluidType),
                        ItemStack.STRICT_CODEC.fieldOf("input").forGetter(CompooperRecipe::input),
                        ItemStack.STRICT_CODEC.fieldOf("output").forGetter(CompooperRecipe::output)
                ).apply(instance, CompooperRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CompooperRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, CompooperRecipe::fluidType,
                        ItemStack.STREAM_CODEC, CompooperRecipe::input,
                        ItemStack.STREAM_CODEC, CompooperRecipe::output,
                        CompooperRecipe::new);

        @Override
        public MapCodec<CompooperRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CompooperRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
