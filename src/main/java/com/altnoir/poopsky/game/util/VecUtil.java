package com.altnoir.poopsky.game.util;

import net.minecraft.world.phys.Vec2;

import java.util.List;
import java.util.Random;
public class VecUtil {
    // Direction constants as integers
    public static int UP = 0;
    public static int RIGHT = 1;
    public static int DOWN = 2;
    public static int LEFT = 3;

    // Direction constants as relative Vec2
    public static Vec2 VEC_UP = new Vec2(0, -1);
    public static Vec2 VEC_RIGHT = new Vec2(1, 0);
    public static Vec2 VEC_DOWN = new Vec2(0, 1);
    public static Vec2 VEC_LEFT = new Vec2(-1, 0);
    public static final List<Vec2> RELATIVES = List.of(
            new Vec2(-1, -1),
            new Vec2(0, -1),
            new Vec2(1, -1),
            new Vec2(1, 0),
            new Vec2(1, 1),
            new Vec2(0, 1),
            new Vec2(-1, 1),
            new Vec2(-1, 0)
    );

    /**
     * @param xy X and Y
     * @return Vec2 made of xy, and xy
     */
    public static Vec2 of(float xy) {
        return new Vec2(xy, xy);
    }

    /**
     * Get four directional direction
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @return Integer of four directional directions
     * @see VecUtil#getFrom(int) for transfering four dir. direction into relative Vec2
     */
    public static int get4DirectionTo(Vec2 pos1, Vec2 pos2) {
        Vec2 pos = pos2.add(pos1.negated());
        return get4Direction(pos);
    }

    /**
     * Get four directional direction
     * @param pos Position of the direction measuring
     * @return Integer of four directional directions
     * @see VecUtil#getFrom(int) for transfering four dir. direction into relative Vec2
     */
    public static int get4Direction(Vec2 pos) {
        if (pos.x > pos.y) {
            if (pos.x + pos.y > 0) {
                return RIGHT;
            }
            else {
                return UP;
            }
        }
        else {
            if (pos.x + pos.y > 0) {
                return DOWN;
            }
            else {
                return LEFT;
            }
        }
    }

    /**
     * @param direction Integer value of four directional direction
     * @return Vec2 relative direction
     */
    public static Vec2 getFrom(int direction) {
        return switch (direction) {
            case 0 -> VEC_UP;
            case 1 -> VEC_RIGHT;
            case 2 -> VEC_DOWN;
            case 3 -> VEC_LEFT;
            default -> Vec2.ZERO;
        };
    }

    /**
     * @return True if pos1 is the same as pos2, false otherwise
     */
    public static boolean is(Vec2 pos1, Vec2 pos2) {
        return (pos1.x == pos2.x && pos1.y == pos2.y);
    }

    /**
     * @param min Minimal Vec2
     * @param max Maximal Vec2
     * @param random Random generator
     * @return Random Vec2 Integer position within the range of min and max
     */
    public static Vec2 randomInt(Vec2 min, Vec2 max, Random random) {
        return new Vec2(random.nextInt((int)min.x, (int)max.x), random.nextInt((int)min.y, (int)max.y));
    }

    /**
     * @param min Minimal Vec2
     * @param max Maximal Vec2
     * @param random Random generator
     * @return Random Vec2 float position within the range of min and max
     */
    public static Vec2 randomFloat(Vec2 min, Vec2 max, Random random) {
        return new Vec2(random.nextFloat(min.x, max.x), random.nextFloat(min.y, max.y));
    }

    /**
     * @return Rounded vector
     */
    public static Vec2 round(Vec2 vec) {
        return new Vec2(Math.round(vec.x), Math.round(vec.y));
    }
}
