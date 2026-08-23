package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class Boss {
    private final String id;
    private final BossScript script;
    private final BossModifiers modifiers;

    public Boss(String id, BossScript script, BossModifiers modifiers) {
        this.id = id;
        this.script = script;
        this.modifiers = modifiers;
    }

    public void tick(TouhouGameState state, Random random) {
        script.tick(this, state, random);
    }

    public String getId() {
        return id;
    }

    public int getMaxBounces() {
        return modifiers.maxBounces();
    }

    public int getBulletCount() {
        return modifiers.bulletCount();
    }

    public int getFireInterval() {
        return modifiers.fireInterval();
    }

    public float getBulletSpeed() {
        return modifiers.bulletSpeed();
    }

    public int getRotation() {
        return modifiers.rotation();
    }

    public int getAngleStep() {
        return modifiers.angleStep();
    }

    public boolean hasCircularRotation() {
        return modifiers.circularRotation() != null;
    }

    public CircularRotation getCircularRotation() {
        return modifiers.circularRotation();
    }
}
