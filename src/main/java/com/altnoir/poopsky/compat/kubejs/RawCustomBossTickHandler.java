package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.game.danmaku.Boss;
import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

@FunctionalInterface
public interface RawCustomBossTickHandler {
    void tick(Boss boss, TouhouGameState state, Random random);
}
