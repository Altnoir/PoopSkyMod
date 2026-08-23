package com.altnoir.poopsky.game.danmaku.script;

import com.altnoir.poopsky.game.danmaku.Boss;
import com.altnoir.poopsky.game.danmaku.BossModifiers;
import com.altnoir.poopsky.game.danmaku.BossScript;
import com.altnoir.poopsky.game.danmaku.RotationState;
import com.altnoir.poopsky.game.danmaku.modifier.BossModifierTemplate;
import com.altnoir.poopsky.game.danmaku.modifier.UniformFloat;
import com.altnoir.poopsky.game.danmaku.modifier.UniformInt;
import com.altnoir.poopsky.game.model.TouhouGameState;

import java.util.Random;

public class FanScript implements BossScript {
    private static final BossModifierTemplate TEMPLATE = BossModifierTemplate.builder()
            .bulletCount(UniformInt.of(10, 15))
            .attackInterval(UniformInt.of(8, 12))
            .bulletSpeed(UniformFloat.of(1.5F, 2.5F))
            .rotation(UniformInt.of(40))
            .angleStep(UniformInt.of(5))
            .build();
    private static final float SPREAD = 90.0F;

    private final RotationState rotation = new RotationState();
    private int intervalCounter;

    @Override
    public BossModifiers createModifiers(Random random, int wave) {
        rotation.randomizeDirection(random);
        return TEMPLATE.roll(random);
    }

    @Override
    public void tick(Boss boss, TouhouGameState state, Random random) {
        BossModifiers modifiers = boss.modifiers();
        if (intervalCounter <= 0) {
            int angle = rotation.next(modifiers.rotation(), modifiers.angleStep());

            state.spawnArc(
                    state.getBossCenterX(),
                    state.getBossCenterY(),
                    modifiers.bulletCount(),
                    modifiers.bulletSpeed(),
                    modifiers.maxBounces(),
                    90.0F + angle,
                    SPREAD
            );
            intervalCounter = modifiers.fireInterval();
        } else {
            intervalCounter--;
        }
    }
}
