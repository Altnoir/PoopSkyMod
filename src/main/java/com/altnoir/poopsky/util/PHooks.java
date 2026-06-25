package com.altnoir.poopsky.util;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.PSMods;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.fml.ModList;

import java.util.Properties;

@SuppressWarnings("unused")
public final class PHooks {
    public static final ResourceKey<WorldPreset> POOPSKY = ResourceKey.create(Registries.WORLD_PRESET, PoopSky.loc("poopsky"));

    /**
     * Called in {@link net.minecraft.server.dedicated.DedicatedServerProperties#DedicatedServerProperties(Properties)}
     * where {@code WorldPresets.NORMAL} is used in the line that looks like {@code WorldPresets.NORMAL.location().toString()}
     */
    public static ResourceKey<WorldPreset> overrideDefaultWorldPreset() {
        if (ModList.get().isLoaded(PSMods.SKYBLOCKBUILDER.id())) {
            return ResourceKey.create(Registries.WORLD_PRESET, PSMods.SKYBLOCKBUILDER.rl("skyblock"));
        }
        return Config.setPoopSkyDefault ? POOPSKY : WorldPresets.NORMAL;
    }
}