/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.altnoir.poopsky.fabric.port.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Fabric-side port of NeoForge's datapack builtin entries provider.
 *
 * <p>The vanilla generator used by Fabric does not accept a namespace filter.
 * The supplied mod ids are retained in the constructor for source compatibility;
 * generated entries are limited by the patch lookup produced from the supplied
 * {@link RegistrySetBuilder}.</p>
 */
public class DatapackBuiltinEntriesProvider extends RegistriesDatapackGenerator {
    private final CompletableFuture<HolderLookup.Provider> fullRegistries;

    public DatapackBuiltinEntriesProvider(
            PackOutput output,
            CompletableFuture<RegistrySetBuilder.PatchedRegistries> registries,
            Set<String> modIds) {
        super(output, registries.thenApply(RegistrySetBuilder.PatchedRegistries::patches));
        this.fullRegistries = registries.thenApply(RegistrySetBuilder.PatchedRegistries::full);
    }

    public DatapackBuiltinEntriesProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries,
            RegistrySetBuilder datapackEntriesBuilder,
            Set<String> modIds) {
        this(output, RegistryPatchGenerator.createLookup(registries, datapackEntriesBuilder), modIds);
    }

    /**
     * Returns a lookup containing both the input registries and entries added by
     * the registry set builder.
     */
    public CompletableFuture<HolderLookup.Provider> getRegistryProvider() {
        return fullRegistries;
    }
}
