package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
    public static final MapCodec<POPExplosionRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("input").forGetter(POPExplosionRecipe::input),
                    Codec.INT.optionalFieldOf("radius", 0).forGetter(POPExplosionRecipe::radius),
                    Output.CODEC.fieldOf("output").forGetter(POPExplosionRecipe::output)
            ).apply(instance, POPExplosionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, POPExplosionRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, POPExplosionRecipe::input,
                    ByteBufCodecs.VAR_INT, POPExplosionRecipe::radius,
                    Output.STREAM_CODEC, POPExplosionRecipe::output,
                    POPExplosionRecipe::new);

    public static final RecipeSerializer<POPExplosionRecipe> SERIALIZER =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public record Output(@Nullable Block block, @Nullable Item item) {
        public Output {
            if (block == null && item == null)
                throw new IllegalArgumentException("Output must have either block or item");
            if (block != null && item != null)
                throw new IllegalArgumentException("Output cannot have both block and item");
        }

        public boolean isBlock() { return block != null; }

        public boolean isItem() { return item != null; }

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
                buf -> {
                    boolean isBlock = buf.readBoolean();
                    if (isBlock) {
                        return new Output(ByteBufCodecs.registry(Registries.BLOCK).decode(buf), null);
                    } else {
                        return new Output(null, ByteBufCodecs.registry(Registries.ITEM).decode(buf));
                    }
                }
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
    public ItemStack assemble(SingleRecipeInput recipeInput) {
        return output.toItemStack();
    }

    @Override
    public boolean isSpecial() {
        return true;
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
    public RecipeType<POPExplosionRecipe> getType() {
        return PoRecipes.POP_EXPLOSION.type().get();
    }

    @Override
    public RecipeSerializer<POPExplosionRecipe> getSerializer() {
        return PoRecipes.POP_EXPLOSION.serializer().get();
    }
}
