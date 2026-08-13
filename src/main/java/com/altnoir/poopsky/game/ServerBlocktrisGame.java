package com.altnoir.poopsky.game;

import com.altnoir.poopsky.game.client.controls.Button;
import com.altnoir.poopsky.game.client.util.GameStage;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class ServerBlocktrisGame extends ServerGame {
    private static final List<List<List<Vec2>>> PIECES = List.of(
            List.of(
                    List.of(new Vec2(0, 0), new Vec2(0, -1), new Vec2(1, 0), new Vec2(0, 1)),
                    List.of(new Vec2(0, 0), new Vec2(-1, 0), new Vec2(1, 0), new Vec2(0, 1)),
                    List.of(new Vec2(0, 0), new Vec2(0, -1), new Vec2(-1, 0), new Vec2(0, 1)),
                    List.of(new Vec2(0, 0), new Vec2(0, -1), new Vec2(1, 0), new Vec2(-1, 0))
            ),
            List.of(List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(-1, 1)),
                    List.of(new Vec2(-1, -1), new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0)),
                    List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, -1)),
                    List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, 1))),
            List.of(List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(0, 2)),
                    List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0), new Vec2(2, 0))),
            List.of(List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(-1, 1), new Vec2(0, -1)),
                    List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1))),
            List.of(List.of(new Vec2(0, 0), new Vec2(1, 0), new Vec2(0, 1), new Vec2(1, 1))),
            List.of(List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1)),
                    List.of(new Vec2(-1, 1), new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0)),
                    List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(-1, -1)),
                    List.of(new Vec2(-1, 0), new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, -1))),
            List.of(List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, 1)),
                    List.of(new Vec2(0, -1), new Vec2(0, 0), new Vec2(1, -1), new Vec2(-1, 0)))
    );

    private int[][] grid = new int[10][20];
    private Piece piece;
    private final List<Piece> next = new ArrayList<>();
    private int placementCooldown;

    @Override
    public void prepare() {
        grid = new int[10][20];
        next.clear();
        piece = new Piece(random.nextInt(7));
        placementCooldown = 0;
        score = 0;
        stage = GameStage.START;
        ticks = 1;
    }

    @Override
    public void start() {
        super.start();
        for (int i = 0; i < 3; i++) {
            next.add(new Piece(random.nextInt(7)));
        }
    }

    @Override
    protected void buttonDown(Button button) {
        super.buttonDown(button);
        if (stage != GameStage.PLAYING) {
            return;
        }
        switch (button) {
            case UP -> {
                piece.rotate();
                playSound(PoSoundEvents.SWING.get(), 1.5F, 0.5F);
            }
            case LEFT -> {
                piece.move(-1, 0);
                playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
                placementCooldown = 10;
            }
            case RIGHT -> {
                piece.move(1, 0);
                playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
                placementCooldown = 10;
            }
            case DOWN -> {
                if (piece.move(0, 1)) {
                    place();
                } else {
                    playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
                    placementCooldown = 10;
                }
            }
            case BUTTON1 -> {
                piece.hardDrop();
                playSound(PoSoundEvents.EXPLOSION.get(), 0.7F, 0.5F);
                placementCooldown = 0;
                place();
                placementCooldown = 10;
            }
            default -> {
            }
        }
    }

    @Override
    protected void gameTick() {
        if (piece.move(0, 1)) {
            place();
        }
    }

    @Override
    protected int gameTickDuration() {
        return (int) (10f / ((float) score / 50f + 1f));
    }

    @Override
    protected void extraTick() {
        placementCooldown--;
        if (placementCooldown < 0) {
            placementCooldown = 0;
        }
        if (stage != GameStage.PLAYING || ticks % 2 != 0 || placementCooldown > 0) {
            return;
        }
        if (leftDown) {
            piece.move(-1, 0);
            playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
        }
        if (rightDown) {
            piece.move(1, 0);
            playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
        }
        if (downDown && piece.move(0, 1)) {
            playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
            place();
        }
    }

    private void place() {
        piece.place();
        for (int y = grid[0].length - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < grid.length; x++) {
                if (grid[x][y] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                score++;
                playSound(PoSoundEvents.POINT.get(), 1.0F, 0.7F);
                for (int line = y; line > 0; line--) {
                    for (int x = 0; x < grid.length; x++) {
                        grid[x][line] = grid[x][line - 1];
                    }
                }
                y++;
            }
        }
        piece = next.removeFirst();
        next.add(new Piece(random.nextInt(7)));
        if (piece.touches()) {
            stage = GameStage.DIED;
            playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
        }
    }

    @Override
    public CompoundTag writeSnapshot() {
        CompoundTag tag = super.writeSnapshot();
        ListTag rows = new ListTag();
        for (int y = 0; y < grid[0].length; y++) {
            int[] row = new int[grid.length];
            for (int x = 0; x < grid.length; x++) {
                row[x] = grid[x][y];
            }
            rows.add(new IntArrayTag(row));
        }
        tag.put("grid", rows);
        tag.putInt("pieceType", piece.type);
        tag.putInt("pieceX", piece.x);
        tag.putInt("pieceY", piece.y);
        tag.putInt("pieceRot", piece.rotation);
        int[] nextTypes = new int[next.size()];
        for (int i = 0; i < next.size(); i++) {
            nextTypes[i] = next.get(i).type;
        }
        tag.putIntArray("nextTypes", nextTypes);
        tag.putInt("placementCooldown", placementCooldown);
        return tag;
    }

    private final class Piece {
        private final int type;
        private final List<List<Vec2>> variants;
        private int x = 4;
        private int y = 1;
        private int rotation;

        private Piece(int type) {
            this.type = type;
            this.variants = PIECES.get(type);
        }

        private List<Vec2> cells() {
            return variants.get(rotation);
        }

        private boolean touches() {
            for (Vec2 cell : cells()) {
                int cx = x + (int) cell.x;
                int cy = y + (int) cell.y;
                if (cx < 0 || cx >= 10 || cy < 0 || cy >= 20 || grid[cx][cy] != 0) {
                    return true;
                }
            }
            return false;
        }

        private boolean move(int dx, int dy) {
            x += dx;
            boolean blocked = touches();
            if (blocked) {
                x -= dx;
            }
            y += dy;
            if (touches()) {
                y -= dy;
                blocked = true;
            }
            return blocked;
        }

        private void rotate() {
            rotation = (rotation + 1) % variants.size();
            if (touches()) {
                rotation = (rotation - 1 + variants.size()) % variants.size();
            }
        }

        private void hardDrop() {
            while (!move(0, 1)) {
            }
        }

        private void place() {
            for (Vec2 cell : cells()) {
                grid[x + (int) cell.x][y + (int) cell.y] = type + 1;
            }
        }
    }
}