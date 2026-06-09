package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PStats {
    public static final DeferredRegister<ResourceLocation> CUSTOM_STATS = DeferredRegister.create(Registries.CUSTOM_STAT, PoopSky.MOD_ID);

    public static final DeferredHolder<ResourceLocation, ResourceLocation> POOP_STATS = CUSTOM_STATS.register("poop_stats", () ->
            PoopSky.loc("poop_stats"));
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INSPECT_PLACER = CUSTOM_STATS.register("inspect_placer", () ->
            PoopSky.loc("inspect_placer"));

    public static void register(IEventBus modEventBus) {
        CUSTOM_STATS.register(modEventBus);
    }

    public static void init() {
        Stats.CUSTOM.get(POOP_STATS.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(INSPECT_PLACER.get(), StatFormatter.DEFAULT);
    }
}
