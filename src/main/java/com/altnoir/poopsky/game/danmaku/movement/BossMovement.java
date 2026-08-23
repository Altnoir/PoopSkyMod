package com.altnoir.poopsky.game.danmaku.movement;

import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public interface BossMovement {
    void tick(TouhouGameState state, Random random);

    BossMovement copy();
}
