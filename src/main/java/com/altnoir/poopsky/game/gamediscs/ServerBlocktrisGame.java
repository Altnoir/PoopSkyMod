package com.altnoir.poopsky.game.gamediscs;

import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.game.controls.Button;
import com.altnoir.poopsky.game.util.GameStage;
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

    private static final int GRID_WIDTH = 10;
    private static final int VISIBLE_HEIGHT = 20;
    private static final int HIDDEN_TOP_ROWS = 4;
    private static final int GRID_HEIGHT = VISIBLE_HEIGHT + HIDDEN_TOP_ROWS;

    private int[][] grid = new int[10][GRID_HEIGHT];
    private Piece piece;
    private final List<Piece> next = new ArrayList<>();
    private int placementCooldown;

    @Override
    public void prepare() {
        super.prepare();
        grid = new int[10][GRID_HEIGHT];
        next.clear();
        piece = new Piece(random.nextInt(7));
        placementCooldown = 0;
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
        for (int y = 0; y < VISIBLE_HEIGHT; y++) {
            int[] row = new int[GRID_WIDTH];
            for (int x = 0; x < GRID_WIDTH; x++) {
                row[x] = grid[x][y + HIDDEN_TOP_ROWS];
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
        private int y = -2;
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
                if (cx < 0 || cx >= GRID_WIDTH || cy < -HIDDEN_TOP_ROWS || cy >= VISIBLE_HEIGHT
                        || grid[cx][cy + HIDDEN_TOP_ROWS] != 0) {
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
                grid[x + (int) cell.x][y + (int) cell.y + HIDDEN_TOP_ROWS] = type + 1;
            }
        }
    }
}