package com.altnoir.poopsky.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.WeepingVinesFeature;

public class PoHugeFungusFeature extends Feature<PoHugeFungusConfiguration> {
    public PoHugeFungusFeature(Codec<PoHugeFungusConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<PoHugeFungusConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        PoHugeFungusConfiguration config = context.config();
        Block allowedBaseBlock = config.validBaseState().getBlock();
        BlockPos newOrigin = null;
        if (level.getBlockState(origin.below()).is(allowedBaseBlock)) {
            newOrigin = origin;
        }

        if (newOrigin == null) {
            return false;
        }

        int totalHeight = Mth.nextInt(random, 4, 13);
        if (random.nextInt(12) == 0) {
            totalHeight *= 2;
        }

        level.setBlock(origin, Blocks.AIR.defaultBlockState(), 260);
        this.placeStem(level, config, newOrigin, totalHeight);
        this.placeHat(level, random, config, newOrigin, totalHeight);
        return true;
    }

    private static boolean isReplaceable(
            WorldGenLevel level,
            BlockPos pos,
            PoHugeFungusConfiguration config,
            boolean checkNonReplaceablePlants
    ) {
        if (level.isStateAtPosition(pos, BlockBehaviour.BlockStateBase::canBeReplaced)) {
            return true;
        }
        return checkNonReplaceablePlants && config.replaceableBlocks().test(level, pos);
    }

    private void placeStem(
            WorldGenLevel level,
            PoHugeFungusConfiguration config,
            BlockPos surfaceOrigin,
            int totalHeight
    ) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        BlockState stem = config.stemState();

        for (int dy = 0; dy < totalHeight; dy++) {
            blockPos.setWithOffset(surfaceOrigin, 0, dy, 0);
            if (isReplaceable(level, blockPos, config, true)) {
                if (!level.getBlockState(blockPos.below()).isAir()) {
                    level.destroyBlock(blockPos, true);
                }
                level.setBlock(blockPos, stem, 3);
            }
        }
    }

    private void placeHat(
            WorldGenLevel level,
            RandomSource random,
            PoHugeFungusConfiguration config,
            BlockPos surfaceOrigin,
            int totalHeight
    ) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        boolean placeVines = config.hatState().is(Blocks.NETHER_WART_BLOCK);
        int hatHeight = Math.min(random.nextInt(1 + totalHeight / 3) + 5, totalHeight);
        int hatStartY = totalHeight - hatHeight;

        for (int dy = hatStartY; dy <= totalHeight; dy++) {
            int radius = dy < totalHeight - random.nextInt(3) ? 2 : 1;
            if (hatHeight > 8 && dy < hatStartY + 4) {
                radius = 3;
            }

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    boolean isEdgeX = dx == -radius || dx == radius;
                    boolean isEdgeZ = dz == -radius || dz == radius;
                    boolean inside = !isEdgeX && !isEdgeZ && dy != totalHeight;
                    boolean corner = isEdgeX && isEdgeZ;
                    boolean isHatBottom = dy < hatStartY + 3;
                    blockPos.setWithOffset(surfaceOrigin, dx, dy, dz);

                    if (isReplaceable(level, blockPos, config, false)) {
                        if (!level.getBlockState(blockPos.below()).isAir()) {
                            level.destroyBlock(blockPos, true);
                        }

                        if (isHatBottom) {
                            if (!inside) {
                                this.placeHatDropBlock(level, random, blockPos, config.hatState(), placeVines);
                            }
                        } else if (inside) {
                            this.placeHatBlock(
                                    level,
                                    random,
                                    config,
                                    blockPos,
                                    0.1F,
                                    0.2F,
                                    placeVines ? 0.1F : 0.0F
                            );
                        } else if (corner) {
                            this.placeHatBlock(
                                    level,
                                    random,
                                    config,
                                    blockPos,
                                    0.01F,
                                    0.7F,
                                    placeVines ? 0.083F : 0.0F
                            );
                        } else {
                            this.placeHatBlock(
                                    level,
                                    random,
                                    config,
                                    blockPos,
                                    5.0E-4F,
                                    0.98F,
                                    placeVines ? 0.07F : 0.0F
                            );
                        }
                    }
                }
            }
        }
    }

    private void placeHatBlock(
            LevelAccessor level,
            RandomSource random,
            PoHugeFungusConfiguration config,
            BlockPos.MutableBlockPos blockPos,
            float decorBlockProbability,
            float hatBlockProbability,
            float vinesProbability
    ) {
        if (config.decorState().isPresent() && random.nextFloat() < decorBlockProbability) {
            this.setBlock(level, blockPos, config.decorState().get());
        } else if (random.nextFloat() < hatBlockProbability) {
            this.setBlock(level, blockPos, config.hatState());
            if (random.nextFloat() < vinesProbability) {
                tryPlaceWeepingVines(blockPos, level, random);
            }
        }
    }

    private void placeHatDropBlock(LevelAccessor level, RandomSource random, BlockPos blockPos, BlockState hatState, boolean placeVines) {
        if (level.getBlockState(blockPos.below()).is(hatState.getBlock())) {
            this.setBlock(level, blockPos, hatState);
        } else if (random.nextFloat() < 0.15) {
            this.setBlock(level, blockPos, hatState);
            if (placeVines && random.nextInt(11) == 0) {
                tryPlaceWeepingVines(blockPos, level, random);
            }
        }
    }

    private static void tryPlaceWeepingVines(BlockPos hatBlockPos, LevelAccessor level, RandomSource random) {
        BlockPos.MutableBlockPos placePos = hatBlockPos.mutable().move(Direction.DOWN);
        if (level.isEmptyBlock(placePos)) {
            int goalVineHeight = Mth.nextInt(random, 1, 5);
            if (random.nextInt(7) == 0) {
                goalVineHeight *= 2;
            }
            WeepingVinesFeature.placeWeepingVinesColumn(level, random, placePos, goalVineHeight, 23, 25);
        }
    }
}