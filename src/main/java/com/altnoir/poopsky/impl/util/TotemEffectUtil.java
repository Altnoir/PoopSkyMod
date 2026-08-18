package com.altnoir.poopsky.impl.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public final class TotemEffectUtil {
    private TotemEffectUtil() {
    }

    public static void spawnActivationParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                pos.x,
                pos.y + 1.0,
                pos.z,
                30,
                0.5,
                0.5,
                0.5,
                0.0
        );
    }

    public static void playActivationSound(ServerLevel level, Vec3 pos) {
        level.playSound(
                null,
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );
    }
}