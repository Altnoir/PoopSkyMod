package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.treedecorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public class PSConfigureFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> POOP_TREE = registerKey("poop_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> MEGA_POOP_TREE = registerKey("mega_poop_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> POOP_VEGETATION = registerKey("poop_vegetation");
    public static final RegistryKey<ConfiguredFeature<?, ?>> POOP_PATCH_BONEMEAL = registerKey("poop_patch_bonemeal");

    public static void boostrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> registryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, POOP_TREE, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(PSBlocks.POOP_LOG),
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
                BlockStateProvider.of(PSBlocks.POOP_LOG),
                new GiantTrunkPlacer(9, 2, 2),

                BlockStateProvider.of(PSBlocks.POOP_LEAVES),
                new RandomSpreadFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(0), ConstantIntProvider.create(3), 255),
                new TwoLayersFeatureSize(1, 1, 2)
                )
                .decorators(ImmutableList.of(new AlterGroundTreeDecorator(BlockStateProvider.of(Blocks.MUD))))
                .forceDirt()
                .build()
        );

        register(context, POOP_VEGETATION, Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(
                new WeightedBlockStateProvider(
                        DataPool.<BlockState>builder()
                                .add(PSBlocks.POOP_SAPLING.getDefaultState(), 25)
                                .add(PSBlocks.POOP_PIECE.getDefaultState(), 50)
                                .add(Blocks.SHORT_GRASS.getDefaultState(), 25)
                ))
        );
        register(context, POOP_PATCH_BONEMEAL, Feature.VEGETATION_PATCH, new VegetationPatchFeatureConfig(
                BlockTags.MOSS_REPLACEABLE,
                BlockStateProvider.of(PSBlocks.POOP_BLOCK),
                PlacedFeatures.createEntry(registryEntryLookup.getOrThrow(POOP_VEGETATION)),
                VerticalSurfaceType.FLOOR,
                ConstantIntProvider.create(1),
                0.0F,
                5,
                0.6F,
                UniformIntProvider.create(1, 2),
                0.75F
                )
        );
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(PoopSky.MOD_ID, name));
    }
    public static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }}
