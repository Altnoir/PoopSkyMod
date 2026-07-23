/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.altnoir.poopsky.fabric.port.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

/**
 * Fabric-side port of NeoForge's particle description data provider.
 */
public abstract class ParticleDescriptionProvider implements DataProvider {
    private final PackOutput.PathProvider particlesPath;
    protected final Map<ResourceLocation, List<String>> descriptions = new LinkedHashMap<>();

    protected ParticleDescriptionProvider(PackOutput output) {
        this.particlesPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "particles");
    }

    /**
     * Constructor matching Fabric's registry-aware data-provider factory.
     */
    protected ParticleDescriptionProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries) {
        this(output);
    }

    protected abstract void addDescriptions();

    protected void sprite(ParticleType<?> type, ResourceLocation texture) {
        spriteSet(type, texture);
    }

    protected void spriteSet(
            ParticleType<?> type,
            ResourceLocation baseName,
            int numOfTextures,
            boolean reverse) {
        if (numOfTextures <= 0) {
            throw new IllegalArgumentException("The number of textures to generate must be positive");
        }

        spriteSet(type, () -> new Iterator<>() {
            private int counter;

            @Override
            public boolean hasNext() {
                return counter < numOfTextures;
            }

            @Override
            public ResourceLocation next() {
                int index = reverse ? numOfTextures - counter - 1 : counter;
                counter++;
                return baseName.withSuffix("_" + index);
            }
        });
    }

    protected void spriteSet(
            ParticleType<?> type,
            ResourceLocation texture,
            ResourceLocation... textures) {
        spriteSet(type, Stream.concat(Stream.of(texture), Arrays.stream(textures))::iterator);
    }

    protected void spriteSet(ParticleType<?> type, Iterable<ResourceLocation> textures) {
        ResourceLocation particle = Objects.requireNonNull(
                BuiltInRegistries.PARTICLE_TYPE.getKey(type),
                "The particle type is not registered");

        List<String> description = new ArrayList<>();
        for (ResourceLocation texture : textures) {
            description.add(Objects.requireNonNull(texture, "Particle texture cannot be null").toString());
        }

        if (description.isEmpty()) {
            throw new IllegalArgumentException("The particle type '" + particle + "' must have at least one texture");
        }
        if (descriptions.putIfAbsent(particle, List.copyOf(description)) != null) {
            throw new IllegalArgumentException(
                    "The particle type '" + particle + "' already has a description associated with it");
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        descriptions.clear();
        addDescriptions();

        return CompletableFuture.allOf(descriptions.entrySet().stream().map(entry -> {
            JsonArray textures = new JsonArray();
            entry.getValue().forEach(textures::add);

            JsonObject description = new JsonObject();
            description.add("textures", textures);
            return DataProvider.saveStable(cache, description, particlesPath.json(entry.getKey()));
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Particle Descriptions";
    }
}
