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
    private static final float EDGE_WIDTH = TouhouGameState.PLAY_WIDTH - TouhouGameState.BOSS_BULLET_SIZE;
    private static final float EDGE_HEIGHT = TouhouGameState.PLAY_HEIGHT - TouhouGameState.BOSS_BULLET_SIZE;
    private static final float PERIMETER = 2.0F * (EDGE_WIDTH + EDGE_HEIGHT);
    private static final BossModifierTemplate TEMPLATE = BossModifierTemplate.builder()
            .baseHp(UniformInt.of(40))
            .bulletCount(UniformInt.of(30, 40))
            .attackInterval(UniformInt.of(20, 40))
            .bulletSpeed(UniformFloat.of(0.4F, 0.6F))
            .rotation(UniformInt.of(40))
            .angleStep(UniformInt.of(5))
            .build();

    private final RotationState rotation = new RotationState();
    private int intervalCounter = TouhouGameState.BOSS_APPEAR_DURATION;

    @Override
    public BossModifiers createModifiers(Random random, int wave) {
        rotation.randomizeDirection(random);
        return TEMPLATE.roll(random);
    }

    @Override
    public void tick(Boss boss, TouhouGameState state, Random random) {
        BossModifiers modifiers = boss.modifiers();
        if (intervalCounter <= 0) {
            int count = modifiers.bulletCount();
            float centerX = state.getBossCenterX();
            float centerY = state.getBossCenterY();
            int angle = rotation.next(modifiers.rotation(), modifiers.angleStep());
            float distance = angle / 360.0F * PERIMETER;
            float distanceStep = PERIMETER / count;
            float speed = modifiers.bulletSpeed();
            int maxBounces = modifiers.maxBounces();

            if (distance < 0) {
                distance += PERIMETER;
            }

            for (int i = 0; i < count; i++) {
                float spawnX;
                float spawnY;

                if (distance < EDGE_WIDTH) {
                    spawnX = distance;
                    spawnY = 0.0F;
                } else if (distance < EDGE_WIDTH + EDGE_HEIGHT) {
                    spawnX = EDGE_WIDTH;
                    spawnY = distance - EDGE_WIDTH;
                } else if (distance < 2.0F * EDGE_WIDTH + EDGE_HEIGHT) {
                    spawnX = EDGE_WIDTH - (distance - EDGE_WIDTH - EDGE_HEIGHT);
                    spawnY = EDGE_HEIGHT;
                } else {
                    spawnX = 0.0F;
                    spawnY = EDGE_HEIGHT - (distance - 2.0F * EDGE_WIDTH - EDGE_HEIGHT);
                }

                float bulletCenterX = spawnX + TouhouGameState.BOSS_BULLET_SIZE / 2.0F;
                float bulletCenterY = spawnY + TouhouGameState.BOSS_BULLET_SIZE / 2.0F;
                float dx = centerX - bulletCenterX;
                float dy = centerY - bulletCenterY;
                float length = Math.max(0.001F, (float) Math.sqrt(dx * dx + dy * dy));
                float vx = dx / length * speed;
                float vy = dy / length * speed;

                state.spawnEnemyBullet(spawnX, spawnY, vx, vy, 600, maxBounces, ACCELERATION, true);
                distance += distanceStep;
                if (distance >= PERIMETER) {
                    distance -= PERIMETER;
                }
            }

            intervalCounter = modifiers.fireInterval();
        } else {
            intervalCounter--;
        }
    }
}
