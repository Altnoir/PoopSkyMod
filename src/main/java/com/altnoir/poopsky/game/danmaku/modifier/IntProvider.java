package com.altnoir.poopsky.game.danmaku.modifier;

import java.util.Random;

@FunctionalInterface
public interface IntProvider {
    int next(Random random);
}
