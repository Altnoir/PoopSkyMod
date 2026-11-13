package com.altnoir.poopsky.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class AltarRitualAnimation {
    private static final double ALTAR_Y = 1.43D;
    private static final double PEDESTAL_Y = 0.93D;

    private AltarRitualAnimation() {
    }

    public static double altarItemY(double time) {
        return ALTAR_Y + Math.sin(time / 10.0D) * 0.04D;
    }

    public static double pedestalItemY(double time) {
        return PEDESTAL_Y + Math.sin(time / 10.0D) * 0.04D;
    }

    public static Vec3 orbitPosition(BlockPos pedestalOffset, double time, float progress) {
        double clampedProgress = Mth.clamp(progress, 0.0F, 1.0F);
        double approach = approachProgress(clampedProgress);
        double radius = 1.0D - approach;
        double angle = clampedProgress * clampedProgress * Math.PI * 16.0D;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x = pedestalOffset.getX();
        double z = pedestalOffset.getZ();
        return new Vec3(0.5D + (x * cos - z * sin) * radius,
                Mth.lerp(approach, pedestalItemY(time), altarItemY(time)),
                0.5D + (x * sin + z * cos) * radius);
    }

    private static double approachProgress(double progress) {
        double rampIn = 0.05D;
        double rampOut = 0.22D;
        double area = 1.0D - (rampIn + rampOut) / 2.0D;
        if (progress < rampIn) {
            return progress * progress / (2.0D * rampIn * area);
        }
        if (progress > 1.0D - rampOut) {
            double remaining = (1.0D - progress) / rampOut;
            return 1.0D - rampOut / area * remaining * remaining / 2.0D;
        }
        return (progress - rampIn / 2.0D) / area;
    }

    public static void spawnSpark(Level level, Vec3 position) {
        var random = level.random;
        level.addParticle(ParticleTypes.END_ROD,
                position.x + (random.nextDouble() - 0.5D) * 0.16D,
                position.y - 0.25D + (random.nextDouble() - 0.5D) * 0.16D,
                position.z + (random.nextDouble() - 0.5D) * 0.16D,
                (random.nextDouble() - 0.5D) * 0.025D, 0.015D, (random.nextDouble() - 0.5D) * 0.025D);
    }
}
