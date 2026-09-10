package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.abysslib.reginth.Reginth;
import com.altnoir.poopsky.worldgen.feature.PoHugeFungusConfiguration;
import com.altnoir.poopsky.worldgen.feature.PoHugeFungusFeature;
import com.altnoir.abysslib.reginth.util.entry.RegistryEntry;
import com.altnoir.abysslib.reginth.util.nullness.NonNullSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;

public class PoFeatures {
    private static final Reginth REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<Feature<?>, Feature<PoHugeFungusConfiguration>> HUGE_PRIMO_FUNGUS = registerFeature("huge_primo_fungus");

    private static RegistryEntry<Feature<?>, Feature<PoHugeFungusConfiguration>> registerFeature(String name) {
        return REGISTRATE.simple(name, Registries.FEATURE, featureSupplier());
    }

    private static NonNullSupplier<Feature<PoHugeFungusConfiguration>> featureSupplier() {
        return () -> new PoHugeFungusFeature(PoHugeFungusConfiguration.CODEC);
    }

    public static void register() {
    }
}