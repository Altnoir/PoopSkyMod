package com.altnoir.poopsky.game.danmaku.modifier;

import java.util.Random;

public record ConstantFloat(float value) implements FloatProvider {
    public static ConstantFloat of(float value) {
        return new ConstantFloat(value);
    }

    @Override
    public float next(Random random) {
        return value;
    }
}
