package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class PSDataMapProvider extends DataMapProvider {
    /**
     * Create a new provider.
     *
     * @param packOutput     the output location
     * @param lookupProvider a {@linkplain CompletableFuture} supplying the registries
     */
    protected PSDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(PItems.POOP.getId(), new FurnaceFuel(200), false)
                .add(PItems.POOP_BALL.getId(), new FurnaceFuel(400), false)
                .add(PBlocks.POOP_SAPLING.getId(), new FurnaceFuel(200), false)
                .add(PBlocks.POOP_LEAVES.getId(), new FurnaceFuel(200), false)
                .add(PBlocks.POOP_BLOCK.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.POOP_STAIRS.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.POOP_SLAB.getId(), new FurnaceFuel(400), false)
                .add(PBlocks.POOP_VERTICAL_SLAB.getId(), new FurnaceFuel(400), false)
                .add(PBlocks.POOP_BUTTON.getId(), new FurnaceFuel(200), false)
                .add(PBlocks.POOP_PRESSURE_PLATE.getId(), new FurnaceFuel(400), false)
                .add(PBlocks.POOP_FENCE.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.POOP_FENCE_GATE.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.POOP_WALL.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.POOP_DOOR.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.POOP_TRAPDOOR.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.POOP_LOG.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.STRIPPED_POOP_LOG.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.POOP_EMPTY_LOG.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.STRIPPED_POOP_EMPTY_LOG.getId(), new FurnaceFuel(800), false)
                .add(PBlocks.POOP_PIECE.getId(), new FurnaceFuel(400), false)
                .add(PBlocks.STOOL.getId(), new FurnaceFuel(800), false);
    }
}
