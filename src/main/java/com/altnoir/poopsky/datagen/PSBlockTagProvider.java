package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.tag.PSBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class PSBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public PSBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(PSBlockTags.POOP_BLOCKS)
                .add(POOP_BLOCKS);
        getOrCreateTagBuilder(PSBlockTags.TOILET_BLOCKS)
                .add(WOODEN_TOILETS)
                .add(CONCRETE_RAINBOW_TOILETS);


        getOrCreateTagBuilder(BlockTags.DIRT)
                .add(PSBlocks.POOP_BLOCK);
        getOrCreateTagBuilder(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)
                .add(PSBlocks.POOP_BLOCK);

        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                .add(PSBlocks.POOP_LOG)
                .add(PSBlocks.STRIPPED_POOP_LOG);
        getOrCreateTagBuilder(BlockTags.LOGS)
                .add(PSBlocks.POOP_LOG)
                .add(PSBlocks.STRIPPED_POOP_LOG);

        getOrCreateTagBuilder(BlockTags.WOODEN_FENCES).add(PSBlocks.POOP_FENCE);
        getOrCreateTagBuilder(BlockTags.FENCE_GATES).add(PSBlocks.POOP_FENCE_GATE);
        getOrCreateTagBuilder(BlockTags.WALLS).add(PSBlocks.POOP_WALL);

        getOrCreateTagBuilder(BlockTags.LEAVES)
                .add(PSBlocks.POOP_LEAVES);
        getOrCreateTagBuilder(BlockTags.FLOWERS)
                .addTag(PSBlockTags.TOILET_BLOCKS)
                .addTag(PSBlockTags.POOP_BLOCKS);

        //工具标签
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(WOODEN_TOILETS);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(CONCRETE_RAINBOW_TOILETS);

        getOrCreateTagBuilder(BlockTags.HOE_MINEABLE)
                .addTag(PSBlockTags.POOP_BLOCKS);
    }

    private static final Block[] POOP_BLOCKS = {
            PSBlocks.POOP_SAPLING,
            PSBlocks.POOP_LEAVES,
            PSBlocks.POOP_PIECE,
            PSBlocks.POOP_BLOCK,
            PSBlocks.POOP_STAIRS,
            PSBlocks.POOP_SLAB,
            PSBlocks.POOP_VERTICAL_SLAB,
            PSBlocks.POOP_BUTTON,
            PSBlocks.POOP_PRESSURE_PLATE,
            PSBlocks.POOP_FENCE,
            PSBlocks.POOP_FENCE_GATE,
            PSBlocks.POOP_WALL,
            PSBlocks.POOP_DOOR,
            PSBlocks.POOP_TRAPDOOR,
    };
    private static final Block[] WOODEN_TOILETS = {
            ToiletBlocks.OAK_TOILET,
            ToiletBlocks.SPRUCE_TOILET,
            ToiletBlocks.BIRCH_TOILET,
            ToiletBlocks.JUNGLE_TOILET,
            ToiletBlocks.ACACIA_TOILET,
            ToiletBlocks.CHERRY_TOILET,
            ToiletBlocks.DARK_OAK_TOILET,
            ToiletBlocks.MANGROVE_TOILET,
            ToiletBlocks.BAMBOO_TOILET
    };
    private static final Block[] CONCRETE_RAINBOW_TOILETS = {
            ToiletBlocks.WHITE_CONCRETE_TOILET,
            ToiletBlocks.ORANGE_CONCRETE_TOILET,
            ToiletBlocks.MAGENTA_CONCRETE_TOILET,
            ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET,
            ToiletBlocks.YELLOW_CONCRETE_TOILET,
            ToiletBlocks.LIME_CONCRETE_TOILET,
            ToiletBlocks.PINK_CONCRETE_TOILET,
            ToiletBlocks.GRAY_CONCRETE_TOILET,
            ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET,
            ToiletBlocks.CYAN_CONCRETE_TOILET,
            ToiletBlocks.PURPLE_CONCRETE_TOILET,
            ToiletBlocks.BLUE_CONCRETE_TOILET,
            ToiletBlocks.BROWN_CONCRETE_TOILET,
            ToiletBlocks.GREEN_CONCRETE_TOILET,
            ToiletBlocks.RED_CONCRETE_TOILET,
            ToiletBlocks.BLACK_CONCRETE_TOILET,
            ToiletBlocks.RAINBOW_TOILET
    };
}
