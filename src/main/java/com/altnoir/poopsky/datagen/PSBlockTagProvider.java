package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PBlocks;
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
        tag(PTags.Blocks.POOP_BLOCK).add(PBlocks.POOP_BLOCK.get());
        tag(PTags.Blocks.CHILI_POOP_BLOCK).add(PBlocks.CHILI_POOP_BLOCK.get());

        tag(PTags.Blocks.RAW_SAPLING_POOP_BLOCK)
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_SAPLING_POOP_BLOCK.get());
        tag(PTags.Blocks.RAW_SEA_POOP_BLOCK)
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get());
        tag(PTags.Blocks.RAW_WITHER_POOP_BLOCK)
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(PTags.Blocks.RAW_SEA_POOP_BLOCK)
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(Blocks.CRYING_OBSIDIAN);
        tag(PTags.Blocks.WATER_BLOCK)
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get());

        tag(PTags.Blocks.POOP_BLOCKS)
                .add(PBlocks.POOP_BLOCK.get())
                .add(PBlocks.CHILI_POOP_BLOCK.get())
                .add(PBlocks.GOLDEN_POOP_BLOCK.get());
        tag(PTags.Blocks.MAGGOTS_BLOCKS)
                .add(PBlocks.MAGGOTS_BLOCK.get());
        var poopBuildingBlocks = tag(PTags.Blocks.POOP_BUILDING_BLOCKS)
                .add(
                        PBlocks.POOP_SAPLING.get(),
                        PBlocks.POOP_LEAVES.get(),
                        PBlocks.POOP_LEAVES_IRON.get(),
                        PBlocks.POOP_LEAVES_GOLD.get(),
                        PBlocks.POOP_PIECE.get()
                );
        poopBuildingBlocks.add(
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
                        PBlocks.POOP_TRAPDOOR.get()
                );
        PBlocks.CHILI_POOP_FAMILY.blocks().forEach(block -> poopBuildingBlocks.add(block.get()));
        PBlocks.GOLDEN_POOP_FAMILY.blocks().forEach(block -> poopBuildingBlocks.add(block.get()));
        tag(PTags.Blocks.EMPTY_LOGS)
                .add(PBlocks.POOP_EMPTY_LOG.get())
                .add(PBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        tag(PTags.Blocks.TOILET_BLOCKS)
                .add(
                        PBlocks.WOODEN_TOILET.get(),
                        PBlocks.HARD_TOILET.get()
                );

        tag(PTags.Blocks.POOP_TNT_DESTROY)
                .addTag(BlockTags.FLOWERS)
                .addTag(BlockTags.LEAVES);
        tag(PTags.Blocks.POOP_TNT_REPLACEABLE).addTag(BlockTags.MOSS_REPLACEABLE);

        //基础标签
        tag(BlockTags.MOSS_REPLACEABLE)
                .add(PBlocks.POOP_BLOCK.get())
                .add(PBlocks.POOLIME_MAGGOTS_BLOCK.get())
                .add(PBlocks.CHILI_POOP_BLOCK.get())
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(BlockTags.BEACON_BASE_BLOCKS).addTag(PTags.Blocks.POOP_BLOCKS);

        tag(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON).addTag(PTags.Blocks.POOP_BLOCKS);
        PBlocks.getPoopCandleCakes().values().forEach(candleCake ->
                tag(BlockTags.CANDLE_CAKES).add(candleCake.get()));
        tag(BlockTags.CLIMBABLE)
                .add(PBlocks.ROUNDWORM_VINES.get())
                .add(PBlocks.ROUNDWORM_VINES_PLANT.get());
        tag(BlockTags.FALL_DAMAGE_RESETTING)
                .add(PBlocks.ROUNDWORM_VINES.get())
                .add(PBlocks.ROUNDWORM_VINES_PLANT.get());
        tag(BlockTags.DRAGON_IMMUNE)
                .add(PBlocks.HARD_TOILET.get());

        tag(BlockTags.DIRT)
                .addTag(PTags.Blocks.POOP_BLOCKS)
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PBlocks.RAW_WITHER_POOP_BLOCK.get());
        tag(BlockTags.SAND)
                .add(PBlocks.DRIED_POOP_BLOCK.get());

        tag(BlockTags.MUSHROOM_GROW_BLOCK)
                .addTag(PTags.Blocks.POOP_BLOCKS)
                .add(PBlocks.RAW_POOP_BLOCK.get())
                .add(PBlocks.RAW_SAPLING_POOP_BLOCK.get())
                .add(PBlocks.RAW_SEA_POOP_BLOCK.get())
                .add(PBlocks.RAW_WITHER_POOP_BLOCK.get())
                .add(PBlocks.POOP_LOG.get())
                .add(PBlocks.POOP_EMPTY_LOG.get())
                .add(PBlocks.STRIPPED_POOP_LOG.get())
                .add(PBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        PBlocks.WALL_TAG_FAMILIES.forEach(family -> tag(BlockTags.WALLS).add(family.wall().get()));
        tag(BlockTags.FLOWERS)
                .add(PBlocks.POOP_LEAVES.get())
                .addTag(PTags.Blocks.POOP_BUILDING_BLOCKS);

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
                .add(PBlocks.WOODEN_TOILET.get())
                .add(PBlocks.FLY_BARREL.get())
                .add(PBlocks.MAGGOTS.get());

        var mineableWithPickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        PBlocks.HARD_TOILET.get(),
                        PBlocks.BREEDING_CHEST.get(),
                        PBlocks.POOP_LOG.get(),
                        PBlocks.STRIPPED_POOP_LOG.get()
                );
        PBlocks.TILE_BLOCK_FAMILY.blocks().forEach(block -> mineableWithPickaxe.add(block.get()));
        mineableWithPickaxe.add(
                        PBlocks.SIEVE.get(),
                        PBlocks.PLACER.get(),
                        PBlocks.COMPOOPER.get(),
                        PBlocks.WATER_COMPOOPER.get(),
                        PBlocks.LAVA_COMPOOPER.get(),
                        PBlocks.POWDER_SNOW_COMPOOPER.get(),
                        PBlocks.URINE_COMPOOPER.get()
                );
        mineableWithPickaxe.add(PBlocks.POOP_BRICKS.get(), PBlocks.CRACKED_POOP_BRICKS.get());
        PBlocks.POOP_BRICK_FAMILY.blocks().stream().skip(1).forEach(block -> mineableWithPickaxe.add(block.get()));
        PBlocks.HARDENED_POOP_FAMILIES.stream().skip(1).forEach(family -> family.blocks().forEach(block -> mineableWithPickaxe.add(block.get())));

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(PBlocks.MAGGOTS_BLOCK.get())
                .add(PBlocks.ROUNDWORM_BLOCK.get())
                .addTag(PTags.Blocks.POOP_BUILDING_BLOCKS);

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(PBlocks.POOP_PIECE.get());

        tag(Tags.Blocks.VILLAGER_JOB_SITES)
                .add(
                        PBlocks.COMPOOPER.get(),
                        PBlocks.WATER_COMPOOPER.get(),
                        PBlocks.LAVA_COMPOOPER.get(),
                        PBlocks.POWDER_SNOW_COMPOOPER.get(),
                        PBlocks.URINE_COMPOOPER.get(),
                        PBlocks.WOODEN_TOILET.get(),
                        PBlocks.HARD_TOILET.get()
                );

        tag(PTags.Blocks.FAN_PROCESSING_CATALYSTS_DIGESTING).add(PBlocks.URINE_LIQUID.get());
    }
}