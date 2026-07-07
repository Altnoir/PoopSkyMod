package com.altnoir.poopsky.api;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RegistryHelper {
    private RegistryHelper() {
    }

    public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey) {
        return DeferredRegister.create(registryKey, PoopSky.MOD_ID);
    }

    public static ResourceLocation loc(String path) {
        return PoopSky.loc(path);
    }

    public static <T> ResourceKey<T> resourceKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, loc(path));
    }
}