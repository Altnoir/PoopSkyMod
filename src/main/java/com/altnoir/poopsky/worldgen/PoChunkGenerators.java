package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class PoChunkGenerators {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<Codec<PoVoidChunkGenerator>> VOID = registerChunkGenerator(
            "void", () -> PoVoidChunkGenerator.CODEC);

    private static <T extends ChunkGenerator> RegistryEntry<Codec<T>> registerChunkGenerator(String name, NonNullSupplier<Codec<T>> codec) {
        return REGISTRATE.simple(name, Registries.CHUNK_GENERATOR, codec);
    }

    public static void register() {
    }
}