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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record SieveRecipe(ResourceLocation id, Ingredient input, List<ChanceItemStack> outputs, int processingTime) implements Recipe<Container> {
    public SieveRecipe(Ingredient input, List<ChanceItemStack> outputs, int processingTime) { this(new ResourceLocation("poopsky", "unregistered"), input, outputs, processingTime); }
    @Override public ResourceLocation getId() { return id; }
    private SieveRecipe withId(ResourceLocation id) { return new SieveRecipe(id, input, outputs, processingTime); }

    @Override
    public boolean matches(Container recipeInput, Level level) {
        return input.test(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(Container recipeInput, net.minecraft.core.RegistryAccess registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.RegistryAccess registries) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).stack().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    @Override
    public RecipeType<?> getType() {
        return PoRecipes.SIEVE.type().get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PoRecipes.SIEVE.serializer().get();
    }

    public List<ItemStack> rollOutputs(RandomSource random) {
        List<ItemStack> rolledOutputs = new ArrayList<>();
        for (ChanceItemStack entry : outputs) {
            if (entry.roll(random)) {
                rolledOutputs.add(entry.stack().copy());
            }
        }
        return rolledOutputs;
    }

    public record ChanceItemStack(ItemStack stack, float chance) {
        public static final Codec<ChanceItemStack> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        PoRecipeCodecs.ITEM_STACK.fieldOf("item").forGetter(ChanceItemStack::stack),
                        Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(ChanceItemStack::chance)
                ).apply(instance, ChanceItemStack::new));

        public static final StreamCodec<FriendlyByteBuf, ChanceItemStack> STREAM_CODEC =
                StreamCodec.composite(
                        com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.ITEM_STACK, ChanceItemStack::stack,
                        ByteBufCodecs.FLOAT, ChanceItemStack::chance,
                        ChanceItemStack::new);

        public boolean roll(RandomSource random) {
            return chance > 0.0F && random.nextFloat() < chance;
        }
    }

    public static class Serializer implements RecipeSerializer<SieveRecipe> {

        public static final MapCodec<SieveRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        PoRecipeCodecs.INGREDIENT.fieldOf("input").forGetter(SieveRecipe::input),
                        ChanceItemStack.CODEC.listOf().fieldOf("outputs").forGetter(SieveRecipe::outputs),
                        Codec.INT.optionalFieldOf("processingTime", 200).forGetter(SieveRecipe::processingTime)
                ).apply(instance, SieveRecipe::new));

        public static final StreamCodec<FriendlyByteBuf, SieveRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.INGREDIENT, SieveRecipe::input,
                        ChanceItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), SieveRecipe::outputs,
                        ByteBufCodecs.VAR_INT, SieveRecipe::processingTime,
                        SieveRecipe::new);

        @Override
        public SieveRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
            return CODEC.codec().parse(com.mojang.serialization.JsonOps.INSTANCE, json)
                    .getOrThrow(false, error -> { throw new com.google.gson.JsonSyntaxException(error); }).withId(id);
        }

        @Override
        public SieveRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return STREAM_CODEC.decode(buffer).withId(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SieveRecipe recipe) {
            STREAM_CODEC.encode(buffer, recipe);
        }

        public MapCodec<SieveRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<FriendlyByteBuf, SieveRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
