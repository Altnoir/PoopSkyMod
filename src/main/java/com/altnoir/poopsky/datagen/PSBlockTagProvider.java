package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.tag.PSBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
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
        tag(PSBlockTags.POOP_BLOCKS)
                .add(POOP_BLOCKS);

        tag(PSBlockTags.TOILET_BLOCKS)
                .add(WOODEN_TOILETS)
                .add(HARD_TOILETS);

        //基础标签
        tag(BlockTags.MOSS_REPLACEABLE)
                .add(PSBlocks.POOP_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(PSBlocks.POOP_PIECE.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(PSBlocks.COMPOOPER.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(PSBlocks.POOP_BLOCK.get());

        tag(BlockTags.DIRT)
                .add(PSBlocks.POOP_BLOCK.get());
        tag(BlockTags.MUSHROOM_GROW_BLOCK)
                .add(PSBlocks.POOP_BLOCK.get())
                .add(PSBlocks.POOP_LOG.get())
                .add(PSBlocks.POOP_EMPTY_LOG.get())
                .add(PSBlocks.STRIPPED_POOP_LOG.get())
                .add(PSBlocks.STRIPPED_POOP_EMPTY_LOG.get());
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
        tag(BlockTags.WALLS).add(PSBlocks.POOP_WALL.get());

        //工具标签
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(WOODEN_TOILETS);

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(HARD_TOILETS)
                .add(PSBlocks.POOP_LOG.get())
                .add(PSBlocks.STRIPPED_POOP_LOG.get())
                //.add(HARDEN_POOP)
                .add(PSBlocks.COMPOOPER.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .addTag(PSBlockTags.POOP_BLOCKS);

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(PSBlocks.POOP_PIECE.get());
    }

    public static final Block[] POOP_BLOCKS = {
            PSBlocks.POOP_SAPLING.get(),
            PSBlocks.POOP_LEAVES.get(),
            PSBlocks.POOP_LEAVES_IRON.get(),
            PSBlocks.POOP_LEAVES_GOLD.get(),
            PSBlocks.POOP_PIECE.get(),
            PSBlocks.POOP_BLOCK.get(),
//            PSBlocks.POOP_STAIRS,
//            PSBlocks.POOP_SLAB,
//            PSBlocks.POOP_VERTICAL_SLAB,
//            PSBlocks.POOP_BUTTON,
//            PSBlocks.POOP_PRESSURE_PLATE,
//            PSBlocks.POOP_FENCE,
//            PSBlocks.POOP_FENCE_GATE,
//            PSBlocks.POOP_WALL,
//            PSBlocks.POOP_DOOR,
//            PSBlocks.POOP_TRAPDOOR
    };
    /* public static final Block[] HARDEN_POOP = {
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
     };*/
    public static final Block[] WOODEN_TOILETS = {
            ToiletBlocks.OAK_TOILET.get(),
            ToiletBlocks.SPRUCE_TOILET.get(),
            ToiletBlocks.BIRCH_TOILET.get(),
            ToiletBlocks.JUNGLE_TOILET.get(),
            ToiletBlocks.ACACIA_TOILET.get(),
            ToiletBlocks.CHERRY_TOILET.get(),
            ToiletBlocks.DARK_OAK_TOILET.get(),
            ToiletBlocks.MANGROVE_TOILET.get(),
            ToiletBlocks.BAMBOO_TOILET.get()
    };
    public static final Block[] HARD_TOILETS = {
            //石制
            ToiletBlocks.STONE_TOILET.get(),
            ToiletBlocks.COBBLESTONE_TOILET.get(),
            ToiletBlocks.MOSSY_COBBLESTONE_TOILET.get(),
            ToiletBlocks.SMOOTH_STONE_TOILET.get(),
            ToiletBlocks.STONE_BRICK_TOILET.get(),
            ToiletBlocks.MOSSY_STONE_BRICK_TOILET.get(),
            //混凝土
            ToiletBlocks.WHITE_CONCRETE_TOILET.get(),
            ToiletBlocks.ORANGE_CONCRETE_TOILET.get(),
            ToiletBlocks.MAGENTA_CONCRETE_TOILET.get(),
            ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET.get(),
            ToiletBlocks.YELLOW_CONCRETE_TOILET.get(),
            ToiletBlocks.LIME_CONCRETE_TOILET.get(),
            ToiletBlocks.PINK_CONCRETE_TOILET.get(),
            ToiletBlocks.GRAY_CONCRETE_TOILET.get(),
            ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET.get(),
            ToiletBlocks.CYAN_CONCRETE_TOILET.get(),
            ToiletBlocks.PURPLE_CONCRETE_TOILET.get(),
            ToiletBlocks.BLUE_CONCRETE_TOILET.get(),
            ToiletBlocks.BROWN_CONCRETE_TOILET.get(),
            ToiletBlocks.GREEN_CONCRETE_TOILET.get(),
            ToiletBlocks.RED_CONCRETE_TOILET.get(),
            ToiletBlocks.BLACK_CONCRETE_TOILET.get(),
            ToiletBlocks.RAINBOW_TOILET.get()
    };
}
