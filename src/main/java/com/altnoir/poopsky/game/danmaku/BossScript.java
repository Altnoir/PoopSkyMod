package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public interface BossScript {
    BossModifiers createModifiers(Random random, int wave);

    void tick(Boss boss, TouhouGameState state, Random random);
}
