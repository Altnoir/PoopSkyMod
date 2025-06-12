package com.altnoir.poopsky.worldgen.foliage;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.altnoir.poopsky.worldgen.foliage.PoopMegaFoliagePlacer.CODEC;

public class PSFoliagePlacerTypes {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, PoopSky.MOD_ID);

    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<PoopMegaFoliagePlacer>> POOP_MEGA_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("poop_mega_foliage_placer", () ->
                    new FoliagePlacerType<>(CODEC));

    public static void register(IEventBus eventBus) {
        FOLIAGE_PLACERS.register(eventBus);
    }
}
