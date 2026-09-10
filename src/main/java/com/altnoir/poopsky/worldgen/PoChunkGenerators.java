package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.abysslib.reginth.Reginth;
import com.mojang.serialization.MapCodec;
import com.altnoir.abysslib.reginth.util.entry.RegistryEntry;
import com.altnoir.abysslib.reginth.util.nullness.NonNullSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class PoChunkGenerators {
    private static final Reginth REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<MapCodec<? extends ChunkGenerator>, MapCodec<PoVoidChunkGenerator>> VOID = registerChunkGenerator(
            "void", () -> PoVoidChunkGenerator.CODEC);

    private static <T extends ChunkGenerator> RegistryEntry<MapCodec<? extends ChunkGenerator>, MapCodec<T>> registerChunkGenerator(String name, NonNullSupplier<MapCodec<T>> codec) {
        return REGISTRATE.simple(name, Registries.CHUNK_GENERATOR, codec);
    }

    public static void register() {
    }
}