package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.AllToiletBlocks;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.tag.PSBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PSBlockTagProvider extends BlockTagsProvider {
    public PSBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(PSBlockTags.POOP_BLOCK)
                .add(PSBlocks.POOP_BLOCK.get());
        tag(PSBlockTags.CHILI_POOP_BLOCK)
                .add(PSBlocks.CHILI_POOP_BLOCK.get());
        tag(PSBlockTags.RAW_SAPING_POOP_BLOCK)
                .add(PSBlocks.RAW_POOP_BLOCK.get())
                .add(PSBlocks.RAW_SAPLING_POOP_BLOCK.get());
        tag(PSBlockTags.RAW_SEA_POOP_BLOCK)
                .add(PSBlocks.RAW_POOP_BLOCK.get())
                .add(PSBlocks.RAW_SEA_POOP_BLOCK.get());
        tag(PSBlockTags.RAW_WITHER_POOP_BLOCK)
                .add(PSBlocks.RAW_POOP_BLOCK.get())
                .add(PSBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(PSBlockTags.RAW_SEA_POOP_BLOCK)
                .add(PSBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(Blocks.CRYING_OBSIDIAN);

        tag(PSBlockTags.POOP_BLOCKS).add(POOP_BLOCKS);
        tag(PSBlockTags.EMPTY_LOGS)
                .add(PSBlocks.POOP_EMPTY_LOG.get())
                .add(PSBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        tag(PSBlockTags.TOILET_BLOCKS)
                .add(WOODEN_TOILETS)
                .add(HARD_TOILETS);
        tag(PSBlockTags.GOLDEN_TOILET_BLOCKS)
                .add(AllToiletBlocks.RAINBOW_TOILET.get());

        //基础标签
        tag(BlockTags.MOSS_REPLACEABLE)
                .add(PSBlocks.POOP_BLOCK.get())
                .add(PSBlocks.POOLIME_POOP_BLOCK.get())
                .add(PSBlocks.CHILI_POOP_BLOCK.get())
                .add(PSBlocks.RAW_POOP_BLOCK.get())
                .add(PSBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PSBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PSBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(BlockTags.BEACON_BASE_BLOCKS)
                .add(PSBlocks.POOP_BLOCK.get())
                .add(PSBlocks.CHILI_POOP_BLOCK.get())
                .add(PSBlocks.DRIED_POOP_BLOCK.get());

        tag(BlockTags.CLIMBABLE)
                .add(PSBlocks.ROUNDWORM_VINES.get())
                .add(PSBlocks.ROUNDWORM_VINES_PLANT.get());
        tag(BlockTags.FALL_DAMAGE_RESETTING)
                .add(PSBlocks.ROUNDWORM_VINES.get())
                .add(PSBlocks.ROUNDWORM_VINES_PLANT.get());

        tag(BlockTags.DIRT)
                .add(PSBlocks.POOP_BLOCK.get())
                .add(PSBlocks.CHILI_POOP_BLOCK.get())
                .add(PSBlocks.RAW_POOP_BLOCK.get())
                .add(PSBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PSBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PSBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(BlockTags.SAND)
                .add(PSBlocks.DRIED_POOP_BLOCK.get());

        tag(BlockTags.MUSHROOM_GROW_BLOCK)
                .add(PSBlocks.POOP_BLOCK.get())
                .add(PSBlocks.CHILI_POOP_BLOCK.get())
                .add(PSBlocks.RAW_POOP_BLOCK.get())
                .add(PSBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PSBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PSBlocks.RAW_WITHER_POOP_BLOCK.get())
                .add(PSBlocks.POOP_LOG.get())
                .add(PSBlocks.POOP_EMPTY_LOG.get())
                .add(PSBlocks.STRIPPED_POOP_LOG.get())
                .add(PSBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        tag(BlockTags.WALLS)
                .add(PSBlocks.POOP_WALL.get())
                .add(PSBlocks.CHILI_POOP_WALL.get())
                .add(PSBlocks.GOLDEN_POOP_WALL.get())
                .add(PSBlocks.DRIED_POOP_BLOCK_WALL.get())
                .add(PSBlocks.SMOOTH_POOP_BLOCK_WALL.get())
                .add(PSBlocks.CUT_POOP_BLOCK_WALL.get())
                .add(PSBlocks.TILE_BLOCK_WALL.get());
        tag(BlockTags.FLOWERS)
                .addTag(PSBlockTags.TOILET_BLOCKS)
                .add(PSBlocks.POOP_LEAVES.get())
                .addTag(PSBlockTags.POOP_BLOCKS);

//        ToiletBlocks.BLOCKS.getEntries().stream()
//                .map(DeferredHolder::get)
//                .forEach(toilet -> {
//                    tag(PSBlockTags.TOILET_BLOCKS)
//                            .add(toilet);
//                });

        tag(BlockTags.LOGS)
                .add(PSBlocks.POOP_LOG.get())
                .add(PSBlocks.POOP_EMPTY_LOG.get())
                .add(PSBlocks.STRIPPED_POOP_LOG.get())
                .add(PSBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        tag(BlockTags.LEAVES)
                .add(PSBlocks.POOP_LEAVES.get())
                .add(PSBlocks.POOP_LEAVES_IRON.get())
                .add(PSBlocks.POOP_LEAVES_GOLD.get());

        tag(BlockTags.FENCES).add(PSBlocks.POOP_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(PSBlocks.POOP_FENCE_GATE.get());

        tag(BlockTags.MOB_INTERACTABLE_DOORS)
                .add(PSBlocks.POOP_DOOR.get());
        tag(BlockTags.DOORS)
                .add(PSBlocks.POOP_DOOR.get());
        tag(BlockTags.TRAPDOORS)
                .add(PSBlocks.POOP_TRAPDOOR.get());

        tag(BlockTags.CROPS)
                .add(PSBlocks.MAGGOTS.get());
        tag(BlockTags.MAINTAINS_FARMLAND)
                .add(PSBlocks.MAGGOTS.get());

        //工具标签
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(WOODEN_TOILETS)
                .add(PSBlocks.MAGGOTS.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(HARD_TOILETS)
                .add(PSBlocks.POOP_LOG.get())
                .add(PSBlocks.STRIPPED_POOP_LOG.get())
                .add(HARDEN_POOP)
                .add(PSBlocks.SIEVE.get())
                .add(PSBlocks.PLACER.get())
                .add(PSBlocks.COMPOOPER.get())
                .add(PSBlocks.WATER_COMPOOPER.get())
                .add(PSBlocks.LAVA_COMPOOPER.get())
                .add(PSBlocks.POWER_SNOW_COMPOOPER.get())
                .add(PSBlocks.URINE_COMPOOPER.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .addTag(PSBlockTags.POOP_BLOCKS);

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(PSBlocks.POOP_PIECE.get());

        tag(Tags.Blocks.VILLAGER_JOB_SITES)
                .add(PSBlocks.COMPOOPER.get())
                .add(PSBlocks.WATER_COMPOOPER.get())
                .add(PSBlocks.LAVA_COMPOOPER.get())
                .add(PSBlocks.POWER_SNOW_COMPOOPER.get())
                .add(PSBlocks.URINE_COMPOOPER.get())
                .add(WOODEN_TOILETS)
                .add(HARD_TOILETS);
    }

    public static final Block[] POOP_BLOCKS = {
            PSBlocks.POOP_SAPLING.get(),
            PSBlocks.POOP_LEAVES.get(),
            PSBlocks.POOP_LEAVES_IRON.get(),
            PSBlocks.POOP_LEAVES_GOLD.get(),
            PSBlocks.POOP_PIECE.get(),
            PSBlocks.POOP_BLOCK.get(),
            PSBlocks.POOP_STAIRS.get(),
            PSBlocks.POOP_SLAB.get(),
            PSBlocks.POOP_VERTICAL_SLAB.get(),
            PSBlocks.POOP_BUTTON.get(),
            PSBlocks.POOP_PRESSURE_PLATE.get(),
            PSBlocks.POOP_FENCE.get(),
            PSBlocks.POOP_FENCE_GATE.get(),
            PSBlocks.POOP_WALL.get(),
            PSBlocks.POOP_DOOR.get(),
            PSBlocks.POOP_TRAPDOOR.get(),
            PSBlocks.CHILI_POOP_BLOCK.get(),
            PSBlocks.CHILI_POOP_STAIRS.get(),
            PSBlocks.CHILI_POOP_SLAB.get(),
            PSBlocks.CHILI_POOP_VERTICAL_SLAB.get(),
            PSBlocks.CHILI_POOP_WALL.get(),
            PSBlocks.GOLDEN_POOP_BLOCK.get(),
            PSBlocks.GOLDEN_POOP_STAIRS.get(),
            PSBlocks.GOLDEN_POOP_SLAB.get(),
            PSBlocks.GOLDEN_POOP_VERTICAL_SLAB.get(),
            PSBlocks.GOLDEN_POOP_WALL.get()
    };
    public static final Block[] HARDEN_POOP = {
            PSBlocks.POOP_BRICKS.get(),
            PSBlocks.CRACKED_POOP_BRICKS.get(),
            PSBlocks.POOP_BRICK_STAIRS.get(),
            PSBlocks.POOP_BRICK_SLAB.get(),
            PSBlocks.POOP_BRICK_VERTICAL_SLAB.get(),
            PSBlocks.POOP_BRICK_WALL.get(),
            PSBlocks.MOSSY_POOP_BRICKS.get(),
            PSBlocks.MOSSY_POOP_BRICK_STAIRS.get(),
            PSBlocks.MOSSY_POOP_BRICK_SLAB.get(),
            PSBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB.get(),
            PSBlocks.MOSSY_POOP_BRICK_WALL.get(),
            PSBlocks.DRIED_POOP_BLOCK.get(),
            PSBlocks.DRIED_POOP_BLOCK_STAIRS.get(),
            PSBlocks.DRIED_POOP_BLOCK_SLAB.get(),
            PSBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB.get(),
            PSBlocks.DRIED_POOP_BLOCK_WALL.get(),
            PSBlocks.SMOOTH_POOP_BLOCK.get(),
            PSBlocks.SMOOTH_POOP_BLOCK_STAIRS.get(),
            PSBlocks.SMOOTH_POOP_BLOCK_SLAB.get(),
            PSBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB.get(),
            PSBlocks.SMOOTH_POOP_BLOCK_WALL.get(),
            PSBlocks.CUT_POOP_BLOCK.get(),
            PSBlocks.CUT_POOP_BLOCK_STAIRS.get(),
            PSBlocks.CUT_POOP_BLOCK_SLAB.get(),
            PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB.get(),
            PSBlocks.CUT_POOP_BLOCK_WALL.get()
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
