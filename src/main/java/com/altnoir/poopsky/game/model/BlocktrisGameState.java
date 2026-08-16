package com.altnoir.poopsky.game.model;

import com.altnoir.poopsky.game.util.BlocktrisShapes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BlocktrisGameState {
    public static final int GRID_WIDTH = 10;
    public static final int VISIBLE_HEIGHT = 20;
    public static final int HIDDEN_TOP_ROWS = 4;
    public static final int GRID_HEIGHT = VISIBLE_HEIGHT + HIDDEN_TOP_ROWS;

    private final int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];
    private Piece piece;
    private final List<Piece> next = new ArrayList<>();
    private int placementCooldown;

    public void prepare(Random random) {
        clearGrid();
        next.clear();
        piece = new Piece(random.nextInt(BlocktrisShapes.PIECES.size()));
        placementCooldown = 0;
    }

    public void start(Random random) {
        for (int i = 0; i < 3; i++) {
            next.add(new Piece(random.nextInt(BlocktrisShapes.PIECES.size())));
        }
    }

    public void rotateCurrent() {
        piece.rotate();
    }

    public boolean moveCurrent(int dx, int dy) {
        return piece.move(dx, dy);
    }

    public void hardDropCurrent() {
        piece.hardDrop();
    }

    public PlacementResult placeCurrent(Random random) {
        piece.place();
        int clearedLines = clearLines();
        piece = next.removeFirst();
        next.add(new Piece(random.nextInt(BlocktrisShapes.PIECES.size())));
        return new PlacementResult(clearedLines, piece.touches());
    }

    public int getCurrentType() {
        return piece.type;
    }

    public int getCurrentX() {
        return piece.x;
    }

    public int getCurrentY() {
        return piece.y;
    }

    public int getCurrentRotation() {
        return piece.rotation;
    }

    public int[] nextTypes() {
        int[] types = new int[next.size()];
        for (int i = 0; i < next.size(); i++) {
            types[i] = next.get(i).type;
        }
        return types;
    }

    public int getPlacementCooldown() {
        return placementCooldown;
    }

    public void setPlacementCooldown(int placementCooldown) {
        this.placementCooldown = placementCooldown;
    }

    public void tickPlacementCooldown() {
        placementCooldown--;
        if (placementCooldown < 0) {
            placementCooldown = 0;
        }
    }

    public int getVisibleGrid(int x, int y) {
        return grid[x][y + HIDDEN_TOP_ROWS];
    }

    public void writeSnapshot(CompoundTag tag) {
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
        tag.putIntArray("nextTypes", nextTypes());
        tag.putInt("placementCooldown", placementCooldown);
    }

    public void applySnapshot(CompoundTag tag) {
        clearGrid();
        ListTag rows = tag.getList("grid", 11);
        for (int y = 0; y < rows.size(); y++) {
            int[] row = rows.getIntArray(y);
            for (int x = 0; x < row.length; x++) {
                grid[x][y + HIDDEN_TOP_ROWS] = row[x];
            }
        }

        piece = new Piece(tag.getInt("pieceType"));
        piece.x = tag.getInt("pieceX");
        piece.y = tag.getInt("pieceY");
        piece.rotation = tag.getInt("pieceRot");

        next.clear();
        for (int type : tag.getIntArray("nextTypes")) {
            next.add(new Piece(type));
        }
        placementCooldown = tag.getInt("placementCooldown");
    }

    private void clearGrid() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                grid[x][y] = 0;
            }
        }
    }

    private int clearLines() {
        int clearedLines = 0;
        for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                clearedLines++;
                for (int line = y; line > 0; line--) {
                    for (int x = 0; x < GRID_WIDTH; x++) {
                        grid[x][line] = grid[x][line - 1];
                    }
                }
                y++;
            }
        }
        return clearedLines;
    }

    private final class Piece {
        private final int type;
        private final List<List<Vec2>> variants;
        private int x = 4;
        private int y = -2;
        private int rotation;

        private Piece(int type) {
            this.type = type;
            this.variants = BlocktrisShapes.PIECES.get(type);
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

    public record PlacementResult(int linesCleared, boolean died) {
    }
}
