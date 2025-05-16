package com.altnoir.poopsky.worldgen.foliage;

import com.altnoir.poopsky.PoopSky;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class PSFoliagePlacerType {
    public static final FoliagePlacerType<PoopMegaFoliagePlacer> POOP_MEGA_FOLIAGE_PLACER = register("poop_mega_foliage_placer", PoopMegaFoliagePlacer.CODEC);
    private static <P extends FoliagePlacer> FoliagePlacerType<P> register(String id, MapCodec<P> codec) {
        return Registry.register(Registries.FOLIAGE_PLACER_TYPE, Identifier.of(PoopSky.MOD_ID, id), new FoliagePlacerType<>(codec));
    }

    public static void registerFoliagePlacer() {
        PoopSky.LOGGER.info("Registering Foliage Placer Types for " + PoopSky.MOD_ID);
    }
}
