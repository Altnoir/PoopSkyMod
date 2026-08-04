package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoBlocks;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public final class BlockTagGen {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();
    private static RegistrateTagsProvider.IntrinsicImpl<Block> provider;

    private BlockTagGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, BlockTagGen::generate);
    }

    private static void generate(RegistrateTagsProvider.IntrinsicImpl<Block> provider) {
        BlockTagGen.provider = provider;
        tag(PoTags.Blocks.POOP_BLOCK).add(PoBlocks.POOP_BLOCK.get());
        tag(PoTags.Blocks.CHILI_POOP_BLOCK).add(PoBlocks.CHILI_POOP_BLOCK.get());
        tag(PoTags.Blocks.GINKGO_LOGS)
                .add(PoBlocks.GINKGO_LOG.get())
                .add(PoBlocks.STRIPPED_GINKGO_LOG.get())
                .add(PoBlocks.GINKGO_WOOD.get())
                .add(PoBlocks.STRIPPED_GINKGO_WOOD.get());

        tag(PoTags.Blocks.RAW_SAPLING_POOP_BLOCK)
                .add(PoBlocks.RAW_POOP_BLOCK.get())
                .add(PoBlocks.RAW_SAPLING_POOP_BLOCK.get());
        tag(PoTags.Blocks.RAW_SEA_POOP_BLOCK)
                .add(PoBlocks.RAW_POOP_BLOCK.get())
                .add(PoBlocks.RAW_SEA_POOP_BLOCK.get());
        tag(PoTags.Blocks.RAW_WITHER_POOP_BLOCK)
                .add(PoBlocks.RAW_POOP_BLOCK.get())
                .add(PoBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(PoTags.Blocks.RAW_SEA_POOP_BLOCK)
                .add(PoBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(Blocks.CRYING_OBSIDIAN);
        tag(PoTags.Blocks.WATER_BLOCK)
                .add(PoBlocks.RAW_SEA_POOP_BLOCK.get());

        tag(PoTags.Blocks.POOP_BLOCKS)
                .add(PoBlocks.POOP_BLOCK.get())
                .add(PoBlocks.CHILI_POOP_BLOCK.get())
                .add(PoBlocks.GOLDEN_POOP_BLOCK.get());
        tag(PoTags.Blocks.MAGGOTS_BLOCKS)
                .add(PoBlocks.MAGGOTS_BLOCK.get());
        var poopBuildingBlocks = tag(PoTags.Blocks.POOP_BUILDING_BLOCKS)
                .add(
                        PoBlocks.POOP_SAPLING.get(),
                        PoBlocks.POOP_LEAVES.get(),
                        PoBlocks.POOP_LEAVES_IRON.get(),
                        PoBlocks.POOP_LEAVES_GOLD.get(),
                        PoBlocks.POOP_PIECE.get()
                );
        poopBuildingBlocks.add(
                PoBlocks.POOP_BLOCK.get(),
                PoBlocks.POOP_STAIRS.get(),
                PoBlocks.POOP_SLAB.get(),
                PoBlocks.POOP_VERTICAL_SLAB.get(),
                PoBlocks.POOP_BUTTON.get(),
                PoBlocks.POOP_PRESSURE_PLATE.get(),
                PoBlocks.POOP_FENCE.get(),
                PoBlocks.POOP_FENCE_GATE.get(),
                PoBlocks.POOP_WALL.get(),
                PoBlocks.POOP_DOOR.get(),
                PoBlocks.POOP_TRAPDOOR.get()
        );
        PoBlocks.CHILI_POOP_FAMILY.blocks().forEach(block -> poopBuildingBlocks.add(block.get()));
        PoBlocks.GOLDEN_POOP_FAMILY.blocks().forEach(block -> poopBuildingBlocks.add(block.get()));
        tag(PoTags.Blocks.EMPTY_LOGS)
                .add(PoBlocks.POOP_EMPTY_LOG.get())
                .add(PoBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        tag(PoTags.Blocks.TOILET_BLOCKS)
                .add(
                        PoBlocks.WOODEN_TOILET.get(),
                        PoBlocks.HARD_TOILET.get()
                );
        tag(PoTags.Blocks.FLUSH_TOILET_BLOCKS)
                .add(
                        PoBlocks.FLUSH_TOILET.get(),
                        PoBlocks.GOLDEN_FLUSH_TOILET.get()
                );
        PoBlocks.COLORED_TILE_BLOCK_FAMILIES.forEach(family ->
                family.blocks().forEach(block -> tag(PoTags.Blocks.TILE_BLOCKS).add(block.get())));

        tag(PoTags.Blocks.POOP_TNT_DESTROY)
                .addTag(PoTags.Blocks.POOP_BLOCKS)
                .addTag(BlockTags.FLOWERS)
                .addTag(BlockTags.LEAVES);
        tag(PoTags.Blocks.POOP_TNT_REPLACEABLE).addTag(BlockTags.MOSS_REPLACEABLE);

        tag(PoTags.Blocks.FLY_LOVE)
                .add(
                        PoBlocks.SHIT.get(),
                        PoBlocks.CHILI_SHIT.get(),
                        PoBlocks.GOLDEN_SHIT.get()
                )
                .addTag(PoTags.Blocks.TOILET_BLOCKS);

        //基础标签
        tag(BlockTags.MOSS_REPLACEABLE)
                .add(PoBlocks.POOP_BLOCK.get())
                .add(PoBlocks.POOLIME_MAGGOTS_BLOCK.get())
                .add(PoBlocks.CHILI_POOP_BLOCK.get())
                .add(PoBlocks.RAW_POOP_BLOCK.get())
                .add(PoBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PoBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PoBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(BlockTags.BEACON_BASE_BLOCKS).addTag(PoTags.Blocks.POOP_BLOCKS);

        tag(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON).addTag(PoTags.Blocks.POOP_BLOCKS);
        PoBlocks.getPoopCandleCakes().values().forEach(candleCake ->
                tag(BlockTags.CANDLE_CAKES).add(candleCake.get()));
        tag(BlockTags.CLIMBABLE)
                .add(
                        PoBlocks.ROUNDWORM_VINES.get(),
                        PoBlocks.ROUNDWORM_VINES_PLANT.get(),
                        PoBlocks.CHILI_VINES.get(),
                        PoBlocks.CHILI_VINES_PLANT.get()
                );
        tag(BlockTags.FALL_DAMAGE_RESETTING)
                .add(PoBlocks.ROUNDWORM_VINES.get())
                .add(PoBlocks.ROUNDWORM_VINES_PLANT.get());
        tag(BlockTags.DRAGON_IMMUNE)
                .add(PoBlocks.HARD_TOILET.get());

        tag(BlockTags.DIRT)
                .addTag(PoTags.Blocks.POOP_BLOCKS)
                .add(PoBlocks.RAW_POOP_BLOCK.get())
                .add(PoBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PoBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PoBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(BlockTags.SAND)
                .add(PoBlocks.DRIED_POOP_BLOCK.get());

        tag(BlockTags.MUSHROOM_GROW_BLOCK)
                .addTag(PoTags.Blocks.POOP_BLOCKS)
                .add(PoBlocks.RAW_POOP_BLOCK.get())
                .add(PoBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PoBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PoBlocks.RAW_WITHER_POOP_BLOCK.get())
                .add(PoBlocks.POOP_LOG.get())
                .add(PoBlocks.POOP_WOOD.get())
                .add(PoBlocks.POOP_EMPTY_LOG.get())
                .add(PoBlocks.STRIPPED_POOP_LOG.get())
                .add(PoBlocks.STRIPPED_POOP_WOOD.get())
                .add(PoBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        PoBlocks.WALL_TAG_FAMILIES.forEach(family -> tag(BlockTags.WALLS).add(family.wall().get()));

        tag(BlockTags.LOGS)
                .add(
                        PoBlocks.POOP_LOG.get(),
                        PoBlocks.POOP_WOOD.get(),
                        PoBlocks.POOP_EMPTY_LOG.get(),
                        PoBlocks.STRIPPED_POOP_LOG.get(),
                        PoBlocks.STRIPPED_POOP_WOOD.get(),
                        PoBlocks.STRIPPED_POOP_EMPTY_LOG.get(),
                        PoBlocks.GINKGO_LOG.get(),
                        PoBlocks.GINKGO_WOOD.get(),
                        PoBlocks.STRIPPED_GINKGO_LOG.get(),
                        PoBlocks.STRIPPED_GINKGO_WOOD.get()
                );
        tag(Tags.Blocks.STRIPPED_LOGS)
                .add(PoBlocks.STRIPPED_POOP_LOG.get())
                .add(PoBlocks.STRIPPED_GINKGO_LOG.get());
        tag(Tags.Blocks.STRIPPED_WOODS)
                .add(PoBlocks.STRIPPED_POOP_WOOD.get())
                .add(PoBlocks.STRIPPED_GINKGO_WOOD.get());
        tag(BlockTags.LOGS_THAT_BURN).addTag(PoTags.Blocks.GINKGO_LOGS);
        tag(BlockTags.PLANKS).add(PoBlocks.GINKGO_PLANKS.get());
        tag(BlockTags.WOODEN_STAIRS).add(PoBlocks.GINKGO_STAIRS.get());
        tag(BlockTags.WOODEN_SLABS).add(PoBlocks.GINKGO_SLAB.get());
        tag(BlockTags.WOODEN_BUTTONS).add(PoBlocks.GINKGO_BUTTON.get());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(PoBlocks.GINKGO_PRESSURE_PLATE.get());
        tag(BlockTags.WOODEN_FENCES).add(PoBlocks.GINKGO_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(PoBlocks.GINKGO_FENCE_GATE.get());
        tag(BlockTags.WOODEN_DOORS).add(PoBlocks.GINKGO_DOOR.get());
        tag(BlockTags.WOODEN_TRAPDOORS).add(PoBlocks.GINKGO_TRAPDOOR.get());
        tag(BlockTags.SAPLINGS).add(PoBlocks.GINKGO_SAPLING.get());

        tag(BlockTags.LEAVES)
                .add(PoBlocks.POOP_LEAVES.get())
                .add(PoBlocks.POOP_LEAVES_IRON.get())
                .add(PoBlocks.POOP_LEAVES_GOLD.get())
                .add(PoBlocks.GINKGO_LEAVES.get());

        tag(BlockTags.FENCES).add(PoBlocks.POOP_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(PoBlocks.POOP_FENCE_GATE.get());

        tag(BlockTags.MOB_INTERACTABLE_DOORS)
                .add(PoBlocks.POOP_DOOR.get());
        tag(BlockTags.DOORS)
                .add(PoBlocks.POOP_DOOR.get());
        tag(BlockTags.TRAPDOORS)
                .add(PoBlocks.POOP_TRAPDOOR.get());

        tag(BlockTags.CROPS)
                .add(PoBlocks.MAGGOTS.get());
        tag(BlockTags.MAINTAINS_FARMLAND)
                .add(PoBlocks.MAGGOTS.get());

        //工具标签
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(
                        PoBlocks.WOODEN_TOILET.get(),
                        PoBlocks.FLY_BARREL.get(),
                        PoBlocks.FLY_BARREL.get(),
                        PoBlocks.MAGGOTS.get(),
                        PoBlocks.GINKGO_PLANKS.get(),
                        PoBlocks.GINKGO_STAIRS.get(),
                        PoBlocks.GINKGO_SLAB.get(),
                        PoBlocks.GINKGO_VERTICAL_SLAB.get(),
                        PoBlocks.GINKGO_BUTTON.get(),
                        PoBlocks.GINKGO_PRESSURE_PLATE.get(),
                        PoBlocks.GINKGO_FENCE.get(),
                        PoBlocks.GINKGO_FENCE_GATE.get(),
                        PoBlocks.GINKGO_DOOR.get(),
                        PoBlocks.GINKGO_TRAPDOOR.get(),
                        PoBlocks.GINKGO_TOILET.get()
                );

        var mineableWithPickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        PoBlocks.HARD_TOILET.get(),
                        PoBlocks.FLUSH_TOILET.get(),
                        PoBlocks.PORTABLE_TOILET.get(),
                        PoBlocks.GOLDEN_FLUSH_TOILET.get(),
                        PoBlocks.BREEDING_CHEST.get(),
                        PoBlocks.POOP_LOG.get(),
                        PoBlocks.POOP_WOOD.get(),
                        PoBlocks.STRIPPED_POOP_LOG.get(),
                        PoBlocks.STRIPPED_POOP_WOOD.get()
                );
        PoBlocks.COLORED_TILE_BLOCK_FAMILIES.forEach(family ->
                family.blocks().forEach(block -> mineableWithPickaxe.add(block.get())));
        mineableWithPickaxe.add(
                PoBlocks.SIEVE.get(),
                PoBlocks.PLACER.get(),
                PoBlocks.SALTPETER_BLOCK.get(),
                PoBlocks.SALTPETER_CLUSTER.get(),
                PoBlocks.LARGE_SALTPETER_BUD.get(),
                PoBlocks.MEDIUM_SALTPETER_BUD.get(),
                PoBlocks.SMALL_SALTPETER_BUD.get(),
                PoBlocks.COMPOOPER.get(),
                PoBlocks.WATER_COMPOOPER.get(),
                PoBlocks.LAVA_COMPOOPER.get(),
                PoBlocks.POWDER_SNOW_COMPOOPER.get(),
                PoBlocks.URINE_COMPOOPER.get()
        );
        mineableWithPickaxe.add(PoBlocks.POOP_BRICKS.get(), PoBlocks.CRACKED_POOP_BRICKS.get());
        PoBlocks.POOP_BRICK_FAMILY.blocks().stream().skip(1).forEach(block -> mineableWithPickaxe.add(block.get()));
        PoBlocks.HARDENED_POOP_FAMILIES.stream().skip(1).forEach(family -> family.blocks().forEach(block -> mineableWithPickaxe.add(block.get())));

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(PoBlocks.MAGGOTS_BLOCK.get(),
                        PoBlocks.ROUNDWORM_BLOCK.get(),
                        PoBlocks.RAW_POOP_BLOCK.get(),
                        PoBlocks.RAW_SAPLING_POOP_BLOCK.get(),
                        PoBlocks.RAW_SEA_POOP_BLOCK.get(),
                        PoBlocks.RAW_WITHER_POOP_BLOCK.get(),
                        PoBlocks.SHIT.get(),
                        PoBlocks.CHILI_SHIT.get(),
                        PoBlocks.GOLDEN_SHIT.get(),
                        PoBlocks.GINKGO_LEAVES.get(),
                        PoBlocks.POOP_LEAVES.get(),
                        PoBlocks.POOP_LEAVES_GOLD.get(),
                        PoBlocks.POOP_LEAVES_IRON.get()
                )
                .addTag(PoTags.Blocks.POOP_BUILDING_BLOCKS);

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(
                        PoBlocks.POOP_PIECE.get(),
                        PoBlocks.POOP_FARMLAND.get()
                );

        tag(Tags.Blocks.VILLAGER_JOB_SITES)
                .add(
                        PoBlocks.COMPOOPER.get(),
                        PoBlocks.WATER_COMPOOPER.get(),
                        PoBlocks.LAVA_COMPOOPER.get(),
                        PoBlocks.POWDER_SNOW_COMPOOPER.get(),
                        PoBlocks.URINE_COMPOOPER.get(),
                        PoBlocks.WOODEN_TOILET.get(),
                        PoBlocks.HARD_TOILET.get()
                );

        tag(PoTags.Blocks.FAN_PROCESSING_CATALYSTS_DIGESTING).add(PoBlocks.URINE_LIQUID.get());
    }

    private static IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag(TagKey<Block> tag) {
        return provider.addTag(tag);
    }
}
