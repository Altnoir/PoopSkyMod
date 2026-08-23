package com.altnoir.poopsky.game.danmaku.modifier;

import java.util.Random;

public record ConstantInt(int value) implements IntProvider {
    public static ConstantInt of(int value) {
        return new ConstantInt(value);
    }

    @Override
    public int next(Random random) {
        return value;
    }
}
