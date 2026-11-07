package com.altnoir.poopsky.init;

import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;

public final class PoFluidInteractions {
    private PoFluidInteractions() {
    }

    public static void register() {
        FluidInteractionRegistry.addInteraction(NeoForgeMod.WATER_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                PoFluidInteractions::hasUrineNeighbor, fluidState -> fluidState.isSource()
                        ? PoBlocks.POOLIME_BLOCK.get().defaultBlockState()
                        : Blocks.CLAY.defaultBlockState()));
        FluidInteractionRegistry.addInteraction(PFluidTypes.URINE_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.WATER_TYPE.value(), fluidState -> fluidState.isSource()
                        ? Blocks.COARSE_DIRT.defaultBlockState()
                        : Blocks.CLAY.defaultBlockState()));

        FluidInteractionRegistry.addInteraction(PFluidTypes.URINE_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.LAVA_TYPE.value(), fluidState -> fluidState.isSource()
                        ? Blocks.MAGMA_BLOCK.defaultBlockState()
                        : Blocks.NETHERRACK.defaultBlockState()));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                PoFluidInteractions::hasUrineNeighbor, fluidState -> fluidState.isSource()
                        ? Blocks.OBSIDIAN.defaultBlockState()
                        : Blocks.NETHERRACK.defaultBlockState()));

        registerLavaIceInteraction(PoBlocks.POOP_BLOCK.get(), Blocks.DRIPSTONE_BLOCK);
        registerLavaIceInteraction(PoBlocks.DRIED_POOP_BLOCK.get(), Blocks.DEEPSLATE);
        registerLavaIceInteraction(PoBlocks.DRIED_CHILI_POOP_BLOCK.get(), Blocks.NETHERRACK);
        registerLavaIceInteraction(PoBlocks.DRIED_GOLDEN_POOP_BLOCK.get(), Blocks.END_STONE);
        registerLavaIceInteraction(PoBlocks.RAW_POOP_BLOCK.get(), Blocks.ANDESITE);
        registerLavaIceInteraction(PoBlocks.RAW_SAPLING_POOP_BLOCK.get(), Blocks.GRANITE);
        registerLavaIceInteraction(PoBlocks.RAW_SEA_POOP_BLOCK.get(), Blocks.PRISMARINE);
        registerLavaIceInteraction(PoBlocks.RAW_WITHER_POOP_BLOCK.get(), Blocks.BLACKSTONE);
    }

    private static boolean hasUrineNeighbor(Level level, BlockPos currentPos, BlockPos relativePos, FluidState currentState) {
        var neighbor = level.getBlockState(relativePos);
        return neighbor.getFluidState().getFluidType() == PFluidTypes.URINE_FLUID_TYPE.get()
                || (neighbor.getBlock() instanceof AbstractToiletBlock
                && !neighbor.getOptionalValue(BaseToiletLavaBlock.LAVA).orElse(false));
    }

    private static void registerLavaIceInteraction(Block base, Block result) {
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(base)
                        && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                result.defaultBlockState()));
    }
}
