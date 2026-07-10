package com.altnoir.poopsky.init;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidType;

public final class PFluidTypes {
    public static final ResourceLocation URINE_STILL_TEXTURE = PFluids.URINE_STILL_TEXTURE;
    public static final ResourceLocation URINE_FLOWING_TEXTURE = PFluids.URINE_FLOWING_TEXTURE;

    public static final RegistryEntry<FluidType, FluidType> URINE_FLUID_TYPE = PFluids.URINE_FLUID_TYPE;

    private PFluidTypes() {
    }
}
