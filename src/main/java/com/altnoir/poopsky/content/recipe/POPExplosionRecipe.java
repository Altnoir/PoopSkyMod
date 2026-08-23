package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public record POPExplosionRecipe(Ingredient input, int radius, Output output) implements Recipe<SingleRecipeInput> {

    public record Output(@Nullable Block block, @Nullable Item item) {
        public Output {
            if (block == null && item == null)
                throw new IllegalArgumentException("Output must have either block or item");
            if (block != null && item != null)
                throw new IllegalArgumentException("Output cannot have both block and item");
        }

        public boolean isBlock() { return block != null; }

        public ItemStack toItemStack() {
            return isBlock() ? new ItemStack(block.asItem()) : new ItemStack(item);
        }

        public static final Codec<Output> CODEC = Codec.either(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").codec(),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").codec()
        ).xmap(
                either -> either.map(b -> new Output(b, null), i -> new Output(null, i)),
                output -> output.isBlock() ? Either.left(output.block()) : Either.right(output.item())
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Output> STREAM_CODEC = StreamCodec.of(
                (buf, output) -> {
                    buf.writeBoolean(output.isBlock());
                    if (output.isBlock()) {
                        ByteBufCodecs.registry(Registries.BLOCK).encode(buf, output.block());
                    } else {
                        ByteBufCodecs.registry(Registries.ITEM).encode(buf, output.item());
                    }
                },
                buf -> buf.readBoolean()
                        ? new Output(ByteBufCodecs.registry(Registries.BLOCK).decode(buf), null)
                        : new Output(null, ByteBufCodecs.registry(Registries.ITEM).decode(buf))
        );
    }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return input.test(recipeInput.item());
    }

    public boolean matches(SingleRecipeInput recipeInput, int explosionRadius) {
        if (!input.test(recipeInput.item())) return false;
        return radius <= 0 || explosionRadius >= radius;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider registries) {
        return output.toItemStack();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.toItemStack();
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
        return PoRecipes.POP_EXPLOSION.type().get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PoRecipes.POP_EXPLOSION.serializer().get();
    }

    public static class Serializer implements RecipeSerializer<POPExplosionRecipe> {

        public static final MapCodec<POPExplosionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(POPExplosionRecipe::input),
                        Codec.INT.optionalFieldOf("radius", 0).forGetter(POPExplosionRecipe::radius),
                        Output.CODEC.fieldOf("output").forGetter(POPExplosionRecipe::output)
                ).apply(instance, POPExplosionRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, POPExplosionRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, POPExplosionRecipe::input,
                        ByteBufCodecs.VAR_INT, POPExplosionRecipe::radius,
                        Output.STREAM_CODEC, POPExplosionRecipe::output,
                        POPExplosionRecipe::new);

        @Override
        public MapCodec<POPExplosionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, POPExplosionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
