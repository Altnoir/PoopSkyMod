/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.altnoir.poopsky.fabric.port.data;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Fabric-side replacement for NeoForge's global loot modifier provider.
 *
 * <p>Fabric applies global loot changes through {@link LootTableEvents#MODIFY}
 * instead of loading NeoForge's {@code loot_modifiers} datapack files. Subclasses
 * collect named modifications in {@link #start()}, then call
 * {@link #registerModifiers()} during mod initialization.</p>
 */
public abstract class GlobalLootModifierProvider implements DataProvider {
    private final String modId;
    private final Map<String, Modification> modifiers = new LinkedHashMap<>();
    private boolean initialized;

    protected GlobalLootModifierProvider(String modId) {
        this.modId = modId;
    }

    /**
     * Compatibility constructor for providers migrated from NeoForge. Fabric
     * registrations do not use datagen output or a registry future.
     */
    protected GlobalLootModifierProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries,
            String modId) {
        this(modId);
    }

    protected abstract void start();

    protected final void add(
            String name,
            ResourceKey<LootTable> target,
            LootTableModifier modifier) {
        if (modifiers.put(name, new Modification(target, modifier)) != null) {
            throw new IllegalArgumentException("Duplicate global loot modifier: " + modId + ":" + name);
        }
    }

    public final void registerModifiers() {
        if (initialized) {
            return;
        }
        initialized = true;
        start();

        for (Modification modification : modifiers.values()) {
            LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
                if (key.equals(modification.target())) {
                    modification.modifier().modify(tableBuilder, registries);
                }
            });
        }
    }

    public String getName() {
        return "Global Loot Modifiers";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.completedFuture(null);
    }

    @FunctionalInterface
    protected interface LootTableModifier {
        void modify(LootTable.Builder tableBuilder, HolderLookup.Provider registries);
    }

    private record Modification(ResourceKey<LootTable> target, LootTableModifier modifier) {
    }
}
