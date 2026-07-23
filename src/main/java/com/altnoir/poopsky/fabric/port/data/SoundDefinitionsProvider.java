/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.altnoir.poopsky.fabric.port.data;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/** Fabric-side port of NeoForge's {@code SoundDefinitionsProvider}. */
public abstract class SoundDefinitionsProvider implements DataProvider {
    private final PackOutput output;
    private final String modId;
    private final Map<String, SoundDefinition> sounds = new LinkedHashMap<>();

    protected SoundDefinitionsProvider(PackOutput output, String modId) {
        this.output = output;
        this.modId = modId;
    }

    /** Constructor matching Fabric's registry-aware data-provider factory. */
    protected SoundDefinitionsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries,
            String modId) {
        this(output, modId);
    }

    public abstract void registerSounds();

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        sounds.clear();
        registerSounds();
        validate();

        if (sounds.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        Path target = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(modId)
                .resolve("sounds.json");
        return DataProvider.saveStable(cache, mapToJson(), target);
    }

    @Override
    public String getName() {
        return "Sound Definitions";
    }

    protected static SoundDefinition definition() {
        return SoundDefinition.definition();
    }

    protected static SoundDefinition.Sound sound(ResourceLocation name, SoundDefinition.SoundType type) {
        return SoundDefinition.Sound.sound(name, type);
    }

    protected static SoundDefinition.Sound sound(ResourceLocation name) {
        return sound(name, SoundDefinition.SoundType.SOUND);
    }

    protected static SoundDefinition.Sound sound(String name, SoundDefinition.SoundType type) {
        return sound(ResourceLocation.parse(name), type);
    }

    protected static SoundDefinition.Sound sound(String name) {
        return sound(ResourceLocation.parse(name));
    }

    protected void add(Supplier<SoundEvent> soundEvent, SoundDefinition definition) {
        add(soundEvent.get(), definition);
    }

    protected void add(SoundEvent soundEvent, SoundDefinition definition) {
        add(soundEvent.getLocation(), definition);
    }

    protected void add(ResourceLocation soundEvent, SoundDefinition definition) {
        addSound(soundEvent.getPath(), definition);
    }

    protected void add(String soundEvent, SoundDefinition definition) {
        add(ResourceLocation.parse(soundEvent), definition);
    }

    private void addSound(String soundEvent, SoundDefinition definition) {
        if (sounds.putIfAbsent(soundEvent, definition) != null) {
            throw new IllegalStateException("Sound event '" + modId + ":" + soundEvent + "' already exists");
        }
    }

    private void validate() {
        for (Map.Entry<String, SoundDefinition> entry : sounds.entrySet()) {
            for (SoundDefinition.Sound sound : entry.getValue().soundList()) {
                if (sound.type() == SoundDefinition.SoundType.EVENT
                        && !BuiltInRegistries.SOUND_EVENT.containsKey(sound.name())
                        && !isGeneratedEvent(sound.name())) {
                    throw new IllegalStateException(
                            "Unknown sound event '" + sound.name() + "' referenced from '" + modId + ":" + entry.getKey() + "'");
                }
            }
        }
    }

    private boolean isGeneratedEvent(ResourceLocation event) {
        return event.getNamespace().equals(modId) && sounds.containsKey(event.getPath());
    }

    private JsonObject mapToJson() {
        JsonObject object = new JsonObject();
        sounds.forEach((name, definition) -> object.add(name, definition.serialize()));
        return object;
    }
}
