package com.altnoir.poopsky.content.block;

import com.altnoir.poopsky.worldgen.PoConfigureFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public final class PoTreeGrower {
    public static final AbstractTreeGrower GINKGO = new AbstractMegaTreeGrower() {
        @Override protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean flowers) {
            return flowers ? PoConfigureFeatures.GINKGO_BEE_TREE : PoConfigureFeatures.GINKGO_TREE;
        }
        @Override protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource random) { return PoConfigureFeatures.MEGA_GINKGO_TREE; }
    };
    public static final AbstractTreeGrower PRIMO = simple(PoConfigureFeatures.PRIMO_FUNGUS);
    public static final AbstractTreeGrower GLOW_PRIMO = simple(PoConfigureFeatures.GLOW_PRIMO_FUNGUS);
    public static AbstractTreeGrower simple(ResourceKey<ConfiguredFeature<?, ?>> feature) {
        return new AbstractTreeGrower() {
            @Override protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean flowers) { return feature; }
        };
    }
    private PoTreeGrower() {}
}
