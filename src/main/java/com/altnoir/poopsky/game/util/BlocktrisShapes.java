package com.altnoir.poopsky.game.util;

import net.minecraft.world.phys.Vec2;

import java.util.List;

public final class BlocktrisShapes {
    public static final List<List<List<Vec2>>> PIECES = List.of(
            List.of(
                    List.of(new Vec2(0, 0), new Vec2(0, -1), new Vec2(1, 0), new Vec2(0, 1)),
                    List.of(new Vec2(0, 0), new Vec2(-1, 0), new Vec2(1, 0), new Vec2(0, 1)),
                    List.of(new Vec2(0, 0), new Vec2(0, -1), new Vec2(-1, 0), new Vec2(0, 1)),
                    List.of(new Vec2(0, 0), new Vec2(0, -1), new Vec2(1, 0), new Vec2(-1, 0))
            ),
            List.of(
                    List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(-1, 1)),
                    List.of(new Vec2(-1, -1), new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0)),
                    List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, -1)),
                    List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, 1))
            ),
            List.of(
                    List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(0, 2)),
                    List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0), new Vec2(2, 0))
            ),
            List.of(
                    List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(-1, 1), new Vec2(0, -1)),
                    List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1))
            ),
            List.of(
                    List.of(new Vec2(0, 0), new Vec2(1, 0), new Vec2(0, 1), new Vec2(1, 1))
            ),
            List.of(
                    List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1)),
                    List.of(new Vec2(-1, 1), new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0)),
                    List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(-1, -1)),
                    List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, -1))
            ),
            List.of(
                    List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, 1)),
                    List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(1, -1), new Vec2(-1, 0))
            )
    );

    private BlocktrisShapes() {
    }
}
