package com.altnoir.poopsky.game.danmaku.script;

import com.altnoir.poopsky.game.danmaku.*;
import com.altnoir.poopsky.game.danmaku.modifier.BossModifierTemplate;
import com.altnoir.poopsky.game.danmaku.modifier.UniformFloat;
import com.altnoir.poopsky.game.danmaku.modifier.UniformInt;
import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class QuadLineScript implements BossScript {
    private static final int DIRECTIONS = 4;
    private static final BossModifierTemplate TEMPLATE = BossModifierTemplate.builder()
            .bulletCount(UniformInt.of(1))
            .attackInterval(UniformInt.of(4, 6))
            .bulletSpeed(UniformFloat.of(1.5F, 2.5F))
            .circularRotation(20, 0)
            .angleStep(UniformInt.of(5))
            .build();

    private final RotationState rotation = new RotationState();
    private int intervalCounter;
    private int activeTicks;

    @Override
    public BossModifiers createModifiers(Random random, int wave) {
        rotation.randomizeDirection(random);
        return TEMPLATE.roll(random);
    }

    @Override
    public void tick(Boss boss, TouhouGameState state, Random random) {
        BossModifiers modifiers = boss.modifiers();
        if (intervalCounter <= 0) {
            int angle = currentAngle(modifiers);
            float centerX = state.getBossCenterX();
            float centerY = state.getBossCenterY();

            for (int i = 0; i < DIRECTIONS; i++) {
                state.spawnArc(
                        centerX,
                        centerY,
                        modifiers.bulletCount(),
                        modifiers.bulletSpeed(),
                        modifiers.maxBounces(),
                        90.0F + angle + i * 90.0F,
                        0.0F
                );
            }

            intervalCounter = modifiers.fireInterval();
        } else {
            intervalCounter--;
        }

        activeTicks++;
    }

    private int currentAngle(BossModifiers modifiers) {
        CircularRotation circular = modifiers.circularRotation();
        if (circular == null) {
            return 0;
        }

        boolean rotating = activeTicks >= circular.startDelay()
                && (circular.neverStops() || activeTicks < circular.startDelay() + circular.duration());

        if (rotating) {
            return rotation.nextCircular(modifiers.angleStep());
        }

        return rotation.getCurrentAngle();
    }
}
