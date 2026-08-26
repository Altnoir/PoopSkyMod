package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.game.danmaku.movement.BossMovement;

public record BossModifiers(
        int baseHp,
        int bulletCount,
        int maxBounces,
        int fireInterval,
        float bulletSpeed,
        int rotation,
        int angleStep,
        CircularRotation circularRotation,
        BossMovement movement,
        int movementWave,
        boolean randomMovement
) {
    public BossModifiers withMovement(BossMovement movement, int movementWave) {
        return new BossModifiers(
                baseHp,
                bulletCount,
                maxBounces,
                fireInterval,
                bulletSpeed,
                rotation,
                angleStep,
                circularRotation,
                movement,
                movementWave,
                randomMovement
        );
    }
}
