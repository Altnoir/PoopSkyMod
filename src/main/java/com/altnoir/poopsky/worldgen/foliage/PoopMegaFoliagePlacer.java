package com.altnoir.poopsky.worldgen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class PoopMegaFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<PoopMegaFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> foliagePlacerParts(instance)
                    .and(UniformInt.codec(0, 24).fieldOf("crown_height").forGetter(p -> p.crownHeight))
                    .apply(instance, PoopMegaFoliagePlacer::new)
    );

    private final IntProvider crownHeight;

    public PoopMegaFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider crownHeight) {
        super(radius, offset);
        this.crownHeight = crownHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return PSFoliagePlacerTypes.POOP_MEGA_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader reader, FoliageSetter placer, RandomSource random,
                                 TreeConfiguration config, int trunkHeight, FoliageAttachment attachment,
                                 int foliageHeight, int radius, int offset) {
        var center = attachment.pos();
        var halfHeight = foliageHeight / 2;
        var prevRadius = 0;

        for (var y = center.getY() - foliageHeight + offset; y <= center.getY() + offset; ++y) {
            var dy = center.getY() - y;
            var spreadFactor = dy >= halfHeight ? foliageHeight - dy : dy;
            var currRadius = radius + attachment.radiusOffset() + Mth.floor((float)spreadFactor / foliageHeight * 5.0F);

            if (dy > 0 && currRadius == prevRadius && (y & 1) == 0) {
                currRadius++;
            }

            placeLeavesRow(reader, placer, random, config, new BlockPos(center.getX(), y, center.getZ()),
                    currRadius, 0, attachment.doubleTrunk());
            prevRadius = currRadius;
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
