package com.altnoir.poopsky.game.danmaku.modifier;

import java.util.Random;

@FunctionalInterface
public interface FloatProvider {
    float next(Random random);
}
