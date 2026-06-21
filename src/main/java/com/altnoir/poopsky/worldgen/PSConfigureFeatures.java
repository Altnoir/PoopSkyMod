package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PBlocks;
import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.worldgen.foliage.PoopMegaFoliagePlacer;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class PSConfigureFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_TREE = resourceKey("poop_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_POOP_TREE = resourceKey("mega_poop_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_VEGETATION = resourceKey("poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_PATCH_BONEMEAL = resourceKey("poop_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHILI_POOP_VEGETATION = resourceKey("chili_poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHILI_POOP_PATCH_BONEMEAL = resourceKey("chili_poop_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DRIED_POOP_PATCH = resourceKey("dried_poop_patch");

    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_SAPLING_POOP_VEGETATION = resourceKey("raw_sapling_poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_SAPLING_POOP_PATCH_BONEMEAL = resourceKey("raw_sapling_poop_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_SEA_POOP_VEGETATION = resourceKey("raw_sea_poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_SEA_POOP_PATCH_BONEMEAL = resourceKey("raw_sea_poop_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_WITHER_POOP_VEGETATION = resourceKey("raw_wither_poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_WITHER_POOP_PATCH_BONEMEAL = resourceKey("raw_wither_poop_patch_bonemeal");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, POOP_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(PBlocks.POOP_LOG.get()),
                        new StraightTrunkPlacer(3, 1, 1),
                        BlockStateProvider.simple(PBlocks.POOP_LEAVES.get()),
                        new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 114),
                        new TwoLayersFeatureSize(2, 0, 2)
                )
                        .dirt(BlockStateProvider.simple(Blocks.MUD))
                        .forceDirt()
                        .build()
        );

        register(context, MEGA_POOP_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(PBlocks.POOP_LOG.get()),
                        new GiantTrunkPlacer(12, 2, 14),

                        new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(PBlocks.POOP_LEAVES.get().defaultBlockState(), 11)
                                .add(PBlocks.POOP_LEAVES_IRON.get().defaultBlockState(), 1)
                                .add(PBlocks.POOP_LEAVES_GOLD.get().defaultBlockState(), 1)
                                .build()),

                        new PoopMegaFoliagePlacer(ConstantInt.of(0), ConstantInt.of(3), UniformInt.of(13, 17)),
                        new TwoLayersFeatureSize(1, 1, 2)
                )
                        .decorators(ImmutableList.of(new AlterGroundDecorator(BlockStateProvider.simple(Blocks.MUD))))
                        .build()
        );

        register(context, POOP_VEGETATION, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        new WeightedStateProvider(
                                SimpleWeightedRandomList.<BlockState>builder()
                                        .add(PBlocks.POOP_SAPLING.get().defaultBlockState(), 5)
                                        .add(PBlocks.POOP_PIECE.get().defaultBlockState(), 1)
                                        .add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 2)
                                        .add(Blocks.RED_MUSHROOM.defaultBlockState(), 2)
                        )
                )
        );
        register(context, POOP_PATCH_BONEMEAL, Feature.VEGETATION_PATCH,
                vegetationPatch(PTags.Blocks.POOP_BLOCK, PBlocks.POOP_BLOCK.get(), holdergetter.getOrThrow(POOP_VEGETATION))
        );

        register(context, CHILI_POOP_VEGETATION, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        new WeightedStateProvider(
                                SimpleWeightedRandomList.<BlockState>builder()
                                        .add(Blocks.WARPED_FUNGUS.defaultBlockState(), 3)
                                        .add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 3)
                                        .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState(), 2)
                                        .add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 2)
                        )
                )
        );
        register(context, CHILI_POOP_PATCH_BONEMEAL, Feature.VEGETATION_PATCH,
                vegetationPatch(PTags.Blocks.CHILI_POOP_BLOCK, PBlocks.CHILI_POOP_BLOCK.get(), holdergetter.getOrThrow(CHILI_POOP_VEGETATION))
        );

        register(context, DRIED_POOP_PATCH, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(
                                new WeightedStateProvider(
                                        SimpleWeightedRandomList.<BlockState>builder()
                                                .add(Blocks.SUGAR_CANE.defaultBlockState(), 50)
                                                .add(Blocks.CACTUS.defaultBlockState(), 20)
                                                .add(Blocks.DEAD_BUSH.defaultBlockState(), 30)
                                )))
        );

        register(context, RAW_SAPLING_POOP_VEGETATION, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        new WeightedStateProvider(
                                SimpleWeightedRandomList.<BlockState>builder()
                                        .add(Blocks.DANDELION.defaultBlockState())
                                        .add(Blocks.POPPY.defaultBlockState())
                                        .add(Blocks.BLUE_ORCHID.defaultBlockState())
                                        .add(Blocks.ALLIUM.defaultBlockState())
                                        .add(Blocks.AZURE_BLUET.defaultBlockState())
                                        .add(Blocks.RED_TULIP.defaultBlockState())
                                        .add(Blocks.ORANGE_TULIP.defaultBlockState())
                                        .add(Blocks.WHITE_TULIP.defaultBlockState())
                                        .add(Blocks.PINK_TULIP.defaultBlockState())
                                        .add(Blocks.OXEYE_DAISY.defaultBlockState())
                                        .add(Blocks.CORNFLOWER.defaultBlockState())
                                        .add(Blocks.LILY_OF_THE_VALLEY.defaultBlockState())
                                        .add(Blocks.PINK_PETALS.defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, PinkPetalsBlock.MAX_FLOWERS))
                        ))
        );
        register(context, RAW_SAPLING_POOP_PATCH_BONEMEAL, Feature.VEGETATION_PATCH,
                vegetationPatch(PTags.Blocks.RAW_SAPLING_POOP_BLOCK, PBlocks.RAW_SAPLING_POOP_BLOCK.get(),
                        holdergetter.getOrThrow(RAW_SAPLING_POOP_VEGETATION), 0.3F, 0.25F)
        );
        register(context, RAW_SEA_POOP_VEGETATION, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        new WeightedStateProvider(
                                SimpleWeightedRandomList.<BlockState>builder()
                                        .add(Blocks.TUBE_CORAL.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, false))
                                        .add(Blocks.TUBE_CORAL_FAN.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, false))
                                        .add(Blocks.BRAIN_CORAL.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, false))
                                        .add(Blocks.BRAIN_CORAL_FAN.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, false))
                                        .add(Blocks.BUBBLE_CORAL.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, false))
                                        .add(Blocks.BUBBLE_CORAL_FAN.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, false))
                                        .add(Blocks.FIRE_CORAL.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, false))
                                        .add(Blocks.FIRE_CORAL_FAN.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, false))
                                        .add(Blocks.HORN_CORAL.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, false))
                                        .add(Blocks.HORN_CORAL_FAN.defaultBlockState().setValue(BaseCoralPlantTypeBlock.WATERLOGGED, false))
                        ))
        );
        register(context, RAW_SEA_POOP_PATCH_BONEMEAL, Feature.VEGETATION_PATCH,
                vegetationPatch(PTags.Blocks.RAW_SEA_POOP_BLOCK, PBlocks.RAW_SEA_POOP_BLOCK.get(),
                        holdergetter.getOrThrow(RAW_SEA_POOP_VEGETATION), 0.3F, 0.25F)
        );

        register(context, RAW_WITHER_POOP_VEGETATION, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.WITHER_ROSE))
        );
        register(context, RAW_WITHER_POOP_PATCH_BONEMEAL, Feature.VEGETATION_PATCH,
                vegetationPatch(PTags.Blocks.RAW_WITHER_POOP_BLOCK, PBlocks.RAW_WITHER_POOP_BLOCK.get(),
                        holdergetter.getOrThrow(RAW_WITHER_POOP_VEGETATION), 0.1F, 0.125F)
        );
    }

    private static VegetationPatchConfiguration vegetationPatch(TagKey<Block> replaceable, Block ground, Holder<ConfiguredFeature<?, ?>> feature) {
        return vegetationPatch(replaceable, ground, feature, 0.25F, 0.0F);
    }

    private static VegetationPatchConfiguration vegetationPatch(TagKey<Block> replaceable, Block ground, Holder<ConfiguredFeature<?, ?>> feature, float grow, float infection) {
        return new VegetationPatchConfiguration(
                replaceable,
                BlockStateProvider.simple(ground),
                PlacementUtils.inlinePlaced(feature),
                CaveSurface.FLOOR,
                ConstantInt.of(1), 0.0F, 5,
                grow, UniformInt.of(1, 2), infection
        );
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> resourceKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, PoopSky.loc(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
