package com.altnoir.poopsky.game.danmaku;

/**
 * Optional continuous-rotation configuration.
 *
 * @param startDelay ticks before rotation starts
 * @param duration   ticks the rotation lasts; <= 0 means it never stops
 */
public record CircularRotation(int startDelay, int duration) {
    public boolean neverStops() {
        return duration <= 0;
    }
}
