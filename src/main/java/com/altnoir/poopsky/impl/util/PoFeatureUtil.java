package com.altnoir.poopsky.impl.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public final class PoFeatureUtil {
    private PoFeatureUtil() {
    }

    public static void placePatch(ServerLevel level, BlockPos origin, RandomSource random,
                                  ResourceKey<ConfiguredFeature<?, ?>> feature, int tries,
                                  int xzSpread, int ySpread) {
        level.registryAccess()
                .registry(Registries.CONFIGURED_FEATURE)
                .flatMap(registry -> registry.getHolder(feature))
                .ifPresent(reference -> {
                    for (int attempt = 0; attempt < tries; attempt++) {
                        int x = random.nextInt((xzSpread << 1) + 1) - xzSpread;
                        int y = random.nextInt((ySpread << 1) + 1) - ySpread;
                        int z = random.nextInt((xzSpread << 1) + 1) - xzSpread;
                        reference.value().place(
                                level,
                                level.getChunkSource().getGenerator(),
                                random,
                                origin.offset(x, y, z)
                        );
                    }
                });
    }
}
