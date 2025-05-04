package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class PSModelProvider extends FabricModelProvider {
    public PSModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(PSBlocks.POOP_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(PSBlocks.POOP_LEAVES);
    }


    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(PSItems.POOP, Models.GENERATED);
        itemModelGenerator.register(PSItems.POOP_BALL, Models.GENERATED);
        itemModelGenerator.register(PSItems.SPALL, Models.GENERATED);
        itemModelGenerator.register(PSItems.TOILET_LINKER, Models.GENERATED);
        itemModelGenerator.register(PSItems.TOILET_PLUG, Models.GENERATED);
        itemModelGenerator.register(PSItems.LAWRENCE_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(PSItems.URINE_BOTTLE, Models.GENERATED);
    }
}
