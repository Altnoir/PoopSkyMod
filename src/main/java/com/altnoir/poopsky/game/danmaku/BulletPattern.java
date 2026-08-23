package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

@FunctionalInterface
public interface BulletPattern {
    void fire(TouhouGameState state, Random random);
}
