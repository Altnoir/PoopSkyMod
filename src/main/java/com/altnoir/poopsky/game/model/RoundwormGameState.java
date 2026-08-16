package com.altnoir.poopsky.game.model;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class RoundwormGameState {
    public static final int GRID_WIDTH = 28;
    public static final int GRID_HEIGHT = 20;

    private static final List<Vec2> BLOCKED = List.of(
            new Vec2(0, 0), new Vec2(1, 0), new Vec2(2, 0),
            new Vec2(3, 0), new Vec2(4, 0), new Vec2(5, 0), new Vec2(6, 0)
    );

    private final List<Vec2> body = new ArrayList<>();
    private Vec2 shit = new Vec2(12, 8);
    private Vec2 direction = new Vec2(1, 0);
    @Nullable
    private Vec2 nextDirection;

    public void prepare(Random random) {
        body.clear();
        body.add(new Vec2(5, 5));
        body.add(new Vec2(6, 5));
        body.add(new Vec2(7, 5));
        respawn(random);
        direction = new Vec2(1, 0);
        nextDirection = null;
    }

    public boolean requestDirection(Vec2 next) {
        if (next == null || next.equals(direction) || next.equals(new Vec2(-direction.x, -direction.y))) {
            return false;
        }
        nextDirection = next;
        return true;
    }

    public TickResult tick(Random random) {
        if (nextDirection != null) {
            direction = nextDirection;
            nextDirection = null;
        }

        Vec2 head = body.getLast();
        Vec2 newPos = new Vec2(head.x + direction.x, head.y + direction.y);
        if (newPos.x < 0 || newPos.x >= GRID_WIDTH || newPos.y < 0 || newPos.y >= GRID_HEIGHT
                || containsPoint(body, newPos) || containsPoint(BLOCKED, newPos)) {
            return TickResult.DIED;
        }

        body.add(newPos);
        if (newPos.equals(shit)) {
            respawn(random);
            return TickResult.ATE;
        }

        body.removeFirst();
        return TickResult.MOVED;
    }

    public List<Vec2> body() {
        return Collections.unmodifiableList(body);
    }

    public Vec2 shit() {
        return shit;
    }

    public void writeSnapshot(CompoundTag tag) {
        int[] xs = new int[body.size()];
        int[] ys = new int[body.size()];
        for (int i = 0; i < body.size(); i++) {
            xs[i] = (int) body.get(i).x;
            ys[i] = (int) body.get(i).y;
        }
        tag.putIntArray("roundwormX", xs);
        tag.putIntArray("roundwormY", ys);
        tag.putInt("shitX", (int) shit.x);
        tag.putInt("shitY", (int) shit.y);
        tag.putInt("dirX", (int) direction.x);
        tag.putInt("dirY", (int) direction.y);
    }

    public void applySnapshot(CompoundTag tag) {
        int[] xs = tag.getIntArray("roundwormX");
        int[] ys = tag.getIntArray("roundwormY");
        body.clear();
        for (int i = 0; i < xs.length; i++) {
            body.add(new Vec2(xs[i], ys[i]));
        }
        shit = new Vec2(tag.getInt("shitX"), tag.getInt("shitY"));
        direction = new Vec2(tag.getInt("dirX"), tag.getInt("dirY"));
        nextDirection = null;
    }

    private void respawn(Random random) {
        while (true) {
            shit = new Vec2(random.nextInt(GRID_WIDTH), random.nextInt(GRID_HEIGHT));
            if (!containsPoint(BLOCKED, shit) && !containsPoint(body, shit)) {
                return;
            }
        }
    }

    private static boolean containsPoint(List<Vec2> points, Vec2 point) {
        for (Vec2 value : points) {
            if (value.equals(point)) {
                return true;
            }
        }
        return false;
    }

    public enum TickResult {
        MOVED,
        ATE,
        DIED
    }
}
