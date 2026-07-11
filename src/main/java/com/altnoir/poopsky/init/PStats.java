package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;

public class PStats {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<ResourceLocation, ResourceLocation> POOP_STATS = REGISTRATE.simple("poop_stats", Registries.CUSTOM_STAT, () ->
            PoopSky.loc("poop_stats"));
    public static final RegistryEntry<ResourceLocation, ResourceLocation> INSPECT_PLACER = REGISTRATE.simple("inspect_placer", Registries.CUSTOM_STAT, () ->
            PoopSky.loc("inspect_placer"));

    public static void register(IEventBus modEventBus) {
    }

    public static void init() {
        Stats.CUSTOM.get(POOP_STATS.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(INSPECT_PLACER.get(), StatFormatter.DEFAULT);
    }
}
