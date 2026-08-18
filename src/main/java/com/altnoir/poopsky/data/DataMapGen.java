package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Strippable;

public final class DataMapGen {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private DataMapGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.DATA_MAP, DataMapGen::generate);
    }

    private static void generate(RegistrateDataMapProvider provider) {
        provider.builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(PoBlocks.GINKGO_SAPLING.getId(), new Compostable(0.3F), false)
                .add(PoBlocks.GINKGO_LEAVES.getId(), new Compostable(0.3F), false);

        provider.builder(NeoForgeDataMaps.STRIPPABLES)
                .add(PoBlocks.GINKGO_LOG.getId(), new Strippable(PoBlocks.STRIPPED_GINKGO_LOG.get()), false)
                .add(PoBlocks.GINKGO_WOOD.getId(), new Strippable(PoBlocks.STRIPPED_GINKGO_WOOD.get()), false)
                .add(PoBlocks.PRIMO_STEM.getId(), new Strippable(PoBlocks.STRIPPED_PRIMO_STEM.get()), false)
                .add(PoBlocks.PRIMO_HYPHAE.getId(), new Strippable(PoBlocks.STRIPPED_PRIMO_HYPHAE.get()), false)
                .add(PoBlocks.POOP_LOG.getId(), new Strippable(PoBlocks.STRIPPED_POOP_LOG.get()), false)
                .add(PoBlocks.POOP_WOOD.getId(), new Strippable(PoBlocks.STRIPPED_POOP_WOOD.get()), false)
                .add(PoBlocks.POOP_EMPTY_LOG.getId(), new Strippable(PoBlocks.STRIPPED_POOP_EMPTY_LOG.get()), false);

        provider.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(PoItems.POOP.getId(), new FurnaceFuel(200), false)
                .add(PoBlocks.SHIT.getId(), new FurnaceFuel(600), false)
                .add(PoItems.POOP_BALL.getId(), new FurnaceFuel(400), false)
                .add(PoBlocks.POOP_SAPLING.getId(), new FurnaceFuel(200), false)
                .add(PoBlocks.POOP_LEAVES.getId(), new FurnaceFuel(200), false)
                .add(PoBlocks.POOP_BLOCK.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.POOP_STAIRS.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.POOP_SLAB.getId(), new FurnaceFuel(400), false)
                .add(PoBlocks.POOP_VERTICAL_SLAB.getId(), new FurnaceFuel(400), false)
                .add(PoBlocks.POOP_BUTTON.getId(), new FurnaceFuel(200), false)
                .add(PoBlocks.POOP_PRESSURE_PLATE.getId(), new FurnaceFuel(400), false)
                .add(PoBlocks.POOP_FENCE.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.POOP_FENCE_GATE.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.POOP_WALL.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.POOP_DOOR.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.POOP_TRAPDOOR.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.POOP_LOG.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.STRIPPED_POOP_LOG.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.POOP_EMPTY_LOG.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.STRIPPED_POOP_EMPTY_LOG.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.POOP_PIECE.getId(), new FurnaceFuel(400), false)
                .add(PoBlocks.STOOL.getId(), new FurnaceFuel(800), false)
                .add(PoBlocks.GINKGO_SAPLING.getId(), new FurnaceFuel(100), false)
                .add(PoBlocks.GINKGO_LOG.getId(), new FurnaceFuel(300), false)
                .add(PoBlocks.STRIPPED_GINKGO_LOG.getId(), new FurnaceFuel(300), false)
                .add(PoBlocks.GINKGO_WOOD.getId(), new FurnaceFuel(300), false)
                .add(PoBlocks.STRIPPED_GINKGO_WOOD.getId(), new FurnaceFuel(300), false)
                .add(PoBlocks.GINKGO_PLANKS.getId(), new FurnaceFuel(300), false)
                .add(PoBlocks.GINKGO_STAIRS.getId(), new FurnaceFuel(300), false)
                .add(PoBlocks.GINKGO_SLAB.getId(), new FurnaceFuel(150), false)
                .add(PoBlocks.GINKGO_VERTICAL_SLAB.getId(), new FurnaceFuel(150), false)
                .add(PoBlocks.GINKGO_BUTTON.getId(), new FurnaceFuel(100), false)
                .add(PoBlocks.GINKGO_PRESSURE_PLATE.getId(), new FurnaceFuel(300), false)
                .add(PoBlocks.GINKGO_FENCE.getId(), new FurnaceFuel(300), false)
                .add(PoBlocks.GINKGO_FENCE_GATE.getId(), new FurnaceFuel(300), false)
                .add(PoBlocks.GINKGO_DOOR.getId(), new FurnaceFuel(200), false)
                .add(PoBlocks.GINKGO_TRAPDOOR.getId(), new FurnaceFuel(300), false);
    }
}
