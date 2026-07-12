package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
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
        tag(PoTags.Blocks.POOP_BLOCK).add(PoBlocks.POOP_BLOCK.get());
        tag(PoTags.Blocks.CHILI_POOP_BLOCK).add(PoBlocks.CHILI_POOP_BLOCK.get());

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

        tag(PoTags.Blocks.POOP_TNT_DESTROY)
                .addTag(BlockTags.FLOWERS)
                .addTag(BlockTags.LEAVES);
        tag(PoTags.Blocks.POOP_TNT_REPLACEABLE).addTag(BlockTags.MOSS_REPLACEABLE);

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
                .add(PoBlocks.ROUNDWORM_VINES.get())
                .add(PoBlocks.ROUNDWORM_VINES_PLANT.get());
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
                .add(PoBlocks.POOP_EMPTY_LOG.get())
                .add(PoBlocks.STRIPPED_POOP_LOG.get())
                .add(PoBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        PoBlocks.WALL_TAG_FAMILIES.forEach(family -> tag(BlockTags.WALLS).add(family.wall().get()));
        tag(BlockTags.FLOWERS)
                .add(PoBlocks.POOP_LEAVES.get())
                .addTag(PoTags.Blocks.POOP_BUILDING_BLOCKS);

//        ToiletBlocks.BLOCKS.getEntries().stream()
//                .map(DeferredHolder::get)
//                .forEach(toilet -> {
//                    tag(PSBlockTags.TOILET_BLOCKS)
//                            .add(toilet);
//                });

        tag(BlockTags.LOGS)
                .add(PoBlocks.POOP_LOG.get())
                .add(PoBlocks.POOP_EMPTY_LOG.get())
                .add(PoBlocks.STRIPPED_POOP_LOG.get())
                .add(PoBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        tag(BlockTags.LEAVES)
                .add(PoBlocks.POOP_LEAVES.get())
                .add(PoBlocks.POOP_LEAVES_IRON.get())
                .add(PoBlocks.POOP_LEAVES_GOLD.get());

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
                .add(PoBlocks.WOODEN_TOILET.get())
                .add(PoBlocks.FLY_BARREL.get())
                .add(PoBlocks.MAGGOTS.get());

        var mineableWithPickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        PoBlocks.HARD_TOILET.get(),
                        PoBlocks.BREEDING_CHEST.get(),
                        PoBlocks.POOP_LOG.get(),
                        PoBlocks.STRIPPED_POOP_LOG.get()
                );
        PoBlocks.TILE_BLOCK_FAMILY.blocks().forEach(block -> mineableWithPickaxe.add(block.get()));
        mineableWithPickaxe.add(
                PoBlocks.SIEVE.get(),
                PoBlocks.PLACER.get(),
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
                        PoBlocks.RAW_WITHER_POOP_BLOCK.get()
                )
                .addTag(PoTags.Blocks.POOP_BUILDING_BLOCKS);

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(PoBlocks.POOP_PIECE.get());

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
}