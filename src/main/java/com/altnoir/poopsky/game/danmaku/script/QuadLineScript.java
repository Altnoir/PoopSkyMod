package com.altnoir.poopsky.game.danmaku.script;

import com.altnoir.poopsky.game.danmaku.Boss;
import com.altnoir.poopsky.game.danmaku.BossModifiers;
import com.altnoir.poopsky.game.danmaku.BossScript;
import com.altnoir.poopsky.game.danmaku.CircularRotation;
import com.altnoir.poopsky.game.danmaku.RotationState;
import com.altnoir.poopsky.game.danmaku.modifier.BossModifierTemplate;
import com.altnoir.poopsky.game.danmaku.modifier.ConstantInt;
import com.altnoir.poopsky.game.danmaku.modifier.UniformFloat;
import com.altnoir.poopsky.game.danmaku.modifier.UniformInt;
import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class QuadLineScript implements BossScript {
    private static final int DIRECTIONS = 4;
    private static final BossModifierTemplate TEMPLATE = BossModifierTemplate.builder()
            .bulletCount(ConstantInt.of(1))
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
        if (intervalCounter <= 0) {
            int angle = currentAngle(boss);

            for (int i = 0; i < DIRECTIONS; i++) {
                state.spawnArc(
                        state.getBossCenterX(),
                        state.getBossCenterY(),
                        boss.getBulletCount(),
                        boss.getBulletSpeed(),
                        boss.getMaxBounces(),
                        90.0F + angle + i * 90.0F,
                        0.0F
                );
            }

            intervalCounter = boss.getFireInterval();
        } else {
            intervalCounter--;
        }

        activeTicks++;
    }

    private int currentAngle(Boss boss) {
        CircularRotation circular = boss.getCircularRotation();
        if (circular == null) {
            return 0;
        }

        boolean rotating = activeTicks >= circular.startDelay()
                && (circular.neverStops() || activeTicks < circular.startDelay() + circular.duration());

        if (rotating) {
            return rotation.nextCircular(boss.getAngleStep());
        }

        return rotation.getCurrentAngle();
    }
}
