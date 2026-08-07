package com.altnoir.poopsky.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public enum PoMods {
    SABLE,
    JEI,
    CREATE,
    FARMERSDELIGHT,
    SKYBLOCKBUILDER,
    MODERNUI,
    TOUHOU_LITTLE_MAID,
    AE2,
    MEKANISM;

    private final String id;
    private final boolean isLoaded;

    PoMods() {
        id = name().toLowerCase(Locale.ROOT);
        isLoaded = FabricLoader.getInstance().isModLoaded(id);
    }

    public String id() {
        return id;
    }

    public ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(id, path);
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}
