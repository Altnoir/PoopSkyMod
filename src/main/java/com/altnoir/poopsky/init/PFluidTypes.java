package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class PFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, PoopSky.MOD_ID);

    public static final ResourceLocation URINE_STILL_TEXTURE = PoopSky.loc("block/urine_liquid");
    public static final ResourceLocation URINE_FLOWING_TEXTURE = PoopSky.loc("block/urine_liquid_flowing");

    public static final Supplier<FluidType> URINE_FLUID_TYPE = FLUID_TYPES.register("urine",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("block.poopsky.urine_liquid")
                    .fallDistanceModifier(0F)
                    .canExtinguish(true)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .canHydrate(true)
                    .canDrown(true)
                    .canPushEntity(true)
                    .lightLevel(7)
                    .density(3000)
                    .viscosity(6000)
            ) {
                @Override
                public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
                    if (reader instanceof Level level) {
                        return level.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
                    }
                    return true;
                }

                @Override
                public double motionScale(Entity entity) {
                    if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(PEffects.OMENER)) {
                        return 0.014D;
                    }
                    return 0.0023D;
                }
            }
    );
}