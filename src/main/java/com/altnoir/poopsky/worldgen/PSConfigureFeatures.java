package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public class PSConfigureFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> POOP_TREE = registerKey("poop_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> MEGA_POOP_TREE = registerKey("mega_poop_tree");

    public static void boostrap(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, POOP_TREE, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(PSBlocks.POOP_BLOCK),
                new StraightTrunkPlacer(3, 1, 1),

                BlockStateProvider.of(PSBlocks.POOP_LEAVES),
                new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 114),
                new TwoLayersFeatureSize(2, 0, 2)
                )
                .dirtProvider(BlockStateProvider.of(Blocks.MUD))
                .forceDirt()
                .build()
        );
        register(context, MEGA_POOP_TREE, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(PSBlocks.POOP_BLOCK),
                new GiantTrunkPlacer(9, 2, 2),

                BlockStateProvider.of(PSBlocks.POOP_LEAVES),
                new RandomSpreadFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(0), ConstantIntProvider.create(3), 255),
                new TwoLayersFeatureSize(1, 1, 2)
                )
                .dirtProvider(BlockStateProvider.of(Blocks.MUD))
                .forceDirt()
                .build()
        );
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(PoopSky.MOD_ID, name));
    }
    public static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }}
