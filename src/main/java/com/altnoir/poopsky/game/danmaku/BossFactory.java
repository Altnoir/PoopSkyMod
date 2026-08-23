package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.game.danmaku.script.*;

import java.util.Random;

public final class BossFactory {
    private BossFactory() {
    }

    public static Boss createRandomBoss(Random random, int wave) {
        BossScript script = switch (random.nextInt(5)) {
            case 0 -> new LineScript();
            case 1 -> new FanScript();
            case 2 -> new ReverseCircleScript();
            case 3 -> new QuadLineScript();
            default -> new CircleBurstScript();
        };
        return new Boss(script, script.createModifiers(random, wave));
    }
}
