package com.altnoir.poopsky.fluid;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PSFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, PoopSky.MOD_ID);

    public static final Supplier<FlowingFluid> POOP = FLUIDS.register("poop",
            () -> new BaseFlowingFluid.Source(PSFluids.POOP_FLUID_PROPERTIES) {
                @Override
                public int getTickDelay(LevelReader level) {
                    return 10;
                }
            });

    public static final Supplier<FlowingFluid> FLOWING_POOP = FLUIDS.register("flowing_poop",
            () -> new BaseFlowingFluid.Flowing(PSFluids.POOP_FLUID_PROPERTIES) {
                @Override
                public int getTickDelay(LevelReader level) {
                    return 10;
                }
            });

    private static final BaseFlowingFluid.Properties POOP_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(PSFluidTypes.POOP_FLUID_TYPE, POOP, FLOWING_POOP)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(1)
            .block(PSBlocks.POOP_LIQUID)
            .bucket(PSItems.URINE_BUCKET);
}