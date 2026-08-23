package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public record Boss(BossScript script, BossModifiers modifiers) {
    public void tick(TouhouGameState state, Random random) {
        script.tick(this, state, random);
    }
}
