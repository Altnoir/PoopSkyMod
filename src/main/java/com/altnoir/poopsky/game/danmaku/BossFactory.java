package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.game.danmaku.script.*;

import java.util.Random;

public final class BossFactory {
    private BossFactory() {
    }

    public static Boss createRandomBoss(Random random, int wave) {
        BossScript script;
        String id;
        switch (random.nextInt(5)) {
            case 0 -> {
                script = new LineScript();
                id = "line";
            }
            case 1 -> {
                script = new FanScript();
                id = "fan";
            }
            case 2 -> {
                script = new ReverseCircleScript();
                id = "reverse";
            }
            case 3 -> {
                script = new QuadLineScript();
                id = "quadline";
            }
            default -> {
                script = new CircleBurstScript();
                id = "circle";
            }
        }

        BossModifiers modifiers = script.createModifiers(random, wave);
        return new Boss(id, script, modifiers);
    }
}
