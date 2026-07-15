package com.altnoir.poopsky.impl.util;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public class PoFeatureUtil {
    public PoFeatureUtil() {
    }

    private static BlockPredicate simplePatchPredicate(List<Block> blocks) {
        BlockPredicate blockpredicate;
        if (!blocks.isEmpty()) {
            blockpredicate = BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), blocks));
        } else {
            blockpredicate = BlockPredicate.ONLY_IN_AIR_PREDICATE;
        }

        return blockpredicate;
    }

    public static RandomPatchConfiguration simpleRandomPatchConfiguration(int tries, int xzSpread, Holder<PlacedFeature> feature) {
        return new RandomPatchConfiguration(tries, xzSpread, 3, feature);
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F feature, FC config, List<Block> blocks, int tries, int xzSpread) {
        return simpleRandomPatchConfiguration(tries, xzSpread, PlacementUtils.filtered(feature, config, simplePatchPredicate(blocks)));
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F feature, FC config, List<Block> blocks) {
        return simplePatchConfiguration(feature, config, blocks, 3, 3);
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F feature, FC config) {
        return simplePatchConfiguration(feature, config, List.of(), 3, 3);
    }

}
