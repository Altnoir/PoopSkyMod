package com.altnoir.poopsky.game.danmaku.modifier;

import java.util.Random;

public record UniformInt(int min, int max) implements IntProvider {
    public static UniformInt of(int min, int max) {
        return new UniformInt(min, max);
    }

    public static IntProvider of(int value) {
        return random -> value;
    }

    @Override
    public int next(Random random) {
        int low = Math.min(min, max);
        int high = Math.max(min, max);
        if (high <= low) {
            return low;
        }
        return low + random.nextInt(high - low + 1);
    }

    public int next(float t) {
        if (max <= min) {
            return min;
        }
        return Math.round(min + (max - min) * t);
    }
}
