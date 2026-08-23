package com.altnoir.poopsky.game.danmaku.modifier;

import java.util.Random;

public record UniformFloat(float min, float max) implements FloatProvider {
    public static UniformFloat of(float min, float max) {
        return new UniformFloat(min, max);
    }

    @Override
    public float next(Random random) {
        if (max <= min) {
            return min;
        }
        return min + random.nextFloat() * (max - min);
    }
}
