package com.altnoir.poopsky.game.danmaku;

import com.altnoir.poopsky.compat.kubejs.CustomBossDefinition;
import com.altnoir.poopsky.compat.kubejs.KubeBossScript;
import com.altnoir.poopsky.compat.kubejs.PoCustomBosses;
import com.altnoir.poopsky.game.danmaku.script.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BossFactory {
    private BossFactory() {
    }

    public static Boss createRandomBoss(Random random, int wave) {
        List<CustomBossDefinition> customBosses = new ArrayList<>();
        int totalWeight = 5;
        int displayWave = wave + 1;
        for (CustomBossDefinition definition : PoCustomBosses.INSTANCE.definitions()) {
            if (displayWave >= definition.minWave()) {
                customBosses.add(definition);
                totalWeight += definition.weight();
            }
        }

        int selection = random.nextInt(totalWeight);
        if (selection >= 5) {
            selection -= 5;
            for (CustomBossDefinition definition : customBosses) {
                if (selection < definition.weight()) {
                    BossScript script = new KubeBossScript(definition);
                    return new Boss(script, script.createModifiers(random, wave));
                }
                selection -= definition.weight();
            }
        }

        BossScript script = switch (selection) {
            case 0 -> new LineScript();
            case 1 -> new FanScript();
            case 2 -> new ReverseCircleScript();
            case 3 -> new QuadLineScript();
            default -> new CircleBurstScript();
        };
        return new Boss(script, script.createModifiers(random, wave));
    }
}
