package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, PoopSky.MOD_ID);

    public static final Supplier<FlowingFluid> URINE = FLUIDS.register("urine",
            () -> new BaseFlowingFluid.Source(PFluids.URINE_FLUID_PROPERTIES) {
                @Override
                public int getTickDelay(LevelReader level) {
                    return 10;
                }
            });

    public static final Supplier<FlowingFluid> FLOWING_URINE = FLUIDS.register("flowing_urine",
            () -> new BaseFlowingFluid.Flowing(PFluids.URINE_FLUID_PROPERTIES) {
                @Override
                public int getTickDelay(LevelReader level) {
                    return 10;
                }
            });

    private static final BaseFlowingFluid.Properties URINE_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(PFluidTypes.URINE_FLUID_TYPE, URINE, FLOWING_URINE)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(1)
            .block(PBlocks.URINE_LIQUID)
            .bucket(PItems.URINE_BUCKET);
}