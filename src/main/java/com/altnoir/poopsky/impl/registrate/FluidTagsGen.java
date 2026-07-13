package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.init.PoFluids;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;

public final class FluidTagsGen {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private FluidTagsGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, FluidTagsGen::generate);
    }

    private static void generate(RegistrateTagsProvider.IntrinsicImpl<Fluid> provider) {
        provider.addTag(FluidTags.WATER)
                .add(PoFluids.URINE.get())
                .add(PoFluids.FLOWING_URINE.get());

        provider.addTag(PoTags.Fluids.FAN_PROCESSING_CATALYSTS_DIGESTING)
                .add(PoFluids.URINE.get())
                .add(PoFluids.FLOWING_URINE.get());
    }
}