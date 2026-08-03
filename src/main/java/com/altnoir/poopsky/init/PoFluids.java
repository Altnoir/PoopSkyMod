package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.fluid.UrineLiquidBlock;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class PoFluids {
    public static final ResourceLocation URINE_STILL_TEXTURE = PoopSky.loc("block/urine_liquid");
    public static final ResourceLocation URINE_FLOWING_TEXTURE = PoopSky.loc("block/urine_liquid_flowing");
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final FluidEntry<SimpleFlowableFluid.Flowing> FLOWING_URINE = REGISTRATE
            .fluid("urine", URINE_STILL_TEXTURE, URINE_FLOWING_TEXTURE, PoFluids::createFlowingUrine)
            .source(PoFluids::createSourceUrine)
            .noBlock()
            .fluidProperties(properties -> properties
                    //.slopeFindDistance(2)
                    .levelDecreasePerBlock(1)
                    .block(PoFluids.URINE_LIQUID))
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .bucket()
            .model((ctx, prov) -> {
            })
            .build()
            .register();

    public static final RegistryEntry<Fluid, FlowingFluid> URINE = REGISTRATE.get("urine", Registries.FLUID);
    public static final ItemEntry<BucketItem> URINE_BUCKET = ItemEntry.cast(REGISTRATE.get("urine_bucket", Registries.ITEM));
    public static final BlockEntry<UrineLiquidBlock> URINE_LIQUID = REGISTRATE
            .block("urine_liquid", properties -> new UrineLiquidBlock(URINE.get(), urineLiquidProperties()))
            .blockstate((ctx, prov) -> {
            })
            .loot(RegistrateBlockLootTables::dropSelf)
            .register();

    private PoFluids() {
    }

    public static void register() {
    }

//    private static FluidType createUrineFluidType() {
//        return new FluidType(FluidType.Properties.create()
//                .descriptionId("block.poopsky.urine_liquid")
//                .fallDistanceModifier(0F)
//                .canExtinguish(true)
//                .supportsBoating(true)
//                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
//                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
//                .canHydrate(true)
//                .canDrown(true)
//                .canPushEntity(true)
//                .lightLevel(7)
//                .density(3000)
//                .viscosity(6000)
//        ) {
//            @Override
//            public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
//                if (reader instanceof Level level) {
//                    return level.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
//                }
//                return true;
//            }
//
//            @Override
//            public double motionScale(Entity entity) {
//                if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(PoEffects.OMENER)) {
//                    return 0.014D;
//                }
//                return 0.0023D;
//            }
//        };
//    }

    private static BlockBehaviour.Properties urineLiquidProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .replaceable()
                .noCollission()
                .randomTicks()
                .strength(100.0F)
                .lightLevel(state -> 7)
                .pushReaction(PushReaction.DESTROY)
                .noLootTable()
                .liquid()
                .sound(SoundType.EMPTY);
    }

    private static SimpleFlowableFluid.Source createSourceUrine(SimpleFlowableFluid.Properties properties) {
        return new SimpleFlowableFluid.Source(properties) {
            @Override
            public int getTickDelay(LevelReader level) {
                return 10;
            }
        };
    }

    private static SimpleFlowableFluid.Flowing createFlowingUrine(SimpleFlowableFluid.Properties properties) {
        return new SimpleFlowableFluid.Flowing(properties) {
            @Override
            public int getTickDelay(LevelReader level) {
                return 10;
            }
        };
    }
}
