/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.altnoir.poopsky.fabric.port.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Contains the data used to serialize one entry in {@code sounds.json}. */
public final class SoundDefinition {
    public static final class Sound {
        private static final float DEFAULT_VOLUME = 1.0F;
        private static final float DEFAULT_PITCH = 1.0F;
        private static final int DEFAULT_WEIGHT = 1;
        private static final int DEFAULT_ATTENUATION_DISTANCE = 16;

        private final ResourceLocation name;
        private final SoundType type;
        private float volume = DEFAULT_VOLUME;
        private float pitch = DEFAULT_PITCH;
        private int weight = DEFAULT_WEIGHT;
        private boolean stream;
        private int attenuationDistance = DEFAULT_ATTENUATION_DISTANCE;
        private boolean preload;

        private Sound(ResourceLocation name, SoundType type) {
            this.name = name;
            this.type = type;
        }

        public static Sound sound(ResourceLocation name, SoundType type) {
            return new Sound(name, type);
        }

        public Sound volume(double volume) {
            return volume((float) volume);
        }

        public Sound volume(float volume) {
            if (volume <= 0.0F) {
                throw new IllegalArgumentException("Volume must be positive for sound " + name);
            }
            this.volume = volume;
            return this;
        }

        public Sound pitch(double pitch) {
            return pitch((float) pitch);
        }

        public Sound pitch(float pitch) {
            if (pitch <= 0.0F) {
                throw new IllegalArgumentException("Pitch must be positive for sound " + name);
            }
            this.pitch = pitch;
            return this;
        }

        public Sound weight(int weight) {
            if (weight <= 0) {
                throw new IllegalArgumentException("Weight must be positive for sound " + name);
            }
            this.weight = weight;
            return this;
        }

        public Sound stream() {
            return stream(true);
        }

        public Sound stream(boolean stream) {
            this.stream = stream;
            return this;
        }

        public Sound attenuationDistance(int attenuationDistance) {
            this.attenuationDistance = attenuationDistance;
            return this;
        }

        public Sound preload() {
            return preload(true);
        }

        public Sound preload(boolean preload) {
            this.preload = preload;
            return this;
        }

        ResourceLocation name() {
            return name;
        }

        SoundType type() {
            return type;
        }

        JsonElement serialize() {
            if (canBeInShortForm()) {
                return new JsonPrimitive(stripMinecraftNamespace(name));
            }

            JsonObject object = new JsonObject();
            object.addProperty("name", stripMinecraftNamespace(name));
            if (type != SoundType.SOUND) object.addProperty("type", type.jsonName);
            if (volume != DEFAULT_VOLUME) object.addProperty("volume", volume);
            if (pitch != DEFAULT_PITCH) object.addProperty("pitch", pitch);
            if (weight != DEFAULT_WEIGHT) object.addProperty("weight", weight);
            if (stream) object.addProperty("stream", true);
            if (preload) object.addProperty("preload", true);
            if (attenuationDistance != DEFAULT_ATTENUATION_DISTANCE) {
                object.addProperty("attenuation_distance", attenuationDistance);
            }
            return object;
        }

        private boolean canBeInShortForm() {
            return type == SoundType.SOUND
                    && volume == DEFAULT_VOLUME
                    && pitch == DEFAULT_PITCH
                    && weight == DEFAULT_WEIGHT
                    && !stream
                    && attenuationDistance == DEFAULT_ATTENUATION_DISTANCE
                    && !preload;
        }

        private static String stripMinecraftNamespace(ResourceLocation name) {
            return "minecraft".equals(name.getNamespace()) ? name.getPath() : name.toString();
        }
    }

    public enum SoundType {
        SOUND("sound"),
        EVENT("event");

        private final String jsonName;

        SoundType(String jsonName) {
            this.jsonName = jsonName;
        }
    }

    private final List<Sound> sounds = new ArrayList<>();
    private boolean replace;
    @Nullable
    private String subtitle;

    private SoundDefinition() {
    }

    public static SoundDefinition definition() {
        return new SoundDefinition();
    }

    public SoundDefinition replace(boolean replace) {
        this.replace = replace;
        return this;
    }

    public SoundDefinition subtitle(@Nullable String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public SoundDefinition with(Sound sound) {
        sounds.add(sound);
        return this;
    }

    public SoundDefinition with(Sound... sounds) {
        this.sounds.addAll(Arrays.asList(sounds));
        return this;
    }

    List<Sound> soundList() {
        return sounds;
    }

    JsonObject serialize() {
        if (sounds.isEmpty()) {
            throw new IllegalStateException("Unable to serialize a sound definition that has no sounds");
        }

        JsonObject object = new JsonObject();
        if (replace) object.addProperty("replace", true);
        if (subtitle != null) object.addProperty("subtitle", subtitle);

        JsonArray serializedSounds = new JsonArray();
        sounds.stream().map(Sound::serialize).forEach(serializedSounds::add);
        object.add("sounds", serializedSounds);
        return object;
    }
}
