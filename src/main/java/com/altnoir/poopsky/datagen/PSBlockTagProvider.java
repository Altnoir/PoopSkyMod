package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.AllToiletBlocks;
import com.altnoir.poopsky.block.PBlocks;
import com.altnoir.poopsky.tag.PBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PSBlockTagProvider extends BlockTagsProvider {
    public PSBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(PBlockTags.POOP_BLOCK).add(PBlocks.POOP_BLOCK.get());
        tag(PBlockTags.CHILI_POOP_BLOCK).add(PBlocks.CHILI_POOP_BLOCK.get());

        tag(PBlockTags.RAW_SAPLING_POOP_BLOCK)
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_SAPLING_POOP_BLOCK.get());
        tag(PBlockTags.RAW_SEA_POOP_BLOCK)
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get());
        tag(PBlockTags.RAW_WITHER_POOP_BLOCK)
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(PBlockTags.RAW_SEA_POOP_BLOCK)
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(Blocks.CRYING_OBSIDIAN);
        tag(PBlockTags.WATER_BLOCK)
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get());

        tag(PBlockTags.POOP_BLOCKS)
                .add(PBlocks.POOP_BLOCK.get())
                .add(PBlocks.CHILI_POOP_BLOCK.get())
                .add(PBlocks.GOLDEN_POOP_BLOCK.get());
        tag(PBlockTags.POOP_BUILDING_BLOCKS).add(POOP_BUILDING_BLOCKS);
        tag(PBlockTags.EMPTY_LOGS)
                .add(PBlocks.POOP_EMPTY_LOG.get())
                .add(PBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        tag(PBlockTags.TOILET_BLOCKS)
                .add(WOODEN_TOILETS)
                .add(HARD_TOILETS);
        tag(PBlockTags.GOLDEN_TOILET_BLOCKS)
                .add(AllToiletBlocks.RAINBOW_TOILET.get());

        tag(PBlockTags.POOP_TNT_DESTROY)
                .addTag(BlockTags.FLOWERS)
                .addTag(BlockTags.LEAVES);
        tag(PBlockTags.POOP_TNT_REPLACEABLE).addTag(BlockTags.MOSS_REPLACEABLE);

        //基础标签
        tag(BlockTags.MOSS_REPLACEABLE)
                .add(PBlocks.POOP_BLOCK.get())
                .add(PBlocks.POOLIME_POOP_BLOCK.get())
                .add(PBlocks.CHILI_POOP_BLOCK.get())
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(BlockTags.BEACON_BASE_BLOCKS).addTag(PBlockTags.POOP_BLOCKS);

        tag(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON).addTag(PBlockTags.POOP_BLOCKS);
        tag(BlockTags.CLIMBABLE)
                .add(PBlocks.ROUNDWORM_VINES.get())
                .add(PBlocks.ROUNDWORM_VINES_PLANT.get());
        tag(BlockTags.FALL_DAMAGE_RESETTING)
                .add(PBlocks.ROUNDWORM_VINES.get())
                .add(PBlocks.ROUNDWORM_VINES_PLANT.get());

        tag(BlockTags.DIRT)
                .addTag(PBlockTags.POOP_BLOCKS)
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(BlockTags.SAND)
                .add(PBlocks.DRIED_POOP_BLOCK.get());

        tag(BlockTags.MUSHROOM_GROW_BLOCK)
                .addTag(PBlockTags.POOP_BLOCKS)
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PBlocks.RAW_WITHER_POOP_BLOCK.get())
                .add(PBlocks.POOP_LOG.get())
                .add(PBlocks.POOP_EMPTY_LOG.get())
                .add(PBlocks.STRIPPED_POOP_LOG.get())
                .add(PBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        tag(BlockTags.WALLS)
                .add(PBlocks.POOP_WALL.get())
                .add(PBlocks.CHILI_POOP_WALL.get())
                .add(PBlocks.GOLDEN_POOP_WALL.get())
                .add(PBlocks.DRIED_POOP_BLOCK_WALL.get())
                .add(PBlocks.SMOOTH_POOP_BLOCK_WALL.get())
                .add(PBlocks.CUT_POOP_BLOCK_WALL.get())
                .add(PBlocks.TILE_BLOCK_WALL.get());
        tag(BlockTags.FLOWERS)
                .addTag(PBlockTags.TOILET_BLOCKS)
                .add(PBlocks.POOP_LEAVES.get())
                .addTag(PBlockTags.POOP_BUILDING_BLOCKS);

//        ToiletBlocks.BLOCKS.getEntries().stream()
//                .map(DeferredHolder::get)
//                .forEach(toilet -> {
//                    tag(PSBlockTags.TOILET_BLOCKS)
//                            .add(toilet);
//                });

        tag(BlockTags.LOGS)
                .add(PBlocks.POOP_LOG.get())
                .add(PBlocks.POOP_EMPTY_LOG.get())
                .add(PBlocks.STRIPPED_POOP_LOG.get())
                .add(PBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        tag(BlockTags.LEAVES)
                .add(PBlocks.POOP_LEAVES.get())
                .add(PBlocks.POOP_LEAVES_IRON.get())
                .add(PBlocks.POOP_LEAVES_GOLD.get());

        tag(BlockTags.FENCES).add(PBlocks.POOP_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(PBlocks.POOP_FENCE_GATE.get());

        tag(BlockTags.MOB_INTERACTABLE_DOORS)
                .add(PBlocks.POOP_DOOR.get());
        tag(BlockTags.DOORS)
                .add(PBlocks.POOP_DOOR.get());
        tag(BlockTags.TRAPDOORS)
                .add(PBlocks.POOP_TRAPDOOR.get());

        tag(BlockTags.CROPS)
                .add(PBlocks.MAGGOTS.get());
        tag(BlockTags.MAINTAINS_FARMLAND)
                .add(PBlocks.MAGGOTS.get());

        //工具标签
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(WOODEN_TOILETS)
                .add(PBlocks.MAGGOTS.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(HARD_TOILETS)
                .add(PBlocks.POOP_LOG.get())
                .add(PBlocks.STRIPPED_POOP_LOG.get())
                .add(HARDEN_POOP)
                .add(PBlocks.TILE_BLOCK.get())
                .add(PBlocks.TILE_BLOCK_STAIRS.get())
                .add(PBlocks.TILE_BLOCK_SLAB.get())
                .add(PBlocks.TILE_BLOCK_VERTICAL_SLAB.get())
                .add(PBlocks.TILE_BLOCK_WALL.get())
                .add(PBlocks.SIEVE.get())
                .add(PBlocks.PLACER.get())
                .add(PBlocks.COMPOOPER.get())
                .add(PBlocks.WATER_COMPOOPER.get())
                .add(PBlocks.LAVA_COMPOOPER.get())
                .add(PBlocks.POWDER_SNOW_COMPOOPER.get())
                .add(PBlocks.URINE_COMPOOPER.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .addTag(PBlockTags.POOP_BUILDING_BLOCKS);

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(PBlocks.POOP_PIECE.get());

        tag(Tags.Blocks.VILLAGER_JOB_SITES)
                .add(PBlocks.COMPOOPER.get())
                .add(PBlocks.WATER_COMPOOPER.get())
                .add(PBlocks.LAVA_COMPOOPER.get())
                .add(PBlocks.POWDER_SNOW_COMPOOPER.get())
                .add(PBlocks.URINE_COMPOOPER.get())
                .add(WOODEN_TOILETS)
                .add(HARD_TOILETS);
    }

    public static final Block[] POOP_BUILDING_BLOCKS = {
            PBlocks.POOP_SAPLING.get(),
            PBlocks.POOP_LEAVES.get(),
            PBlocks.POOP_LEAVES_IRON.get(),
            PBlocks.POOP_LEAVES_GOLD.get(),
            PBlocks.POOP_PIECE.get(),

            PBlocks.POOP_BLOCK.get(),
            PBlocks.POOP_STAIRS.get(),
            PBlocks.POOP_SLAB.get(),
            PBlocks.POOP_VERTICAL_SLAB.get(),
            PBlocks.POOP_BUTTON.get(),
            PBlocks.POOP_PRESSURE_PLATE.get(),
            PBlocks.POOP_FENCE.get(),
            PBlocks.POOP_FENCE_GATE.get(),
            PBlocks.POOP_WALL.get(),
            PBlocks.POOP_DOOR.get(),
            PBlocks.POOP_TRAPDOOR.get(),

            PBlocks.CHILI_POOP_BLOCK.get(),
            PBlocks.CHILI_POOP_STAIRS.get(),
            PBlocks.CHILI_POOP_SLAB.get(),
            PBlocks.CHILI_POOP_VERTICAL_SLAB.get(),
            PBlocks.CHILI_POOP_WALL.get(),

            PBlocks.GOLDEN_POOP_BLOCK.get(),
            PBlocks.GOLDEN_POOP_STAIRS.get(),
            PBlocks.GOLDEN_POOP_SLAB.get(),
            PBlocks.GOLDEN_POOP_VERTICAL_SLAB.get(),
            PBlocks.GOLDEN_POOP_WALL.get()
    };
    public static final Block[] HARDEN_POOP = {
            PBlocks.POOP_BRICKS.get(),
            PBlocks.CRACKED_POOP_BRICKS.get(),
            PBlocks.POOP_BRICK_STAIRS.get(),
            PBlocks.POOP_BRICK_SLAB.get(),
            PBlocks.POOP_BRICK_VERTICAL_SLAB.get(),
            PBlocks.POOP_BRICK_WALL.get(),
            PBlocks.MOSSY_POOP_BRICKS.get(),
            PBlocks.MOSSY_POOP_BRICK_STAIRS.get(),
            PBlocks.MOSSY_POOP_BRICK_SLAB.get(),
            PBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB.get(),
            PBlocks.MOSSY_POOP_BRICK_WALL.get(),
            PBlocks.DRIED_POOP_BLOCK.get(),
            PBlocks.DRIED_POOP_BLOCK_STAIRS.get(),
            PBlocks.DRIED_POOP_BLOCK_SLAB.get(),
            PBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB.get(),
            PBlocks.DRIED_POOP_BLOCK_WALL.get(),
            PBlocks.SMOOTH_POOP_BLOCK.get(),
            PBlocks.SMOOTH_POOP_BLOCK_STAIRS.get(),
            PBlocks.SMOOTH_POOP_BLOCK_SLAB.get(),
            PBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB.get(),
            PBlocks.SMOOTH_POOP_BLOCK_WALL.get(),
            PBlocks.CUT_POOP_BLOCK.get(),
            PBlocks.CUT_POOP_BLOCK_STAIRS.get(),
            PBlocks.CUT_POOP_BLOCK_SLAB.get(),
            PBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB.get(),
            PBlocks.CUT_POOP_BLOCK_WALL.get()
    };
    public static final Block[] WOODEN_TOILETS = {
            AllToiletBlocks.OAK_TOILET.get(),
            AllToiletBlocks.SPRUCE_TOILET.get(),
            AllToiletBlocks.BIRCH_TOILET.get(),
            AllToiletBlocks.JUNGLE_TOILET.get(),
            AllToiletBlocks.ACACIA_TOILET.get(),
            AllToiletBlocks.CHERRY_TOILET.get(),
            AllToiletBlocks.DARK_OAK_TOILET.get(),
            AllToiletBlocks.MANGROVE_TOILET.get(),
            AllToiletBlocks.BAMBOO_TOILET.get()
    };
    public static final Block[] HARD_TOILETS = {
            //石制
            AllToiletBlocks.STONE_TOILET.get(),
            AllToiletBlocks.COBBLESTONE_TOILET.get(),
            AllToiletBlocks.MOSSY_COBBLESTONE_TOILET.get(),
            AllToiletBlocks.SMOOTH_STONE_TOILET.get(),
            AllToiletBlocks.STONE_BRICK_TOILET.get(),
            AllToiletBlocks.MOSSY_STONE_BRICK_TOILET.get(),
            AllToiletBlocks.TILE_TOILET.get(),
            //混凝土
            AllToiletBlocks.WHITE_CONCRETE_TOILET.get(),
            AllToiletBlocks.ORANGE_CONCRETE_TOILET.get(),
            AllToiletBlocks.MAGENTA_CONCRETE_TOILET.get(),
            AllToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET.get(),
            AllToiletBlocks.YELLOW_CONCRETE_TOILET.get(),
            AllToiletBlocks.LIME_CONCRETE_TOILET.get(),
            AllToiletBlocks.PINK_CONCRETE_TOILET.get(),
            AllToiletBlocks.GRAY_CONCRETE_TOILET.get(),
            AllToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET.get(),
            AllToiletBlocks.CYAN_CONCRETE_TOILET.get(),
            AllToiletBlocks.PURPLE_CONCRETE_TOILET.get(),
            AllToiletBlocks.BLUE_CONCRETE_TOILET.get(),
            AllToiletBlocks.BROWN_CONCRETE_TOILET.get(),
            AllToiletBlocks.GREEN_CONCRETE_TOILET.get(),
            AllToiletBlocks.RED_CONCRETE_TOILET.get(),
            AllToiletBlocks.BLACK_CONCRETE_TOILET.get(),
            AllToiletBlocks.RAINBOW_TOILET.get()
    };
}
