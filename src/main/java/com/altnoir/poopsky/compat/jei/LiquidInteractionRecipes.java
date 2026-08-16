package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public final class LiquidInteractionRecipes {
    private LiquidInteractionRecipes() {
    }

    public static List<LiquidInteractionRecipe> all() {
        return List.of(
                new LiquidInteractionRecipe(PoopSky.loc("water_urine"),
                        Fluids.WATER,
                        PoFluids.URINE.get(),
                        null,
                        null,
                        List.of(PoBlocks.POOLIME_BLOCK.get(), Blocks.CLAY)),
                new LiquidInteractionRecipe(PoopSky.loc("urine_water"),
                        PoFluids.URINE.get(),
                        Fluids.WATER,
                        null,
                        null,
                        List.of(Blocks.COARSE_DIRT, Blocks.CLAY)),
                new LiquidInteractionRecipe(PoopSky.loc("lava_urine"),
                        Fluids.LAVA,
                        PoFluids.URINE.get(),
                        null,
                        null,
                        List.of(Blocks.OBSIDIAN, Blocks.NETHERRACK)),
                new LiquidInteractionRecipe(PoopSky.loc("urine_lava"),
                        PoFluids.URINE.get(),
                        Fluids.LAVA,
                        null,
                        null,
                        List.of(Blocks.MAGMA_BLOCK, Blocks.NETHERRACK)),
                new LiquidInteractionRecipe(PoopSky.loc("urine_sand"),
                        PoFluids.URINE.get(),
                        null,
                        Blocks.SAND,
                        null,
                        List.of(PoBlocks.POOP_SAND.get())),
                new LiquidInteractionRecipe(PoopSky.loc("lava_soul_sand"),
                        Fluids.LAVA,
                        null,
                        PoBlocks.POOP_SAND.get(),
                        null,
                        List.of(Blocks.SOUL_SAND)),
                new LiquidInteractionRecipe(PoopSky.loc("lava_blue_ice_poop"),
                        Fluids.LAVA,
                        null,
                        Blocks.BLUE_ICE,
                        PoBlocks.POOP_BLOCK.get(),
                        List.of(Blocks.DRIPSTONE_BLOCK)),
                new LiquidInteractionRecipe(PoopSky.loc("lava_blue_ice_dried_poop"),
                        Fluids.LAVA,
                        null,
                        Blocks.BLUE_ICE,
                        PoBlocks.DRIED_POOP_BLOCK.get(),
                        List.of(Blocks.DEEPSLATE)),
                new LiquidInteractionRecipe(PoopSky.loc("lava_blue_ice_dried_chili_poop"),
                        Fluids.LAVA,
                        null,
                        Blocks.BLUE_ICE,
                        PoBlocks.DRIED_CHILI_POOP_BLOCK.get(),
                        List.of(Blocks.NETHERRACK)),
                new LiquidInteractionRecipe(PoopSky.loc("lava_blue_ice_dried_golden_poop"),
                        Fluids.LAVA,
                        null,
                        Blocks.BLUE_ICE,
                        PoBlocks.DRIED_GOLDEN_POOP_BLOCK.get(),
                        List.of(Blocks.END_STONE)),
                new LiquidInteractionRecipe(PoopSky.loc("lava_blue_ice_raw_poop"),
                        Fluids.LAVA,
                        null,
                        Blocks.BLUE_ICE,
                        PoBlocks.RAW_POOP_BLOCK.get(),
                        List.of(Blocks.ANDESITE)),
                new LiquidInteractionRecipe(PoopSky.loc("lava_blue_ice_raw_sapling_poop"),
                        Fluids.LAVA,
                        null,
                        Blocks.BLUE_ICE,
                        PoBlocks.RAW_SAPLING_POOP_BLOCK.get(),
                        List.of(Blocks.GRANITE)),
                new LiquidInteractionRecipe(PoopSky.loc("lava_blue_ice_raw_sea_poop"),
                        Fluids.LAVA,
                        null,
                        Blocks.BLUE_ICE,
                        PoBlocks.RAW_SEA_POOP_BLOCK.get(),
                        List.of(Blocks.PRISMARINE)),
                new LiquidInteractionRecipe(PoopSky.loc("lava_blue_ice_raw_wither_poop"),
                        Fluids.LAVA,
                        null,
                        Blocks.BLUE_ICE,
                        PoBlocks.RAW_WITHER_POOP_BLOCK.get(),
                        List.of(Blocks.BLACKSTONE))
        );
    }
}