package com.altnoir.poopsky.compat;

import net.minecraft.resources.Identifier;
import net.neoforged.fml.loading.FMLLoader;

import java.util.Locale;

public enum PoMods {
    SABLE,
    JEI,
    CREATE,
    SKYBLOCKBUILDER,
    MODERNUI,
    AE2,
    MEKANISM;

    private final String id;
    private final boolean isLoaded;

    PoMods() {
        id = name().toLowerCase(Locale.ROOT);
        isLoaded = FMLLoader.getCurrent().getLoadingModList().getModFileById(id) != null;
    }

    public String id() {
        return id;
    }

    public Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(id, path);
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}
