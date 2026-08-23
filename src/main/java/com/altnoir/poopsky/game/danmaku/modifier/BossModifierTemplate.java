package com.altnoir.poopsky.game.danmaku.modifier;

import com.altnoir.poopsky.game.danmaku.BossModifiers;
import com.altnoir.poopsky.game.danmaku.CircularRotation;

import java.util.Random;

public class BossModifierTemplate {
    private final IntProvider bulletCount;
    private final IntProvider maxBounces;
    private final IntProvider fireInterval;
    private final FloatProvider bulletSpeed;
    private final IntProvider rotation;
    private final IntProvider angleStep;
    private final CircularRotation circularRotation;

    private BossModifierTemplate(Builder builder) {
        this.bulletCount = builder.bulletCount;
        this.maxBounces = builder.maxBounces;
        this.fireInterval = builder.attackInterval;
        this.bulletSpeed = builder.bulletSpeed;
        this.rotation = builder.rotation;
        this.angleStep = builder.angleStep;
        this.circularRotation = builder.circularRotation;
    }

    public BossModifiers roll(Random random) {
        int count;
        int bounce;
        if (bulletCount instanceof UniformInt countRange && maxBounces instanceof UniformInt bounceRange) {
            count = countRange.next(random);
            bounce = rollLinkedBounce(count, countRange, bounceRange);
        } else {
            count = bulletCount.next(random);
            bounce = maxBounces.next(random);
        }

        return new BossModifiers(
                count,
                bounce,
                fireInterval.next(random),
                bulletSpeed.next(random),
                rotation.next(random),
                angleStep.next(random),
                circularRotation
        );
    }

    private static int rollLinkedBounce(int count, UniformInt countRange, UniformInt bounceRange) {
        int countMin = Math.min(countRange.min(), countRange.max());
        int countMax = Math.max(countRange.min(), countRange.max());
        int bounceMin = bounceRange.min();
        int bounceMax = bounceRange.max();
        int diff = Math.abs(bounceMax - bounceMin);
        if (diff <= 0 || countMax <= countMin) {
            return bounceMin;
        }

        float p = (count - countMin) / (float) (countMax - countMin);
        int segments = diff + 1;
        int segment = (int) Math.floor(p * segments);
        if (segment >= segments) {
            segment = segments - 1;
        }

        return bounceMin > bounceMax ? bounceMin - segment : bounceMin + segment;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private IntProvider bulletCount = random -> 20;
        private IntProvider maxBounces = random -> 0;
        private IntProvider attackInterval = random -> 8;
        private FloatProvider bulletSpeed = random -> 2.0F;
        private IntProvider rotation = random -> 0;
        private IntProvider angleStep = random -> 5;
        private CircularRotation circularRotation;
        private boolean rotationSet;

        public Builder bulletCount(IntProvider bulletCount) {
            this.bulletCount = bulletCount;
            return this;
        }

        public Builder maxBounces(IntProvider maxBounces) {
            this.maxBounces = maxBounces;
            return this;
        }

        public Builder attackInterval(IntProvider attackInterval) {
            this.attackInterval = attackInterval;
            return this;
        }

        public Builder bulletSpeed(FloatProvider bulletSpeed) {
            this.bulletSpeed = bulletSpeed;
            return this;
        }

        public Builder rotation(IntProvider rotation) {
            this.rotation = rotation;
            this.rotationSet = true;
            return this;
        }

        public Builder angleStep(IntProvider angleStep) {
            this.angleStep = angleStep;
            return this;
        }

        public Builder circularRotation() {
            this.circularRotation = new CircularRotation(0, 0);
            return this;
        }

        public Builder circularRotation(int startDelay, int duration) {
            this.circularRotation = new CircularRotation(startDelay, duration);
            return this;
        }

        public BossModifierTemplate build() {
            if (circularRotation != null && rotationSet) {
                throw new IllegalStateException(".circularRotation() and .rotation() are mutually exclusive");
            }
            return new BossModifierTemplate(this);
        }
    }
}
