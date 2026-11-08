package com.altnoir.poopsky.worldgen.foliage;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class PoFoliagePlacerTypes {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<FoliagePlacerType<RhombusFoliagePlacer>> RHOMBUS_FOLIAGE_PLACER =
            registerFoliagePlacer("rhombus_foliage_placer", RhombusFoliagePlacer.CODEC);

    private static <T extends FoliagePlacer> RegistryEntry<FoliagePlacerType<T>> registerFoliagePlacer(String name, Codec<T> codec) {
        return REGISTRATE.simple(name, Registries.FOLIAGE_PLACER_TYPE, () -> new FoliagePlacerType<>(codec));
    }

    public static void register() {
    }
}