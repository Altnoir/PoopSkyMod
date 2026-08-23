package com.altnoir.poopsky.game.danmaku.pattern;

import com.altnoir.poopsky.game.danmaku.BulletPattern;
import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class PolygonBulletPattern implements BulletPattern {
    private final int sides;
    private final int bulletCount;
    private final int maxBounces;
    private final float bulletSpeed;

    public PolygonBulletPattern(int sides, int bulletCount, int maxBounces, float bulletSpeed) {
        this.sides = sides;
        this.bulletCount = bulletCount;
        this.maxBounces = maxBounces;
        this.bulletSpeed = bulletSpeed;
    }

    @Override
    public void fire(TouhouGameState state, Random random) {
        float centerX = state.getBossCenterX();
        float centerY = state.getBossCenterY();

        if (sides <= 0) {
            fireCircle(state, centerX, centerY);
        } else {
            firePolygon(state, centerX, centerY);
        }
    }

    private void fireCircle(TouhouGameState state, float centerX, float centerY) {
        for (int i = 0; i < bulletCount; i++) {
            double angle = Math.PI * 2 * i / bulletCount;
            float vx = (float) (Math.cos(angle) * bulletSpeed);
            float vy = (float) (Math.sin(angle) * bulletSpeed);
            state.spawnEnemyBullet(
                    centerX - TouhouGameState.ENEMY_BULLET_SIZE / 2.0F,
                    centerY - TouhouGameState.ENEMY_BULLET_SIZE / 2.0F,
                    vx, vy,
                    600,
                    maxBounces,
                    0.0F
            );
        }
    }

    private void firePolygon(TouhouGameState state, float centerX, float centerY) {
        int perSide = bulletCount / sides;
        int remainder = bulletCount % sides;
        for (int side = 0; side < sides; side++) {
            int count = perSide + (side < remainder ? 1 : 0);
            double baseAngle = Math.PI * 2 * side / sides;
            for (int i = 0; i < count; i++) {
                double offset = (i - (count - 1) / 2.0) * 0.18;
                double angle = baseAngle + offset;
                float vx = (float) (Math.cos(angle) * bulletSpeed);
                float vy = (float) (Math.sin(angle) * bulletSpeed);
                state.spawnEnemyBullet(
                        centerX - TouhouGameState.ENEMY_BULLET_SIZE / 2.0F,
                        centerY - TouhouGameState.ENEMY_BULLET_SIZE / 2.0F,
                        vx, vy,
                        600,
                        maxBounces,
                        0.0F
                );
            }
        }
    }
}
