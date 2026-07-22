package com.altnoir.poopsky.worldgen.foliage;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.mojang.serialization.MapCodec;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class PoFoliagePlacerTypes {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<FoliagePlacerType<?>, FoliagePlacerType<RhombusFoliagePlacer>> RHOMBUS_FOLIAGE_PLACER =
            registerFoliagePlacer("rhombus_foliage_placer", RhombusFoliagePlacer.CODEC);

    private static <T extends FoliagePlacer> RegistryEntry<FoliagePlacerType<?>, FoliagePlacerType<T>> registerFoliagePlacer(String name, MapCodec<T> codec) {
        return REGISTRATE.simple(name, Registries.FOLIAGE_PLACER_TYPE, () -> new FoliagePlacerType<>(codec));
    }

    public static void register() {
    }
}