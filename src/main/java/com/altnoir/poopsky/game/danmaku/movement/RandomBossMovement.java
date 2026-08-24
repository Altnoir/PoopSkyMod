package com.altnoir.poopsky.game.danmaku.movement;

import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class RandomBossMovement implements BossMovement {
    private final float amplitude;
    private final float speed;
    private BossMovement delegate;
    private boolean initialized;

    public RandomBossMovement(float amplitude, float speed) {
        this.amplitude = amplitude;
        this.speed = speed;
    }

    @Override
    public void tick(TouhouGameState state, Random random) {
        if (!initialized) {
            delegate = random.nextBoolean()
                    ? new LeftRightBossMovement(amplitude, speed)
                    : new OrbitBossMovement(amplitude, speed);
            initialized = true;
        }

        delegate.tick(state, random);
    }

    @Override
    public BossMovement copy() {
        return new RandomBossMovement(amplitude, speed);
    }
}
