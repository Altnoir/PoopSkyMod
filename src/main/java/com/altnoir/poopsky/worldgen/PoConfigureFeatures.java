package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.util.PoFeatureUtil;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.worldgen.feature.PoHugeFungusConfiguration;
import com.altnoir.poopsky.worldgen.foliage.RhombusFoliagePlacer;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import java.util.List;
import java.util.Optional;

public class PoConfigureFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_TREE = resourceKey("poop_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_POOP_TREE = resourceKey("mega_poop_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GINKGO_TREE = resourceKey("ginkgo_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_GINKGO_TREE = resourceKey("mega_ginkgo_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GINKGO_BEE_TREE = resourceKey("ginkgo_bee_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PRIMO_FUNGUS = resourceKey("primo_fungus");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOW_PRIMO_FUNGUS = resourceKey("glow_primo_fungus");

    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_VEGETATION = resourceKey("poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_PATCH_BONEMEAL = resourceKey("poop_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHILI_POOP_VEGETATION = resourceKey("chili_poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHILI_POOP_PATCH_BONEMEAL = resourceKey("chili_poop_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GOLDEN_POOP_VEGETATION = resourceKey("golden_poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GOLDEN_POOP_PATCH_BONEMEAL = resourceKey("golden_poop_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_SAND_VEGETATION = resourceKey("poop_sand_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_SAND_PATCH_BONEMEAL = resourceKey("poop_sand_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SALTPETER_PATCH = resourceKey("saltpeter_patch");

    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_SAPLING_POOP_VEGETATION = resourceKey("raw_sapling_poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_SAPLING_POOP_PATCH_BONEMEAL = resourceKey("raw_sapling_poop_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_SEA_POOP_VEGETATION = resourceKey("raw_sea_poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_SEA_POOP_PATCH_BONEMEAL = resourceKey("raw_sea_poop_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_WITHER_POOP_VEGETATION = resourceKey("raw_wither_poop_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAW_WITHER_POOP_PATCH_BONEMEAL = resourceKey("raw_wither_poop_patch_bonemeal");

    public static final ResourceKey<ConfiguredFeature<?, ?>> MYCELIUM_VEGETATION = resourceKey("mycelium_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYCELIUM_PATCH_BONEMEAL = resourceKey("mycelium_patch_bonemeal");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, POOP_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(PoBlocks.POOP_LOG.get()),
                        new StraightTrunkPlacer(3, 1, 1),
                        BlockStateProvider.simple(PoBlocks.POOP_LEAVES.get()),
                        new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 114),
                        new TwoLayersFeatureSize(2, 0, 2)
                )
                        .dirt(BlockStateProvider.simple(Blocks.MUD))
                        .forceDirt()
                        .build()
        );

        register(context, MEGA_POOP_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(PoBlocks.POOP_LOG.get()),
                        new GiantTrunkPlacer(12, 2, 14),

                        new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(PoBlocks.POOP_LEAVES.get().defaultBlockState(), 11)
                                .add(PoBlocks.POOP_LEAVES_IRON.get().defaultBlockState(), 1)
                                .add(PoBlocks.POOP_LEAVES_GOLD.get().defaultBlockState(), 1)
                                .build()),

                        new RhombusFoliagePlacer(ConstantInt.of(4), ConstantInt.of(1), UniformInt.of(13, 17)),
                        new TwoLayersFeatureSize(1, 1, 2)
                )
                        .decorators(ImmutableList.of(new AlterGroundDecorator(BlockStateProvider.simple(Blocks.MUD))))
                        .build()
        );

        register(context, GINKGO_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(PoBlocks.GINKGO_LOG.get()),
                new StraightTrunkPlacer(8, 2, 1),
                BlockStateProvider.simple(PoBlocks.GINKGO_LEAVES.get()),
                new RhombusFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), UniformInt.of(7, 8)),
                new TwoLayersFeatureSize(2, 0, 2)
        ).build());

        register(context, MEGA_GINKGO_TREE, Feature.TREE, (new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(PoBlocks.GINKGO_LOG.get()),
                new GiantTrunkPlacer(13, 2, 14),
                BlockStateProvider.simple(PoBlocks.GINKGO_LEAVES.get()),
                new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)),
                new TwoLayersFeatureSize(1, 1, 2))
        ).build());

        register(context, GINKGO_BEE_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(PoBlocks.GINKGO_LOG.get()),
                new StraightTrunkPlacer(8, 2, 1),
                BlockStateProvider.simple(PoBlocks.GINKGO_LEAVES.get()),
                new RhombusFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), UniformInt.of(7, 8)),
                new TwoLayersFeatureSize(2, 0, 2)
        ).decorators(List.of(new BeehiveDecorator(0.05F))).build());

        BlockPredicate blockpredicate = BlockPredicate.matchesBlocks(
                Blocks.OAK_SAPLING,
                Blocks.SPRUCE_SAPLING,
                Blocks.BIRCH_SAPLING,
                Blocks.JUNGLE_SAPLING,
                Blocks.ACACIA_SAPLING,
                Blocks.CHERRY_SAPLING,
                Blocks.DARK_OAK_SAPLING,
                Blocks.MANGROVE_PROPAGULE,
                Blocks.DANDELION,
                Blocks.TORCHFLOWER,
                Blocks.POPPY,
                Blocks.BLUE_ORCHID,
                Blocks.ALLIUM,
                Blocks.AZURE_BLUET,
                Blocks.RED_TULIP,
                Blocks.ORANGE_TULIP,
                Blocks.WHITE_TULIP,
                Blocks.PINK_TULIP,
                Blocks.OXEYE_DAISY,
                Blocks.CORNFLOWER,
                Blocks.WITHER_ROSE,
                Blocks.LILY_OF_THE_VALLEY,
                Blocks.BROWN_MUSHROOM,
                Blocks.RED_MUSHROOM,
                Blocks.WHEAT,
                Blocks.SUGAR_CANE,
                Blocks.ATTACHED_PUMPKIN_STEM,
                Blocks.ATTACHED_MELON_STEM,
                Blocks.PUMPKIN_STEM,
                Blocks.MELON_STEM,
                Blocks.LILY_PAD,
                Blocks.NETHER_WART,
                Blocks.COCOA,
                Blocks.CARROTS,
                Blocks.POTATOES,
                Blocks.CHORUS_PLANT,
                Blocks.CHORUS_FLOWER,
                Blocks.TORCHFLOWER_CROP,
                Blocks.PITCHER_CROP,
                Blocks.BEETROOTS,
                Blocks.SWEET_BERRY_BUSH,
                Blocks.WARPED_FUNGUS,
                Blocks.CRIMSON_FUNGUS,
                Blocks.WEEPING_VINES,
                Blocks.WEEPING_VINES_PLANT,
                Blocks.TWISTING_VINES,
                Blocks.TWISTING_VINES_PLANT,
                Blocks.CAVE_VINES,
                Blocks.CAVE_VINES_PLANT,
                Blocks.SPORE_BLOSSOM,
                Blocks.AZALEA,
                Blocks.FLOWERING_AZALEA,
                Blocks.MOSS_CARPET,
                Blocks.PINK_PETALS,
                Blocks.BIG_DRIPLEAF,
                Blocks.BIG_DRIPLEAF_STEM,
                Blocks.SMALL_DRIPLEAF,
                PoBlocks.PRIMO_FUNGUS.get(),
                PoBlocks.GLOW_PRIMO_FUNGUS.get(),
                PoBlocks.MUSHROOM_BED.get()
        );
        register(context, PRIMO_FUNGUS, PoFeatures.HUGE_PRIMO_FUNGUS.get(), new PoHugeFungusConfiguration(
                BlockTags.DIRT,
                PoBlocks.PRIMO_STEM.get().defaultBlockState(),
                PoBlocks.PRIMO_CAP.get().defaultBlockState(),
                Optional.of(Blocks.BUDDING_AMETHYST.defaultBlockState()),
                blockpredicate
        ));
        register(context, GLOW_PRIMO_FUNGUS, PoFeatures.HUGE_PRIMO_FUNGUS.get(), new PoHugeFungusConfiguration(
                BlockTags.DIRT,
                PoBlocks.PRIMO_STEM.get().defaultBlockState(),
                PoBlocks.GLOW_PRIMO_CAP.get().defaultBlockState(),
                Optional.empty(),
                blockpredicate
        ));

        register(context, POOP_VEGETATION, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        new WeightedStateProvider(
                                SimpleWeightedRandomList.<BlockState>builder()
                                        .add(PoBlocks.POOP_SAPLING.get().defaultBlockState(), 5)
                                        .add(PoBlocks.POOP_PIECE.get().defaultBlockState(), 1)
                                        .add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 2)
                                        .add(Blocks.RED_MUSHROOM.defaultBlockState(), 2)
                        )
                )
        );
        register(context, POOP_PATCH_BONEMEAL, Feature.VEGETATION_PATCH,
                vegetationPatch(PoTags.Blocks.POOP_PATCH, PoBlocks.POOP_BLOCK.get(), holdergetter.getOrThrow(POOP_VEGETATION))
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
                vegetationPatch(PoTags.Blocks.CHILI_POOP_PATCH, PoBlocks.CHILI_POOP_BLOCK.get(), holdergetter.getOrThrow(CHILI_POOP_VEGETATION))
        );
        register(context, GOLDEN_POOP_VEGETATION, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(PoBlocks.GINKGO_SAPLING.get()))
        );
        register(context, GOLDEN_POOP_PATCH_BONEMEAL, Feature.VEGETATION_PATCH,
                vegetationPatch(PoTags.Blocks.GOLDEN_POOP_PATCH, PoBlocks.GOLDEN_POOP_BLOCK.get(), holdergetter.getOrThrow(GOLDEN_POOP_VEGETATION))
        );
        register(context, POOP_SAND_VEGETATION, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        new WeightedStateProvider(
                                SimpleWeightedRandomList.<BlockState>builder()
                                        .add(Blocks.SUGAR_CANE.defaultBlockState(), 100)
                                        .add(Blocks.CACTUS.defaultBlockState(), 20)
                                        .add(Blocks.DEAD_BUSH.defaultBlockState(), 30)
                        )
                )
        );
        register(context, POOP_SAND_PATCH_BONEMEAL, Feature.VEGETATION_PATCH,
                vegetationPatch(PoTags.Blocks.POOP_SAND_PATCH, PoBlocks.POOP_SAND.get(), holdergetter.getOrThrow(POOP_SAND_VEGETATION))
        );

        register(context, SALTPETER_PATCH, Feature.RANDOM_PATCH,
                PoFeatureUtil.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(
                                new WeightedStateProvider(
                                        SimpleWeightedRandomList.<BlockState>builder()
                                                .add(PoBlocks.SALTPETER_CLUSTER.get().defaultBlockState(), 1)
                                                .add(PoBlocks.LARGE_SALTPETER_BUD.get().defaultBlockState(), 9)
                                                .add(PoBlocks.MEDIUM_SALTPETER_BUD.get().defaultBlockState(), 40)
                                                .add(PoBlocks.SMALL_SALTPETER_BUD.get().defaultBlockState(), 100)

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
                vegetationPatch(PoTags.Blocks.RAW_SAPLING_POOP_BLOCK, PoBlocks.RAW_SAPLING_POOP_BLOCK.get(),
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
                vegetationPatch(PoTags.Blocks.RAW_SEA_POOP_BLOCK, PoBlocks.RAW_SEA_POOP_BLOCK.get(),
                        holdergetter.getOrThrow(RAW_SEA_POOP_VEGETATION), 0.3F, 0.25F)
        );

        register(context, RAW_WITHER_POOP_VEGETATION, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.WITHER_ROSE))
        );
        register(context, RAW_WITHER_POOP_PATCH_BONEMEAL, Feature.VEGETATION_PATCH,
                vegetationPatch(PoTags.Blocks.RAW_WITHER_POOP_BLOCK, PoBlocks.RAW_WITHER_POOP_BLOCK.get(),
                        holdergetter.getOrThrow(RAW_WITHER_POOP_VEGETATION), 0.1F, 0.125F)
        );

        SimpleWeightedRandomList.Builder<BlockState> builder = SimpleWeightedRandomList.builder();
        for (int i = 1; i <= 4; i++) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                builder.add(PoBlocks.MUSHROOM_BED.get().defaultBlockState()
                        .setValue(PinkPetalsBlock.AMOUNT, i).setValue(PinkPetalsBlock.FACING, direction), 2);
            }
        }
        register(context, MYCELIUM_VEGETATION, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        new WeightedStateProvider(builder
                                .add(PoBlocks.GLOW_PRIMO_FUNGUS.get().defaultBlockState(), 8)
                                .add(PoBlocks.PRIMO_FUNGUS.get().defaultBlockState(), 12)
                                .add(PoBlocks.MYCELIUM_MAT.get().defaultBlockState().setValue(BlockStateProperties.DOWN, true), 25)
                                .add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 4)
                                .add(Blocks.RED_MUSHROOM.defaultBlockState(), 4)
                        ))
        );
        register(context, MYCELIUM_PATCH_BONEMEAL, Feature.VEGETATION_PATCH,
                vegetationPatch(PoTags.Blocks.MYCELIUM_REPLACEABLE, PoBlocks.MYCELIUM_BLOCK.get(),
                        holdergetter.getOrThrow(MYCELIUM_VEGETATION), 0.6F, 0.75F));
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