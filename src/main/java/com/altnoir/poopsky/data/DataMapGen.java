package com.altnoir.poopsky.data;

import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;

public final class DataMapGen {
    private DataMapGen() {
    }

    public static void register() {
        CompostingChanceRegistry.INSTANCE.add(PoBlocks.GINKGO_SAPLING.asItem(), 0.3F);
        CompostingChanceRegistry.INSTANCE.add(PoBlocks.GINKGO_LEAVES.asItem(), 0.3F);

        StrippableBlockRegistry.register(PoBlocks.GINKGO_LOG.get(), PoBlocks.STRIPPED_GINKGO_LOG.get());
        StrippableBlockRegistry.register(PoBlocks.GINKGO_WOOD.get(), PoBlocks.STRIPPED_GINKGO_WOOD.get());
        StrippableBlockRegistry.register(PoBlocks.POOP_LOG.get(), PoBlocks.STRIPPED_POOP_LOG.get());
        StrippableBlockRegistry.register(PoBlocks.POOP_WOOD.get(), PoBlocks.STRIPPED_POOP_WOOD.get());

        FuelRegistry fuels = FuelRegistry.INSTANCE;
        fuels.add(PoItems.POOP.get(), 200);
        fuels.add(PoItems.POOP_BALL.get(), 400);
        fuels.add(PoBlocks.POOP_SAPLING.asItem(), 200);
        fuels.add(PoBlocks.POOP_LEAVES.asItem(), 200);
        fuels.add(PoBlocks.POOP_BLOCK.asItem(), 800);
        fuels.add(PoBlocks.POOP_STAIRS.asItem(), 800);
        fuels.add(PoBlocks.POOP_SLAB.asItem(), 400);
        fuels.add(PoBlocks.POOP_VERTICAL_SLAB.asItem(), 400);
        fuels.add(PoBlocks.POOP_BUTTON.asItem(), 200);
        fuels.add(PoBlocks.POOP_PRESSURE_PLATE.asItem(), 400);
        fuels.add(PoBlocks.POOP_FENCE.asItem(), 800);
        fuels.add(PoBlocks.POOP_FENCE_GATE.asItem(), 800);
        fuels.add(PoBlocks.POOP_WALL.asItem(), 800);
        fuels.add(PoBlocks.POOP_DOOR.asItem(), 800);
        fuels.add(PoBlocks.POOP_TRAPDOOR.asItem(), 800);
        fuels.add(PoBlocks.POOP_LOG.asItem(), 800);
        fuels.add(PoBlocks.STRIPPED_POOP_LOG.asItem(), 800);
        fuels.add(PoBlocks.POOP_EMPTY_LOG.asItem(), 800);
        fuels.add(PoBlocks.STRIPPED_POOP_EMPTY_LOG.asItem(), 800);
        fuels.add(PoBlocks.POOP_PIECE.asItem(), 400);
        fuels.add(PoBlocks.STOOL.asItem(), 800);
    }
}
