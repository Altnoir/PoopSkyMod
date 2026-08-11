package com.altnoir.poopsky.init;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidType;

public final class PFluidTypes {
    public static final Identifier URINE_STILL_TEXTURE = PoFluids.URINE_STILL_TEXTURE;
    public static final Identifier URINE_FLOWING_TEXTURE = PoFluids.URINE_FLOWING_TEXTURE;

    public static final RegistryEntry<FluidType, FluidType> URINE_FLUID_TYPE = PoFluids.URINE_FLUID_TYPE;

    private PFluidTypes() {
    }
}
