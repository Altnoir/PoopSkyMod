package com.altnoir.poopsky.init;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.PoMods;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public final class PoWorldPreset {
    public static final ResourceKey<WorldPreset> POOPSKY = ResourceKey.create(Registries.WORLD_PRESET, PoopSky.loc("poopsky"));

    public static ResourceKey<WorldPreset> overrideDefaultWorldPreset() {
        if (FabricLoader.getInstance().isModLoaded(PoMods.SKYBLOCKBUILDER.id())) {
            return ResourceKey.create(Registries.WORLD_PRESET, PoMods.SKYBLOCKBUILDER.rl("skyblock"));
        }
        return Config.setPoopSkyDefault ? POOPSKY : WorldPresets.NORMAL;
    }
}
