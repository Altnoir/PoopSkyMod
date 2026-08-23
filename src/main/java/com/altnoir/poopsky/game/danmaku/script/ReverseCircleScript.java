package com.altnoir.poopsky.game.danmaku.script;

import com.altnoir.poopsky.game.danmaku.Boss;
import com.altnoir.poopsky.game.danmaku.BossModifiers;
import com.altnoir.poopsky.game.danmaku.BossScript;
import com.altnoir.poopsky.game.danmaku.RotationState;
import com.altnoir.poopsky.game.danmaku.modifier.BossModifierTemplate;
import com.altnoir.poopsky.game.danmaku.modifier.UniformFloat;
import com.altnoir.poopsky.game.danmaku.modifier.UniformInt;
import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class ReverseCircleScript implements BossScript {
    private static final float ACCELERATION = 0.01F;
    private static final BossModifierTemplate TEMPLATE = BossModifierTemplate.builder()
            .bulletCount(UniformInt.of(30, 40))
            .attackInterval(UniformInt.of(20, 40))
            .bulletSpeed(UniformFloat.of(0.4F, 0.6F))
            .rotation(UniformInt.of(40))
            .angleStep(UniformInt.of(5))
            .build();

    private final RotationState rotation = new RotationState();
    private int intervalCounter;

    @Override
    public BossModifiers createModifiers(Random random, int wave) {
        rotation.randomizeDirection(random);
        return TEMPLATE.roll(random);
    }

    @Override
    public void tick(Boss boss, TouhouGameState state, Random random) {
        if (intervalCounter <= 0) {
            int count = boss.getBulletCount();
            float centerX = state.getBossCenterX();
            float centerY = state.getBossCenterY();

            float edgeWidth = TouhouGameState.PLAY_WIDTH - TouhouGameState.ENEMY_BULLET_SIZE;
            float edgeHeight = TouhouGameState.PLAY_HEIGHT - TouhouGameState.ENEMY_BULLET_SIZE;
            float perimeter = 2.0F * (edgeWidth + edgeHeight);
            int angle = rotation.next(boss.getRotation(), boss.getAngleStep());
            float perimeterOffset = angle / 360.0F * perimeter;

            for (int i = 0; i < count; i++) {
                float distance = perimeter * i / count + perimeterOffset;
                distance %= perimeter;
                if (distance < 0) {
                    distance += perimeter;
                }
                float spawnX;
                float spawnY;

                if (distance < edgeWidth) {
                    spawnX = distance;
                    spawnY = 0.0F;
                } else if (distance < edgeWidth + edgeHeight) {
                    spawnX = edgeWidth;
                    spawnY = distance - edgeWidth;
                } else if (distance < 2.0F * edgeWidth + edgeHeight) {
                    spawnX = edgeWidth - (distance - edgeWidth - edgeHeight);
                    spawnY = edgeHeight;
                } else {
                    spawnX = 0.0F;
                    spawnY = edgeHeight - (distance - 2.0F * edgeWidth - edgeHeight);
                }

                float dx = centerX - spawnX;
                float dy = centerY - spawnY;
                float length = Math.max(0.001F, (float) Math.sqrt(dx * dx + dy * dy));
                float speed = boss.getBulletSpeed();
                float vx = dx / length * speed;
                float vy = dy / length * speed;

                state.spawnEnemyBullet(spawnX, spawnY, vx, vy, 600, boss.getMaxBounces(), ACCELERATION, true);
            }

            intervalCounter = boss.getFireInterval();
        } else {
            intervalCounter--;
        }
    }
}
