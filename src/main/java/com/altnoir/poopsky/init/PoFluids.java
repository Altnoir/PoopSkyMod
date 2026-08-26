package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.fluid.UrineLiquidBlock;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
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

    public static final RegistryEntry<Fluid, FlowingFluid> URINE = REGISTRATE
            .simple("urine", Registries.FLUID, () -> createSourceUrine(urineProperties()));
    public static final RegistryEntry<Fluid, FlowingFluid> FLOWING_URINE = REGISTRATE
            .simple("flowing_urine", Registries.FLUID, () -> createFlowingUrine(urineProperties()));
    public static final ItemEntry<BucketItem> URINE_BUCKET = REGISTRATE
            .item("urine_bucket", properties -> new BucketItem(URINE.get(), properties))
            .properties(properties -> properties
                    .craftRemainder(Items.BUCKET)
                    .stacksTo(1))
            .model((ctx, prov) -> {
            })
            .register();
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

    private static SimpleFlowableFluid.Properties urineProperties() {
        return new SimpleFlowableFluid.Properties(URINE, FLOWING_URINE)
                .levelDecreasePerBlock(1)
                .bucket(URINE_BUCKET)
                .block(URINE_LIQUID);
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
