package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class PSWorldPresets {
    public static final ResourceKey<WorldPreset> POOPSKY = ResourceKey.create(Registries.WORLD_PRESET, PoopSky.loc("poopsky"));
}
