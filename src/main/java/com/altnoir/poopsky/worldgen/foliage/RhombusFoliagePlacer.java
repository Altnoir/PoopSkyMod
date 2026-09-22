package com.altnoir.poopsky.worldgen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import java.util.ArrayList;
import java.util.List;

public class RhombusFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<RhombusFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> foliagePlacerParts(instance)
                    .and(UniformInt.codec(0, 24).fieldOf("crown_height").forGetter(p -> p.crownHeight))
                    .and(Codec.intRange(0, 4).optionalFieldOf("crown_jitter", 2).forGetter(p -> p.crownJitter))
                    .apply(instance, RhombusFoliagePlacer::new)
    );

    private final IntProvider crownHeight;
    private final int crownJitter;

    public RhombusFoliagePlacer(IntProvider radius,IntProvider crownHeight) {
        this(radius,  ConstantInt.of(1), crownHeight, 2);
    }

    public RhombusFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider crownHeight, int crownJitter) {
        super(radius, offset);
        this.crownHeight = crownHeight;
        this.crownJitter = crownJitter;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return PoFoliagePlacerTypes.RHOMBUS_FOLIAGE_PLACER.get();
    }

    @Override
    public int foliageHeight(RandomSource random, int trunkHeight, TreeConfiguration config) {
        int height = crownHeight.sample(random) + random.nextInt(crownJitter + 1) + random.nextInt(crownJitter + 1);
        return Math.clamp(height, 1, trunkHeight);
    }

    @Override
    protected void createFoliage(LevelSimulatedReader reader, FoliageSetter placer, RandomSource random,
                                 TreeConfiguration config, int trunkHeight, FoliageAttachment attachment,
                                 int foliageHeight, int radius, int offset) {
        var top = attachment.pos();
        var topY = top.getY() + offset;
        var trunkBottomY = top.getY() - trunkHeight;
        var bottomY = Math.max(topY - foliageHeight + 1, trunkBottomY + 2);
        var layers = topY - bottomY + 1;
        var trunkSize = attachment.doubleTrunk() ? 1 : 0;
        var halfSpan = Math.max((layers - 1) / 2.0F, 1.0F);

        for (var layer = 0; layer < layers; layer++) {
            var y = bottomY + layer;

            // 顶层只有正上方封顶
            if (layer == layers - 1) {
                for (var dx = 0; dx <= trunkSize; dx++) {
                    for (var dz = 0; dz <= trunkSize; dz++) {
                        tryPlaceLeaf(reader, placer, random, config, top.atY(y).offset(dx, 0, dz));
                    }
                }
                continue;
            }

            // 斜角叶只在树冠中段出现，且下方无叶时必放，避免出现悬空叶片
            var withDiagonals = layer >= 1 && layer <= layers - 3;

            for (var dx = -1; dx <= trunkSize + 1; dx++) {
                for (var dz = -1; dz <= trunkSize + 1; dz++) {
                    var insideX = dx >= 0 && dx <= trunkSize;
                    var insideZ = dz >= 0 && dz <= trunkSize;

                    if (insideX && insideZ) {
                        continue;
                    }

                    var leafPos = top.atY(y).offset(dx, 0, dz);

                    if (insideX || insideZ) {
                        tryPlaceLeaf(reader, placer, random, config, leafPos);
                    } else if (withDiagonals && (random.nextInt(4) != 0 || !placer.isSet(leafPos.below()))) {
                        tryPlaceLeaf(reader, placer, random, config, leafPos);
                    }
                }
            }

            // 宽度沿树冠长度线性收放（菱形剖面），中段不会出现等宽方柱
            var distFromEdge = Math.min(layer, layers - 1 - layer);
            var layerRadius = (int) Math.round(radius * distFromEdge / halfSpan);

            if (layerRadius > 0) {
                placeLeavesRow(reader, placer, random, config, top.atY(y), layerRadius, 0, attachment.doubleTrunk());
            }
        }

        placeBranchClusters(reader, placer, random, config, top, trunkBottomY, bottomY, trunkSize);
    }

    private static void placeBranchClusters(LevelSimulatedReader reader, FoliageSetter placer, RandomSource random,
                                            TreeConfiguration config, BlockPos top, int trunkBottomY, int canopyBottomY,
                                            int trunkSize) {
        // 短枝叶簇落在树冠下方 2~4 格处，过低时夹到树干下部，保证矮树也能长出短枝
        var branchY = Math.max(canopyBottomY - 2 - random.nextInt(3), trunkBottomY + 4);
        var bonusBranchY = Math.max(branchY - 2 - random.nextInt(3), trunkBottomY + 3);

        if (random.nextInt(5) != 0) {
            placeBranchCluster(reader, placer, random, config, top, branchY, trunkSize);
        }
        if (random.nextInt(3) != 0) {
            placeBranchCluster(reader, placer, random, config, top, bonusBranchY, trunkSize);
        }
    }

    private static void placeBranchCluster(LevelSimulatedReader reader, FoliageSetter placer, RandomSource random,
                                           TreeConfiguration config, BlockPos top, int y, int trunkSize) {
        var branches = 1 + random.nextInt(2);
        if (random.nextBoolean()) {
            branches += 1 + random.nextInt(2);
        }

        List<Direction> directions = new ArrayList<>(4);
        Direction.Plane.HORIZONTAL.forEach(directions::add);

        for (var i = 0; i < branches && !directions.isEmpty(); i++) {
            var direction = directions.remove(random.nextInt(directions.size()));
            for (var index = 0; index <= trunkSize; index++) {
                tryPlaceLeaf(reader, placer, random, config, trunkFace(top, y, direction, trunkSize, index));
            }
        }
    }

    private static BlockPos trunkFace(BlockPos top, int y, Direction direction, int trunkSize, int index) {
        var dx = direction.getStepX() > 0 ? trunkSize + 1 : direction.getStepX() < 0 ? -1 : index;
        var dz = direction.getStepZ() > 0 ? trunkSize + 1 : direction.getStepZ() < 0 ? -1 : index;
        return top.atY(y).offset(dx, 0, dz);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx == radius && dz == radius || random.nextBoolean();
    }
}
