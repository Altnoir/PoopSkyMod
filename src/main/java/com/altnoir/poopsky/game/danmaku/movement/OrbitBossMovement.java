package com.altnoir.poopsky.game.danmaku.movement;

import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class OrbitBossMovement implements BossMovement {
    private final float radius;
    private final float degreesPerTick;
    private boolean initialized;
    private float centerX;
    private float centerY;
    private float orbitRadius;
    private float angle;
    private int direction = 1;

    public OrbitBossMovement(float radius, float degreesPerTick) {
        this.radius = radius;
        this.degreesPerTick = degreesPerTick;
    }

    @Override
    public void tick(TouhouGameState state, Random random) {
        if (!initialized) {
            float originalCenterX = state.getBossCenterX();
            float originalCenterY = state.getBossCenterY();

            centerX = TouhouGameState.PLAY_WIDTH / 2.0F;
            centerY = originalCenterY + radius;

            float dx = originalCenterX - centerX;
            float dy = originalCenterY - centerY;
            orbitRadius = Math.max(0.001F, (float) Math.sqrt(dx * dx + dy * dy));

            angle = (float) Math.toDegrees(Math.atan2(dy, dx));
            direction = random.nextBoolean() ? 1 : -1;
            initialized = true;
        }

        angle += direction * degreesPerTick;
        double radians = Math.toRadians(angle);
        float bossCenterX = centerX + (float) (Math.cos(radians) * orbitRadius);
        float bossCenterY = centerY + (float) (Math.sin(radians) * orbitRadius);

        state.setBossX(Math.clamp(
                bossCenterX - TouhouGameState.BOSS_SIZE / 2.0F,
                0.0F,
                (float) (TouhouGameState.PLAY_WIDTH - TouhouGameState.BOSS_SIZE)
        ));
        state.setBossY(Math.clamp(
                bossCenterY - TouhouGameState.BOSS_SIZE / 2.0F,
                0.0F,
                (float) (TouhouGameState.PLAY_HEIGHT - TouhouGameState.BOSS_SIZE)
        ));
    }

    @Override
    public BossMovement copy() {
        return new OrbitBossMovement(radius, degreesPerTick);
    }
}
