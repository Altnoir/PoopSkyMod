package com.altnoir.poopsky.game.danmaku;

public record BossModifiers(
        int bulletCount,
        int maxBounces,
        int fireInterval,
        float bulletSpeed,
        int rotation,
        int angleStep
) {
}

