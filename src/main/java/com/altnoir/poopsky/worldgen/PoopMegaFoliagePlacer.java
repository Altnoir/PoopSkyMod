package com.altnoir.poopsky.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class PoopMegaFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<PoopMegaFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> fillFoliagePlacerFields(instance)
                    .and(IntProvider.createValidatingCodec(0, 24).fieldOf("crown_height").forGetter(placer -> placer.crownHeight))
                    .apply(instance, PoopMegaFoliagePlacer::new)
    );

    private final IntProvider crownHeight;

    public PoopMegaFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider crownHeight) {
        super(radius, offset);
        this.crownHeight = crownHeight;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return PSFoliagePlacerType.POOP_MEGA_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(
            TestableWorld world,
            BlockPlacer placer,
            Random random,
            TreeFeatureConfig config,
            int trunkHeight,
            TreeNode treeNode,
            int foliageHeight,
            int radius,
            int offset
    ) {
        BlockPos blockPos = treeNode.getCenter();
        int halfHeight = foliageHeight / 2;
        int i = 0;

        for (int j = blockPos.getY() - foliageHeight + offset; j <= blockPos.getY() + offset; j++) {
            int k = blockPos.getY() - j;
            int e = k >= halfHeight ? foliageHeight - k : k;
            int l = radius + treeNode.getFoliageRadius() + MathHelper.floor((float)e / foliageHeight * 5.0F);
            int m;
            if (k > 0 && l == i && (j & 1) == 0) {
                m = l + 1;
            } else {
                m = l;
            }

            this.generateSquare(world, placer, random, config, new BlockPos(blockPos.getX(), j, blockPos.getZ()), m, 0, treeNode.isGiantTrunk());
            i = l;
        }
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return this.crownHeight.get(random);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx + dz >= 7 ? true : dx * dx + dz * dz > radius * radius;
    }
}
