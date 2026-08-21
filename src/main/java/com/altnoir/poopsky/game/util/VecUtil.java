package com.altnoir.poopsky.game.util;

import net.minecraft.world.phys.Vec2;

public final class VecUtil {
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    public static final Vec2 VEC_UP = new Vec2(0, -1);
    public static final Vec2 VEC_RIGHT = new Vec2(1, 0);
    public static final Vec2 VEC_DOWN = new Vec2(0, 1);
    public static final Vec2 VEC_LEFT = new Vec2(-1, 0);

    private VecUtil() {
    }

    public static int get4DirectionTo(Vec2 pos1, Vec2 pos2) {
        Vec2 pos = pos2.add(pos1.negated());
        return get4Direction(pos);
    }

    public static int get4Direction(Vec2 pos) {
        if (pos.x > pos.y) {
            if (pos.x + pos.y > 0) {
                return RIGHT;
            } else {
                return UP;
            }
        } else {
            if (pos.x + pos.y > 0) {
                return DOWN;
            } else {
                return LEFT;
            }
        }
    }

    public static Vec2 getFrom(int direction) {
        return switch (direction) {
            case UP -> VEC_UP;
            case RIGHT -> VEC_RIGHT;
            case DOWN -> VEC_DOWN;
            case LEFT -> VEC_LEFT;
            default -> Vec2.ZERO;
        };
    }
}
