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
                .add(HARD_TOILETS);

        getOrCreateTagBuilder(BlockTags.DIRT)
                .add(PSBlocks.POOP_BLOCK);
        getOrCreateTagBuilder(BlockTags.SAND)
                .add(PSBlocks.DRIED_POOP_BLOCK);
        getOrCreateTagBuilder(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)
                .add(PSBlocks.POOP_BLOCK);

        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                .add(PSBlocks.POOP_LOG)
                .add(PSBlocks.POOP_EMPTY_LOG)
                .add(PSBlocks.STRIPPED_POOP_LOG)
                .add(PSBlocks.STRIPPED_POOP_EMPTY_LOG);

        getOrCreateTagBuilder(BlockTags.FLOWERS)
                .addTag(PSBlockTags.TOILET_BLOCKS)
                .addTag(PSBlockTags.POOP_BLOCKS);

        getOrCreateTagBuilder(PSBlockTags.CONVERTABLE_TO_MOSS)
                .add(PSBlocks.POOP_BLOCK);

        getOrCreateTagBuilder(BlockTags.MUSHROOM_GROW_BLOCK)
                .add(PSBlocks.POOP_BLOCK)
                .add(PSBlocks.POOP_BRICKS)
                .add(PSBlocks.MOSSY_POOP_BRICKS)
                .add(PSBlocks.DRIED_POOP_BLOCK)
                .add(PSBlocks.POOP_LOG)
                .add(PSBlocks.POOP_EMPTY_LOG)
                .add(PSBlocks.STRIPPED_POOP_LOG)
                .add(PSBlocks.STRIPPED_POOP_EMPTY_LOG);

        //方块基础标签
        getOrCreateTagBuilder(BlockTags.CROPS)
                .add(PSBlocks.MAGGOTS);
        getOrCreateTagBuilder(BlockTags.MAINTAINS_FARMLAND)
                .add(PSBlocks.MAGGOTS);
        getOrCreateTagBuilder(BlockTags.SAPLINGS)
                .add(PSBlocks.POOP_SAPLING);
        getOrCreateTagBuilder(BlockTags.LEAVES)
                .add(PSBlocks.POOP_LEAVES)
                .add(PSBlocks.POOP_LEAVES_IRON)
                .add(PSBlocks.POOP_LEAVES_GOLD);
        getOrCreateTagBuilder(BlockTags.LOGS)
                .add(PSBlocks.POOP_LOG)
                .add(PSBlocks.POOP_EMPTY_LOG)
                .add(PSBlocks.STRIPPED_POOP_LOG)
                .add(PSBlocks.STRIPPED_POOP_EMPTY_LOG);
        getOrCreateTagBuilder(BlockTags.STAIRS)
                .add(PSBlocks.POOP_STAIRS)
                .add(PSBlocks.POOP_BRICK_STAIRS)
                .add(PSBlocks.MOSSY_POOP_BRICK_STAIRS)
                .add(PSBlocks.DRIED_POOP_BLOCK_STAIRS)
                .add(PSBlocks.SMOOTH_POOP_BLOCK_STAIRS)
                .add(PSBlocks.CUT_POOP_BLOCK_STAIRS);
        getOrCreateTagBuilder(BlockTags.SLABS)
                .add(PSBlocks.POOP_SLAB)
                .add(PSBlocks.POOP_BRICK_SLAB)
                .add(PSBlocks.MOSSY_POOP_BRICK_SLAB)
                .add(PSBlocks.DRIED_POOP_BLOCK_SLAB)
                .add(PSBlocks.SMOOTH_POOP_BLOCK_SLAB)
                .add(PSBlocks.CUT_POOP_BLOCK_SLAB);
        getOrCreateTagBuilder(BlockTags.WALLS)
                .add(PSBlocks.POOP_WALL)
                .add(PSBlocks.POOP_BRICK_WALL)
                .add(PSBlocks.MOSSY_POOP_BRICK_WALL)
                .add(PSBlocks.DRIED_POOP_BLOCK_WALL)
                .add(PSBlocks.SMOOTH_POOP_BLOCK_WALL)
                .add(PSBlocks.CUT_POOP_BLOCK_WALL);
        getOrCreateTagBuilder(BlockTags.BUTTONS).add(PSBlocks.POOP_BUTTON);
        getOrCreateTagBuilder(BlockTags.PRESSURE_PLATES).add(PSBlocks.POOP_PRESSURE_PLATE);
        getOrCreateTagBuilder(BlockTags.FENCES).add(PSBlocks.POOP_FENCE);
        getOrCreateTagBuilder(BlockTags.FENCE_GATES).add(PSBlocks.POOP_FENCE_GATE);
        getOrCreateTagBuilder(BlockTags.DOORS).add(PSBlocks.POOP_DOOR);
        getOrCreateTagBuilder(BlockTags.TRAPDOORS).add(PSBlocks.POOP_TRAPDOOR);

        //工具标签
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(WOODEN_TOILETS)
                .add(PSBlocks.MAGGOTS);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(HARD_TOILETS)
                .add(PSBlocks.POOP_LOG)
                .add(PSBlocks.STRIPPED_POOP_LOG)
                .add(HARDEN_POOP)
                .add(PSBlocks.COMPOOPER);

        getOrCreateTagBuilder(BlockTags.HOE_MINEABLE)
                .addTag(PSBlockTags.POOP_BLOCKS);

        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(PSBlocks.POOP_PIECE);
    }

    public static final Block[] POOP_BLOCKS = {
            PSBlocks.POOP_SAPLING,
            PSBlocks.POOP_LEAVES,
            PSBlocks.POOP_LEAVES_IRON,
            PSBlocks.POOP_LEAVES_GOLD,
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
    public static final Block[] HARDEN_POOP = {
            PSBlocks.POOP_BRICKS,
            PSBlocks.CRACKED_POOP_BRICKS,
            PSBlocks.POOP_BRICK_STAIRS,
            PSBlocks.POOP_BRICK_SLAB,
            PSBlocks.POOP_BRICK_VERTICAL_SLAB,
            PSBlocks.POOP_BRICK_WALL,
            PSBlocks.MOSSY_POOP_BRICKS,
            PSBlocks.MOSSY_POOP_BRICK_STAIRS,
            PSBlocks.MOSSY_POOP_BRICK_SLAB,
            PSBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB,
            PSBlocks.MOSSY_POOP_BRICK_WALL,
            PSBlocks.DRIED_POOP_BLOCK,
            PSBlocks.DRIED_POOP_BLOCK_STAIRS,
            PSBlocks.DRIED_POOP_BLOCK_SLAB,
            PSBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB,
            PSBlocks.DRIED_POOP_BLOCK_WALL,
            PSBlocks.SMOOTH_POOP_BLOCK,
            PSBlocks.SMOOTH_POOP_BLOCK_STAIRS,
            PSBlocks.SMOOTH_POOP_BLOCK_SLAB,
            PSBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB,
            PSBlocks.SMOOTH_POOP_BLOCK_WALL,
            PSBlocks.CUT_POOP_BLOCK,
            PSBlocks.CUT_POOP_BLOCK_STAIRS,
            PSBlocks.CUT_POOP_BLOCK_SLAB,
            PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB,
            PSBlocks.CUT_POOP_BLOCK_WALL
    };
    public static final Block[] WOODEN_TOILETS = {
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
    public static final Block[] HARD_TOILETS = {
            //石制
            ToiletBlocks.STONE_TOILET,
            ToiletBlocks.COBBLESTONE_TOILET,
            ToiletBlocks.MOSSY_COBBLESTONE_TOILET,
            ToiletBlocks.SMOOTH_STONE_TOILET,
            ToiletBlocks.STONE_BRICK_TOILET,
            ToiletBlocks.MOSSY_STONE_BRICK_TOILET,
            //混凝土
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
