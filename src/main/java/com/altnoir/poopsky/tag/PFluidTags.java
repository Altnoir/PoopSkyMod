package com.altnoir.poopsky.tag;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class PFluidTags {
    public static final TagKey<Fluid> FAN_PROCESSING_CATALYSTS_DIGESTING = create("fan_processing_catalysts/digesting");

    private static TagKey<Fluid> create(String name) {
        return TagKey.create(Registries.FLUID, PoopSky.loc(name));
    }
}
