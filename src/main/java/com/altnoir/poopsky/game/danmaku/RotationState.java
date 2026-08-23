package com.altnoir.poopsky.game.danmaku;

import java.util.Random;

public class RotationState {
    private int currentAngle;
    private int direction = 1;

    public void randomizeDirection(Random random) {
        direction = random.nextBoolean() ? 1 : -1;
    }

    public int next(int maxAngle, int angleStep) {
        if (maxAngle <= 0 || angleStep <= 0) {
            return 0;
        }

        currentAngle += direction * angleStep;
        if (currentAngle >= maxAngle) {
            currentAngle = maxAngle;
            direction = -1;
        } else if (currentAngle <= -maxAngle) {
            currentAngle = -maxAngle;
            direction = 1;
        }
        return currentAngle;
    }
}
