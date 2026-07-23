package com.altnoir.poopsky.fabric.port.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

public class ParticleOptionUtils {
    private static BlockPos pos;

    public static BlockParticleOption setBlockPos(BlockParticleOption particleOption, BlockPos blockPos) {
        pos = blockPos;
        return particleOption;
    }

    public static BlockPos getPos() {
        return pos;
    }
}
