package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.worldgen.foliage.PoopMegaFoliagePlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class PSConfigureFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_TREE = resourceKey("poop_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_POOP_TREE = resourceKey("mega_poop_tree");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, POOP_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(PSBlocks.POOP_LOG.get()),
                new StraightTrunkPlacer(3, 1, 1),
                new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                        .add(PSBlocks.POOP_LEAVES.get().defaultBlockState(), 11)
                        .add(PSBlocks.POOP_LEAVES_IRON.get().defaultBlockState(), 3)
                        .add(PSBlocks.POOP_LEAVES_GOLD.get().defaultBlockState(), 1)
                        .build()),
                new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 114),
                new TwoLayersFeatureSize(2, 0, 2)
                )
                .dirt(BlockStateProvider.simple(Blocks.MUD))
                .forceDirt()
                .build()
        );

        register(context, MEGA_POOP_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(PSBlocks.POOP_LOG.get()),
                new GiantTrunkPlacer(12, 2, 14),

                new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                        .add(PSBlocks.POOP_LEAVES.get().defaultBlockState(), 11)
                        .add(PSBlocks.POOP_LEAVES_IRON.get().defaultBlockState(), 3)
                        .add(PSBlocks.POOP_LEAVES_GOLD.get().defaultBlockState(), 1)
                        .build()),

                new PoopMegaFoliagePlacer(ConstantInt.of(0), ConstantInt.of(3), UniformInt.of(13, 17)),
                new TwoLayersFeatureSize(1, 1, 2)
                )
                .dirt(BlockStateProvider.simple(Blocks.MUD))
                .forceDirt()
                .build()
        );
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> resourceKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID,  name));
    }
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
