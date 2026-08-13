package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record CompooperRecipe(String fluidType, ItemStackTemplate input, ItemStackTemplate output) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<CompooperRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("fluid_type").forGetter(CompooperRecipe::fluidType),
                    ItemStackTemplate.CODEC.fieldOf("input").forGetter(CompooperRecipe::input),
                    ItemStackTemplate.CODEC.fieldOf("output").forGetter(CompooperRecipe::output)
            ).apply(instance, CompooperRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompooperRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, CompooperRecipe::fluidType,
                    ItemStackTemplate.STREAM_CODEC, CompooperRecipe::input,
                    ItemStackTemplate.STREAM_CODEC, CompooperRecipe::output,
                    CompooperRecipe::new);

    public static final RecipeSerializer<CompooperRecipe> SERIALIZER =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return false; // Not used for custom matching
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput) {
        return output.create();
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
    public RecipeType<CompooperRecipe> getType() {
        return PoRecipes.COMPOOPER.type().get();
    }

    @Override
    public RecipeSerializer<CompooperRecipe> getSerializer() {
        return PoRecipes.COMPOOPER.serializer().get();
    }

    public boolean matchesFluid(String type) {
        return this.fluidType.equals(type);
    }

    /**
     * 检查输入物品是否匹配，支持组件匹配
     */
    public boolean matchesInput(ItemStack stack) {
        ItemStack expected = input.create();
        if (!ItemStack.isSameItemSameComponents(expected, stack)) {
            return false;
        }
        return stack.getCount() >= input.count();
    }

}
