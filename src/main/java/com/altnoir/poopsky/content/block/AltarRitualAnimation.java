package com.altnoir.poopsky.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class AltarRitualAnimation {
    private static final double ALTAR_Y = 1.53D;
    private static final double PEDESTAL_Y = 0.93D;

    private static final double MID_RADIUS = 0.5D;
    private static final double END_RADIUS = 0.1D;
    private static final double SPIRAL_START = 0.5D;
    private static final double SPIN_POWER = 5.0D;

    private AltarRitualAnimation() {
    }

    public static double altarItemY(double time) {
        return ALTAR_Y + Math.sin(time / 10.0D) * 0.04D;
    }

    public static double pedestalItemY(double time) {
        return PEDESTAL_Y + Math.sin(time / 10.0D) * 0.04D;
    }

    public static Vec3 orbitPosition(BlockPos pedestalOffset, double time, float progress) {
        double p = Mth.clamp(progress, 0.0F, 1.0F);
        double radius;
        if (p < SPIRAL_START) {
            double u = p / SPIRAL_START;
            radius = 1.0D - (1.0D - MID_RADIUS) * (1.0D - (1.0D - u) * (1.0D - u));
        } else {
            double v = (p - SPIRAL_START) / (1.0D - SPIRAL_START);
            radius = END_RADIUS + (MID_RADIUS - END_RADIUS) * (1.0D - v * v);
        }
        double angle = Math.pow(p, SPIN_POWER) * Math.PI * 16.0D;
        double approach = (1.0D - radius) / (1.0D - END_RADIUS);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x = pedestalOffset.getX();
        double z = pedestalOffset.getZ();
        return new Vec3(0.5D + (x * cos - z * sin) * radius,
                Mth.lerp(approach, pedestalItemY(time), altarItemY(time)),
                0.5D + (x * sin + z * cos) * radius);
    }

    public static void spawnSpark(Level level, Vec3 position) {
        var random = level.random;
        level.addParticle(ParticleTypes.END_ROD,
                position.x + (random.nextDouble() - 0.5D) * 0.16D,
                position.y - 0.2D + (random.nextDouble() - 0.5D) * 0.16D,
                position.z + (random.nextDouble() - 0.5D) * 0.16D,
                (random.nextDouble() - 0.5D) * 0.025D, 0.015D, (random.nextDouble() - 0.5D) * 0.025D);
    }
}
