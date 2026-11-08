package com.altnoir.poopsky.game.danmaku.script;

import com.altnoir.poopsky.game.danmaku.Boss;
import com.altnoir.poopsky.game.danmaku.BossModifiers;
import com.altnoir.poopsky.game.danmaku.BossScript;
import com.altnoir.poopsky.game.danmaku.modifier.BossModifierTemplate;
import com.altnoir.poopsky.game.danmaku.modifier.UniformFloat;
import com.altnoir.poopsky.game.danmaku.modifier.UniformInt;
import com.altnoir.poopsky.game.danmaku.movement.LeftRightBossMovement;
import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class CircleBurstScript implements BossScript {
    private static final BossModifierTemplate TEMPLATE = BossModifierTemplate.builder()
            .bulletCount(UniformInt.of(25, 40))
            .maxBounces(UniformInt.of(1, 0))
            .attackInterval(UniformInt.of(4, 8))
            .bulletSpeed(UniformFloat.of(2.0F, 3.0F))
            .movement(new LeftRightBossMovement(30.0F, 1.0F))
            .movementWave(2, true)
            .build();
    private static final int BURST_VOLLEYS = 4;
    private static final int IDLE_TICKS = 40;

    private int intervalCounter;

    @Override
    public BossModifiers createModifiers(Random random, int wave) {
        return TEMPLATE.roll(random);
    }

    private int volleysFired;
    private int idleCounter;
    private boolean idle;

    @Override
    public void tick(Boss boss, TouhouGameState state, Random random) {
        BossModifiers modifiers = boss.modifiers();
        if (idle) {
            idleCounter++;
            if (idleCounter >= IDLE_TICKS) {
                idle = false;
                idleCounter = 0;
                volleysFired = 0;
                intervalCounter = 0;
            }
            return;
        }

        if (intervalCounter <= 0) {
            state.spawnCircle(
                    state.getBossCenterX(),
                    state.getBossCenterY(),
                    modifiers.bulletCount(),
                    modifiers.bulletSpeed(),
                    modifiers.maxBounces(),
                    0.0F
            );
            intervalCounter = modifiers.fireInterval();
            volleysFired++;

            if (volleysFired >= BURST_VOLLEYS) {
                idle = true;
                idleCounter = 0;
                intervalCounter = 0;
            }
        } else {
            intervalCounter--;
        }
    }
}
