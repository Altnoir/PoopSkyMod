package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.Optional;

public record IgneousExtruderRecipe(List<SideInput> inputs, Optional<Block> below, ItemStack result)
        implements Recipe<IgneousExtruderRecipe.Input> {
    public IgneousExtruderRecipe {
        inputs = List.copyOf(inputs);
        if (inputs.size() != 2) {
            throw new IllegalArgumentException("Igneous extruder recipes require two side inputs");
        }
    }

    public int matchingPairs(Input input) {
        if (below.isPresent() && !input.below().is(below.get())) {
            return 0;
        }

        SideInput first = inputs.get(0);
        SideInput second = inputs.get(1);
        int firstMatches = 0;
        int secondMatches = 0;
        for (int i = 0; i < input.size(); i++) {
            BlockState side = input.sides().get(i);
            if (first.matches(side)) firstMatches |= 1 << i;
            if (second.matches(side)) secondMatches |= 1 << i;
        }

        // A pair's complement uses exactly the two sides that are still free.
        int pairs = 0;
        int allSides = (1 << input.size()) - 1;
        for (int i = 0; i < input.size(); i++) {
            if ((firstMatches & (1 << i)) == 0) continue;
            for (int j = 0; j < input.size(); j++) {
                if (i == j || (secondMatches & (1 << j)) == 0) continue;
                int pair = (1 << i) | (1 << j);
                if ((pairs & (1 << (allSides ^ pair))) != 0) {
                    return 2;
                }
                pairs |= 1 << pair;
            }
        }
        return pairs == 0 ? 0 : 1;
    }

    @Override
    public boolean matches(Input input, Level level) {
        return matchingPairs(input) > 0;
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
        return PoRecipes.IGNEOUS_EXTRUDER.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return PoRecipes.IGNEOUS_EXTRUDER.type().get();
    }

    public record Input(List<BlockState> sides, BlockState below) implements RecipeInput {
        public Input {
            sides = List.copyOf(sides);
            if (sides.size() != 4) {
                throw new IllegalArgumentException("Igneous extruder inputs require four horizontal sides");
            }
        }

        @Override
        public ItemStack getItem(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return sides.size();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    public record SideInput(Optional<Fluid> fluid, Optional<Block> block) {
        public static final Codec<SideInput> CODEC = RecordCodecBuilder.<SideInput>create(instance -> instance.group(
                BuiltInRegistries.FLUID.byNameCodec().optionalFieldOf("fluid").forGetter(SideInput::fluid),
                BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("block").forGetter(SideInput::block)
        ).apply(instance, SideInput::new)).flatXmap(SideInput::validate, SideInput::validate);

        private static final StreamCodec<RegistryFriendlyByteBuf, SideInput> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.FLUID)), SideInput::fluid,
                ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.BLOCK)), SideInput::block,
                SideInput::new);

        public static SideInput of(Fluid fluid) {
            return new SideInput(Optional.of(fluid), Optional.empty());
        }

        public static SideInput of(Block block) {
            return new SideInput(Optional.empty(), Optional.of(block));
        }

        private static DataResult<SideInput> validate(SideInput input) {
            if (input.fluid.isPresent() == input.block.isPresent()) {
                return DataResult.error(() -> "A side input requires exactly one of fluid or block");
            }
            if (input.fluid.filter(fluid -> fluid == Fluids.EMPTY || !fluid.defaultFluidState().isSource()).isPresent()) {
                return DataResult.error(() -> "A fluid input requires a source fluid ID");
            }
            return DataResult.success(input);
        }

        private boolean matches(BlockState state) {
            if (block.isPresent()) {
                return state.is(block.get());
            }
            var fluidState = state.getFluidState();
            return fluidState.isSource() && fluidState.getType() == fluid.orElseThrow();
        }
    }

    public static class Serializer implements RecipeSerializer<IgneousExtruderRecipe> {
        private static final MapCodec<IgneousExtruderRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                SideInput.CODEC.listOf(2, 2).fieldOf("inputs").forGetter(IgneousExtruderRecipe::inputs),
                BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("below").forGetter(IgneousExtruderRecipe::below),
                ItemStack.CODEC.fieldOf("result").forGetter(IgneousExtruderRecipe::result)
        ).apply(instance, IgneousExtruderRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, IgneousExtruderRecipe> STREAM_CODEC = StreamCodec.composite(
                SideInput.STREAM_CODEC.apply(ByteBufCodecs.list(2)), IgneousExtruderRecipe::inputs,
                ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.BLOCK)), IgneousExtruderRecipe::below,
                ItemStack.STREAM_CODEC, IgneousExtruderRecipe::result,
                IgneousExtruderRecipe::new);

        @Override
        public MapCodec<IgneousExtruderRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, IgneousExtruderRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
