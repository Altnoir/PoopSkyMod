package com.altnoir.poopsky.game.danmaku.modifier;

import java.util.Random;

/**
 * Links two UniformInt ranges to the same random roll.
 * Example: LinkedUniformInt.of(UniformInt.of(20, 30), UniformInt.of(2, 0))
 * means bullet count 20 -> bounce 2, bullet count 30 -> bounce 0.
 */
public record LinkedUniformInt(UniformInt first, UniformInt second) {
    public static LinkedUniformInt of(UniformInt first, UniformInt second) {
        return new LinkedUniformInt(first, second);
    }

    public int[] roll(Random random) {
        float t = random.nextFloat();
        return new int[]{first.next(t), second.next(t)};
    }
}
