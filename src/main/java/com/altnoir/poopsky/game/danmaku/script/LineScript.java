package com.altnoir.poopsky.game.danmaku.script;

import com.altnoir.poopsky.game.danmaku.Boss;
import com.altnoir.poopsky.game.danmaku.BossModifiers;
import com.altnoir.poopsky.game.danmaku.BossScript;
import com.altnoir.poopsky.game.danmaku.RotationState;
import com.altnoir.poopsky.game.danmaku.modifier.BossModifierTemplate;
import com.altnoir.poopsky.game.danmaku.modifier.ConstantInt;
import com.altnoir.poopsky.game.danmaku.modifier.UniformFloat;
import com.altnoir.poopsky.game.danmaku.modifier.UniformInt;
import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class LineScript implements BossScript {
    private static final BossModifierTemplate TEMPLATE = BossModifierTemplate.builder()
            .bulletCount(ConstantInt.of(1))
            .attackInterval(UniformInt.of(1, 3))
            .bulletSpeed(UniformFloat.of(2.0F, 3.0F))
            .rotation(UniformInt.of(45))
            .angleStep(UniformInt.of(3))
            .build();

    private final RotationState rotation = new RotationState();
    private int intervalCounter;

    @Override
    public BossModifiers createModifiers(Random random, int wave) {
        rotation.randomizeDirection(random);
        return TEMPLATE.roll(random);
    }

    @Override
    public void tick(Boss boss, TouhouGameState state, Random random) {
        if (intervalCounter <= 0) {
            int angle = rotation.next(boss.getRotation(), boss.getAngleStep());

            state.spawnArc(
                    state.getBossCenterX(),
                    state.getBossCenterY(),
                    boss.getBulletCount(),
                    boss.getBulletSpeed(),
                    boss.getMaxBounces(),
                    90.0F + angle,
                    0.0F
            );
            intervalCounter = boss.getFireInterval();
        } else {
            intervalCounter--;
        }
    }
}
