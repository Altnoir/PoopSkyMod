package com.altnoir.poopsky.content.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.mojang.serialization.MapCodec;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class PoChunkGenerators {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<MapCodec<? extends ChunkGenerator>, MapCodec<PoVoidChunkGenerator>> VOID = registerChunkGenerator(
            "void", () -> PoVoidChunkGenerator.CODEC);

    private static <T extends ChunkGenerator> RegistryEntry<MapCodec<? extends ChunkGenerator>, MapCodec<T>> registerChunkGenerator(String name, NonNullSupplier<MapCodec<T>> codec) {
        return REGISTRATE.simple(name, Registries.CHUNK_GENERATOR, codec);
    }

    public static void register() {
    }
}