package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record AltarRecipe(Ingredient input, List<Ingredient> pedestals, FluidStack fluid,
                          int duration, ItemStack result, String command,
                          String description) implements Recipe<AltarRecipe.Input> {
    public AltarRecipe {
        pedestals = List.copyOf(pedestals);
        if (pedestals.isEmpty() || pedestals.size() > 8 || fluid.isEmpty() || duration <= 0
                || (result.isEmpty() && command.isBlank())) {
            throw new IllegalArgumentException("Invalid altar recipe");
        }
    }

    @Override
    public boolean matches(Input input, Level level) {
        if (!this.input.test(input.center()) || input.items().size() != pedestals.size()) {
            return false;
        }
        return matchIngredients(input.items(), 0, 0);
    }

    private boolean matchIngredients(List<ItemStack> items, int ingredientIndex, int used) {
        if (ingredientIndex == pedestals.size()) {
            return true;
        }
        Ingredient ingredient = pedestals.get(ingredientIndex);
        for (int i = 0; i < items.size(); i++) {
            if ((used & (1 << i)) == 0 && ingredient.test(items.get(i))
                    && matchIngredients(items, ingredientIndex + 1, used | (1 << i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PoRecipes.ALTAR.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return PoRecipes.ALTAR.type().get();
    }

    public record Input(ItemStack center, List<ItemStack> items) implements RecipeInput {
        public Input {
            items = List.copyOf(items);
        }

        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? center : items.get(index - 1);
        }

        @Override
        public int size() {
            return items.size() + 1;
        }
    }

    public static class Serializer implements RecipeSerializer<AltarRecipe> {
        private static final MapCodec<AltarRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(AltarRecipe::input),
                Ingredient.CODEC_NONEMPTY.listOf(1, 8).fieldOf("pedestals").forGetter(AltarRecipe::pedestals),
                FluidStack.CODEC.fieldOf("fluid").forGetter(AltarRecipe::fluid),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("duration", 200).forGetter(AltarRecipe::duration),
                ItemStack.STRICT_CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(AltarRecipe::result),
                Codec.STRING.optionalFieldOf("command", "").forGetter(AltarRecipe::command),
                Codec.STRING.optionalFieldOf("description", "").forGetter(AltarRecipe::description)
        ).apply(instance, AltarRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC.codec());

        @Override
        public MapCodec<AltarRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
