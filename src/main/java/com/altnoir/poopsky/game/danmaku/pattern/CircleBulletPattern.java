package com.altnoir.poopsky.game.danmaku.pattern;

public class CircleBulletPattern extends PolygonBulletPattern {
    public CircleBulletPattern() {
        this(12, 0, 1.0F);
    }

    public CircleBulletPattern(int bulletCount, int maxBounces, float bulletSpeed) {
        super(0, bulletCount, maxBounces, bulletSpeed);
    }
}
