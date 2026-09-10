package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.abysslib.reginth.Reginth;
import com.altnoir.poopsky.init.PoFluids;
import com.altnoir.abysslib.reginth.providers.ProviderType;
import com.altnoir.abysslib.reginth.providers.ReginthTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;

public final class FluidTagsGen {
    private static final Reginth REGISTRATE = PoopSky.registrate();

    private FluidTagsGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, FluidTagsGen::generate);
    }

    private static void generate(ReginthTagsProvider.IntrinsicImpl<Fluid> provider) {
        provider.addTag(FluidTags.WATER)
                .add(PoFluids.URINE.get())
                .add(PoFluids.FLOWING_URINE.get());

        provider.addTag(PoTags.Fluids.FAN_PROCESSING_CATALYSTS_DIGESTING)
                .add(PoFluids.URINE.get())
                .add(PoFluids.FLOWING_URINE.get());
    }
}