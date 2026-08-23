package com.altnoir.poopsky.game.danmaku.modifier;

import java.util.Random;

/**
 * A pair of linked integer ranges that share the same random roll.
 * For example (minA=20, maxA=30, minB=1, maxB=0) means:
 * bulletCount 20 -> maxBounces 1
 * bulletCount 30 -> maxBounces 0
 */
public record LinkedIntPair(int minA, int maxA, int minB, int maxB) {
    public static LinkedIntPair of(int minA, int maxA, int minB, int maxB) {
        return new LinkedIntPair(minA, maxA, minB, maxB);
    }

    public int[] roll(Random random) {
        float t = random.nextFloat();
        return new int[]{nextA(t), nextB(t)};
    }

    public int nextA(float t) {
        return Math.round(minA + (maxA - minA) * t);
    }

    public int nextB(float t) {
        return Math.round(minB + (maxB - minB) * t);
    }
}
