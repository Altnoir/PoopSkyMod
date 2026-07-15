package com.altnoir.poopsky.impl.util;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.PoMods;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.fml.ModList;

public final class PoHooks {
    public static final ResourceKey<WorldPreset> POOPSKY = ResourceKey.create(Registries.WORLD_PRESET, PoopSky.loc("poopsky"));

    public static ResourceKey<WorldPreset> overrideDefaultWorldPreset() {
        if (ModList.get().isLoaded(PoMods.SKYBLOCKBUILDER.id())) {
            return ResourceKey.create(Registries.WORLD_PRESET, PoMods.SKYBLOCKBUILDER.rl("skyblock"));
        }
        return Config.setPoopSkyDefault ? POOPSKY : WorldPresets.NORMAL;
    }
}