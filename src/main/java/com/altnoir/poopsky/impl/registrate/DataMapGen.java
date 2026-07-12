package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class DataMapGen extends DataMapProvider {
    /**
     * Create a new provider.
     *
     * @param packOutput     the output location
     * @param lookupProvider a {@linkplain CompletableFuture} supplying the registries
     */
    public DataMapGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(PoItems.POOP.getId(), new FurnaceFuel(200), false)
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
                .add(PoBlocks.STOOL.getId(), new FurnaceFuel(800), false);
    }
}
