package com.altnoir.poopsky.content.recipe;

import net.minecraft.world.Container;
import net.minecraft.resources.ResourceLocation;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.ByteBufCodecs;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public record POPExplosionRecipe(ResourceLocation id, Ingredient input, int radius, Output output) implements Recipe<Container> {
    public POPExplosionRecipe(Ingredient input, int radius, Output output) { this(new ResourceLocation("poopsky", "unregistered"), input, radius, output); }
    @Override public ResourceLocation getId() { return id; }
    private POPExplosionRecipe withId(ResourceLocation id) { return new POPExplosionRecipe(id, input, radius, output); }

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

        public static final StreamCodec<FriendlyByteBuf, Output> STREAM_CODEC = StreamCodec.of(
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
    public boolean matches(Container recipeInput, Level level) {
        return input.test(recipeInput.getItem(0));
    }

    public boolean matches(Container recipeInput, int explosionRadius) {
        if (!input.test(recipeInput.getItem(0))) return false;
        return radius <= 0 || explosionRadius >= radius;
    }

    @Override
    public ItemStack assemble(Container recipeInput, net.minecraft.core.RegistryAccess registries) {
        return output.toItemStack();
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.RegistryAccess registries) {
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
                        PoRecipeCodecs.INGREDIENT.fieldOf("input").forGetter(POPExplosionRecipe::input),
                        Codec.INT.optionalFieldOf("radius", 0).forGetter(POPExplosionRecipe::radius),
                        Output.CODEC.fieldOf("output").forGetter(POPExplosionRecipe::output)
                ).apply(instance, POPExplosionRecipe::new));

        public static final StreamCodec<FriendlyByteBuf, POPExplosionRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.INGREDIENT, POPExplosionRecipe::input,
                        ByteBufCodecs.VAR_INT, POPExplosionRecipe::radius,
                        Output.STREAM_CODEC, POPExplosionRecipe::output,
                        POPExplosionRecipe::new);

        @Override
        public POPExplosionRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
            return CODEC.codec().parse(com.mojang.serialization.JsonOps.INSTANCE, json)
                    .getOrThrow(false, error -> { throw new com.google.gson.JsonSyntaxException(error); }).withId(id);
        }

        @Override
        public POPExplosionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return STREAM_CODEC.decode(buffer).withId(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, POPExplosionRecipe recipe) {
            STREAM_CODEC.encode(buffer, recipe);
        }

        public MapCodec<POPExplosionRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<FriendlyByteBuf, POPExplosionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
