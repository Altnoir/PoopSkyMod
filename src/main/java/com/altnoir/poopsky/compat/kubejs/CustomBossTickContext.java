package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.game.danmaku.BossModifiers;
import com.altnoir.poopsky.game.model.TouhouGameState;

public final class CustomBossTickContext {
    private final TouhouGameState state;
    private final BossModifiers modifiers;
    private final int age;
    private float rotation;

    CustomBossTickContext(TouhouGameState state, BossModifiers modifiers, int age, float rotation) {
        this.state = state;
        this.modifiers = modifiers;
        this.age = age;
        this.rotation = rotation;
    }

    public int getAge() {
        return age;
    }

    public int getWave() {
        return state.getWave() + 1;
    }

    public int getBaseHp() {
        return modifiers.baseHp();
    }

    public int getBulletCount() {
        return modifiers.bulletCount();
    }

    public int getMaxBounces() {
        return modifiers.maxBounces();
    }

    public int getAttackInterval() {
        return modifiers.fireInterval();
    }

    public float getBulletSpeed() {
        return modifiers.bulletSpeed();
    }

    public int getModifierRotation() {
        return modifiers.rotation();
    }

    public int getAngleStep() {
        return modifiers.angleStep();
    }

    public int getMovementWave() {
        return modifiers.movementWave();
    }

    public boolean isRandomMovement() {
        return modifiers.randomMovement();
    }

    public boolean hasCircularRotation() {
        return modifiers.circularRotation() != null;
    }

    public int getCircularRotationStartDelay() {
        return modifiers.circularRotation() == null ? 0 : modifiers.circularRotation().startDelay();
    }

    public int getCircularRotationDuration() {
        return modifiers.circularRotation() == null ? 0 : modifiers.circularRotation().duration();
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getBossX() {
        return state.getBossX();
    }

    public float getBossY() {
        return state.getBossY();
    }

    public float getPlayerX() {
        return state.getPlayerX();
    }

    public float getPlayerY() {
        return state.getPlayerY();
    }

    public void spawnBullet(float vx, float vy, int maxBounces) {
        state.spawnEnemyBullet(
                state.getBossCenterX() - TouhouGameState.BOSS_BULLET_SIZE / 2.0F,
                state.getBossCenterY() - TouhouGameState.BOSS_BULLET_SIZE / 2.0F,
                vx,
                vy,
                600,
                maxBounces,
                0.0F
        );
    }

    public void spawnCircle(int count, float speed, int maxBounces, float angleOffsetDegrees) {
        state.spawnCircle(
                state.getBossCenterX(),
                state.getBossCenterY(),
                count,
                speed,
                maxBounces,
                angleOffsetDegrees
        );
    }

    public void spawnArc(int count, float speed, int maxBounces, float centerAngleDegrees, float spreadDegrees) {
        state.spawnArc(
                state.getBossCenterX(),
                state.getBossCenterY(),
                count,
                speed,
                maxBounces,
                centerAngleDegrees,
                spreadDegrees
        );
    }

    public void moveBy(float x, float y) {
        setBossPosition(state.getBossX() + x, state.getBossY() + y);
    }

    public void setBossPosition(float x, float y) {
        state.setBossX(Math.clamp(x, 0.0F, (float) (TouhouGameState.PLAY_WIDTH - TouhouGameState.BOSS_SIZE)));
        state.setBossY(Math.clamp(y, 0.0F, (float) (TouhouGameState.PLAY_HEIGHT - TouhouGameState.BOSS_SIZE)));
    }

    public void moveToPlayerX(float speed) {
        if (speed <= 0.0F) {
            return;
        }

        float targetX = state.getPlayerX() + TouhouGameState.PLAYER_SIZE / 2.0F - TouhouGameState.BOSS_SIZE / 2.0F;
        float delta = targetX - state.getBossX();
        if (Math.abs(delta) <= speed) {
            setBossPosition(targetX, state.getBossY());
        } else {
            setBossPosition(state.getBossX() + Math.copySign(speed, delta), state.getBossY());
        }
    }
}
