package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.game.danmaku.script.CircleBurstScript;
import com.altnoir.poopsky.game.danmaku.script.LineScript;
import com.altnoir.poopsky.game.danmaku.script.ReverseCircleScript;
import com.altnoir.poopsky.game.danmaku.script.RotatingCircleScript;

import java.util.Random;

public final class BossFactory {
    private BossFactory() {
    }

    public static Boss createRandomBoss(Random random, int wave) {
        BossScript script;
        String id;
        switch (random.nextInt(4)) {
            case 0 -> {
                script = new CircleBurstScript();
                id = "circle";
            }
            case 1 -> {
                script = new RotatingCircleScript();
                id = "fan";
            }
            case 2 -> {
                script = new LineScript();
                id = "line";
            }
            default -> {
                script = new ReverseCircleScript();
                id = "reverse";
            }
        }

        BossModifiers modifiers = script.createModifiers(random, wave);
        return new Boss(id, script, modifiers);
    }
}
