package com.altnoir.poopsky.game.danmaku.movement;

import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class LeftRightBossMovement implements BossMovement {
    private final float amplitude;
    private final float speed;
    private boolean initialized;
    private float startX;
    private int direction = 1;

    public LeftRightBossMovement(float amplitude, float speed) {
        this.amplitude = amplitude;
        this.speed = speed;
    }

    @Override
    public void tick(TouhouGameState state, Random random) {
        if (!initialized) {
            startX = state.getBossX();
            direction = random.nextBoolean() ? 1 : -1;
            initialized = true;
        }

        float minX = Math.max(0.0F, startX - amplitude);
        float maxX = Math.min(
                (float) (TouhouGameState.PLAY_WIDTH - TouhouGameState.BOSS_SIZE),
                startX + amplitude
        );

        float x = state.getBossX() + direction * speed;
        if (x < minX) {
            x = minX;
            direction = 1;
        } else if (x > maxX) {
            x = maxX;
            direction = -1;
        }

        state.setBossX(x);
    }

    @Override
    public BossMovement copy() {
        return new LeftRightBossMovement(amplitude, speed);
    }
}
