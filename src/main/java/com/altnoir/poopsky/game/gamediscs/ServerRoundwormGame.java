package com.altnoir.poopsky.game.gamediscs;

import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.game.controls.Button;
import com.altnoir.poopsky.game.util.GameStage;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class ServerRoundwormGame extends ServerGame {
    private final List<Vec2> body = new ArrayList<>();
    private Vec2 shit = new Vec2(12, 8);
    private Vec2 direction = new Vec2(1, 0);
    private Vec2 nextDirection;

    private static final List<Vec2> BLOCKED = List.of(
            new Vec2(0, 0), new Vec2(1, 0), new Vec2(2, 0),
            new Vec2(3, 0), new Vec2(4, 0), new Vec2(5, 0), new Vec2(6, 0)
    );

    @Override
    public void prepare() {
        super.prepare();
        body.clear();
        body.add(new Vec2(5, 5));
        body.add(new Vec2(6, 5));
        body.add(new Vec2(7, 5));
        respawnApple();
        direction = new Vec2(1, 0);
        nextDirection = null;
    }

    private void respawnApple() {
        while (true) {
            shit = new Vec2(random.nextInt(28), random.nextInt(20));
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

    @Override
    protected int gameTickDuration() {
        return 5;
    }

    @Override
    protected void buttonDown(Button button) {
        super.buttonDown(button);
        if (stage != GameStage.PLAYING) {
            return;
        }
        Vec2 next = switch (button) {
            case UP -> new Vec2(0, -1);
            case DOWN -> new Vec2(0, 1);
            case LEFT -> new Vec2(-1, 0);
            case RIGHT -> new Vec2(1, 0);
            default -> null;
        };
        if (next != null && !next.equals(direction) && !next.equals(new Vec2(-direction.x, -direction.y))) {
            nextDirection = next;
            playSound(SoundEvents.SLIME_SQUISH, 0.1F, 0.5F);
        }
    }

    @Override
    protected void gameTick() {
        if (nextDirection != null) {
            direction = nextDirection;
            nextDirection = null;
        }
        Vec2 head = body.getLast();
        Vec2 newPos = new Vec2(head.x + direction.x, head.y + direction.y);
        if (newPos.x < 0 || newPos.x >= 28 || newPos.y < 0 || newPos.y >= 20
                || containsPoint(body, newPos) || containsPoint(BLOCKED, newPos)) {
            stage = GameStage.DIED;
            playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
            return;
        }
        body.add(newPos);
        if (newPos.equals(shit)) {
            score++;
            playSound(SoundEvents.GENERIC_EAT, 0.8F, 0.8F);
            respawnApple();
        } else {
            body.removeFirst();
        }
    }

    @Override
    public CompoundTag writeSnapshot() {
        CompoundTag tag = super.writeSnapshot();
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
        return tag;
    }
}