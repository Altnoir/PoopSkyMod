package com.altnoir.poopsky.worldgen.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class RhombusFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<RhombusFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> foliagePlacerParts(instance)
                    .and(UniformInt.codec(0, 24).fieldOf("crown_height").forGetter(p -> p.crownHeight))
                    .apply(instance, RhombusFoliagePlacer::new)
    );

    private final IntProvider crownHeight;

    public RhombusFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider crownHeight) {
        super(radius, offset);
        this.crownHeight = crownHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return PoFoliagePlacerTypes.RHOMBUS_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader reader, FoliageSetter placer, RandomSource random,
                                 TreeConfiguration config, int trunkHeight, FoliageAttachment attachment,
                                 int foliageHeight, int radius, int offset) {
        var center = attachment.pos();
        var startY = center.getY() - foliageHeight + offset;
        var endY = center.getY() + offset;
        var halfHeight = (endY - startY + 1) / 2.0F;

        for (var y = startY; y <= endY; ++y) {
            var distFromEdge = Math.min(y - startY, endY - y);
            var extraRadius = (int) Math.ceil(distFromEdge * radius / halfHeight);
            var currRadius = attachment.radiusOffset() + extraRadius;

            placeLeavesRow(reader, placer, random, config, new BlockPos(center.getX(), y, center.getZ()),
                    currRadius, 0, attachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int trunkHeight, TreeConfiguration config) {
        return crownHeight.sample(random);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx + dz >= 7 || dx * dx + dz * dz > radius * radius;
    }
}