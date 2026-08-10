package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BlockStateGen extends FabricModelProvider {
    public BlockStateGen(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        for (PoBlocks.BlockFamily family : PoBlocks.SIMPLE_MODEL_FAMILIES) {
            generators.family(family.block().get())
                    .stairs(family.stairs().get())
                    .slab(family.slab().get())
                    .wall(family.wall().get());
        }

        List<Block> cubes = List.of(
                PoBlocks.POOLIME_BLOCK.get(),
                PoBlocks.CRACKED_POOP_BRICKS.get(),
                PoBlocks.POOP_SAND.get(),
                PoBlocks.DRIED_CHILI_POOP_BLOCK.get(),
                PoBlocks.DRIED_GOLDEN_POOP_BLOCK.get(),
                PoBlocks.RAW_SAPLING_POOP_BLOCK.get(),
                PoBlocks.RAW_SEA_POOP_BLOCK.get(),
                PoBlocks.RAW_WITHER_POOP_BLOCK.get(),
                PoBlocks.POOP_LEAVES.get(),
                PoBlocks.POOP_LEAVES_GOLD.get(),
                PoBlocks.POOP_LEAVES_IRON.get(),
                PoBlocks.SALTPETER_BLOCK.get(),
                PoBlocks.ROUNDWORM_BLOCK.get()
        );
        cubes.forEach(generators::createTrivialCube);

        generators.createCrossBlockWithDefaultItem(
                PoBlocks.POOP_SAPLING.get(), BlockModelGenerators.TintState.NOT_TINTED);
        generators.createPlant(
                PoBlocks.GINKGO_SAPLING.get(),
                PoBlocks.POTTED_GINKGO_SAPLING.get(),
                BlockModelGenerators.TintState.NOT_TINTED);
        generators.createAmethystCluster(PoBlocks.SALTPETER_CLUSTER.get());
        generators.createAmethystCluster(PoBlocks.LARGE_SALTPETER_BUD.get());
        generators.createAmethystCluster(PoBlocks.MEDIUM_SALTPETER_BUD.get());
        generators.createAmethystCluster(PoBlocks.SMALL_SALTPETER_BUD.get());

        var craftingTable = PoBlocks.POOP_CRAFTING_TABLE.get();
        var craftingTableModel = ModelTemplates.SLAB_BOTTOM.create(craftingTable, new TextureMapping()
                .put(TextureSlot.BOTTOM, PoopSky.loc("block/poop_bricks"))
                .put(TextureSlot.SIDE, PoopSky.loc("block/poop_crafting_table_side"))
                .put(TextureSlot.TOP, PoopSky.loc("block/poop_crafting_table_top")), generators.modelOutput);
        generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(craftingTable, craftingTableModel));
        generators.delegateItemModel(craftingTable, craftingTableModel);

    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        ItemModelGen.generateAll(generators);
    }
}
