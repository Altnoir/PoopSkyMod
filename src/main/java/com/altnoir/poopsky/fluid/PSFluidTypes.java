package com.altnoir.poopsky.fluid;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class PSFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, PoopSky.MOD_ID);

    public static final ResourceLocation POOP_STILL_TEXTURE = PoopSky.loc("block/poop_liquid");
    public static final ResourceLocation POOP_FLOWING_TEXTURE = PoopSky.loc("block/poop_liquid_flowing");

    public static final Supplier<FluidType> POOP_FLUID_TYPE = FLUID_TYPES.register("poop",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("block.poopsky.poop_liquid")
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
                        return level.getGameRules().getBoolean(GameRules.RULE_LAVA_SOURCE_CONVERSION);
                    }
                    //Best guess fallback to default (false)
                    return super.canConvertToSource(state, reader, pos);
                }

                @Override
                public double motionScale(Entity entity) {
                    return 0.0023333333333333335D;
                }
            }
    );
}