package com.altnoir.poopsky.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PSChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, "poopsky");

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<PSVoidChunkGenerator>> VOID = CHUNK_GENERATORS
            .register("void", () -> PSVoidChunkGenerator.CODEC);

    private PSChunkGenerators() {
    }

    public static void register(IEventBus eventBus) {
        CHUNK_GENERATORS.register(eventBus);
    }
}
