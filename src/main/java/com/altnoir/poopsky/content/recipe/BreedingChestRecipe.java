package com.altnoir.poopsky.content.recipe;

import net.minecraft.world.Container;
import net.minecraft.resources.ResourceLocation;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.ByteBufCodecs;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 繁育箱变异配方：两个父本苍蝇品种 -> 子代品种 + 概率
 */
public record BreedingChestRecipe(ResourceLocation id, String parent1, String parent2, String result, float chance) implements Recipe<Container> {
    public BreedingChestRecipe(String parent1, String parent2, String result, float chance) { this(new ResourceLocation("poopsky", "unregistered"), parent1, parent2, result, chance); }
    @Override public ResourceLocation getId() { return id; }
    private BreedingChestRecipe withId(ResourceLocation id) { return new BreedingChestRecipe(id, parent1, parent2, result, chance); }

    /**
     * 双向匹配：parent1+parent2 或 parent2+parent1。
     */
    public boolean matches(String p1, String p2) {
        return (parent1.equals(p1) && parent2.equals(p2))
                || (parent1.equals(p2) && parent2.equals(p1));
    }

    @Override
    public boolean matches(Container input, Level level) {
        return false; // 不使用标准 Container 匹配
    }

    @Override
    public ItemStack assemble(Container input, net.minecraft.core.RegistryAccess registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.RegistryAccess registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PoRecipes.BREEDING_CHEST.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return PoRecipes.BREEDING_CHEST.type().get();
    }

    public static class Serializer implements RecipeSerializer<BreedingChestRecipe> {
        public static final MapCodec<BreedingChestRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ExtraCodecs.NON_EMPTY_STRING.fieldOf("parent1").forGetter(BreedingChestRecipe::parent1),
                ExtraCodecs.NON_EMPTY_STRING.fieldOf("parent2").forGetter(BreedingChestRecipe::parent2),
                ExtraCodecs.NON_EMPTY_STRING.fieldOf("result").forGetter(BreedingChestRecipe::result),
                Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(BreedingChestRecipe::chance)
        ).apply(inst, BreedingChestRecipe::new));

        public static final StreamCodec<FriendlyByteBuf, BreedingChestRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, BreedingChestRecipe::parent1,
                        ByteBufCodecs.STRING_UTF8, BreedingChestRecipe::parent2,
                        ByteBufCodecs.STRING_UTF8, BreedingChestRecipe::result,
                        ByteBufCodecs.FLOAT, BreedingChestRecipe::chance,
                        BreedingChestRecipe::new);

        @Override
        public BreedingChestRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
            return CODEC.codec().parse(com.mojang.serialization.JsonOps.INSTANCE, json)
                    .getOrThrow(false, error -> { throw new com.google.gson.JsonSyntaxException(error); }).withId(id);
        }

        @Override
        public BreedingChestRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return STREAM_CODEC.decode(buffer).withId(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BreedingChestRecipe recipe) {
            STREAM_CODEC.encode(buffer, recipe);
        }

        public MapCodec<BreedingChestRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<FriendlyByteBuf, BreedingChestRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
