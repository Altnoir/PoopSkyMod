package com.altnoir.poopsky.worldgen.foliage;

import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import static com.altnoir.poopsky.worldgen.foliage.PoopMegaFoliagePlacer.CODEC;

public class PoFoliagePlacerTypes {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<FoliagePlacerType<?>, FoliagePlacerType<PoopMegaFoliagePlacer>> POOP_MEGA_FOLIAGE_PLACER =
            REGISTRATE.simple("poop_mega_foliage_placer", Registries.FOLIAGE_PLACER_TYPE, () -> new FoliagePlacerType<>(CODEC));

    public static void register() {
    }
}