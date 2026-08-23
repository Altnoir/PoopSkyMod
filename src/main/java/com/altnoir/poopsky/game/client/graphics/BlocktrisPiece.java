package com.altnoir.poopsky.game.client.graphics;

import com.altnoir.poopsky.game.util.BlocktrisShapes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class BlocktrisPiece {
    private final List<List<Vec2>> variants;
    private int x;
    private int y;
    private int rotation;
    private final int color;
    private final Grid grid;

    public BlocktrisPiece(int type, int x, int y, int color, Grid grid) {
        this.variants = BlocktrisShapes.PIECES.get(type);
        this.x = x;
        this.y = y;
        this.color = color;
        this.grid = grid;
    }

    public List<Vec2> current() {
        return variants.get(rotation);
    }

    public void render(GuiGraphics graphics, int posX, int posY) {
        render(graphics, posX, posY, 0.0F, 0.0F);
    }

    public void renderCentered(GuiGraphics graphics, int posX, int posY) {
        List<Vec2> parts = current();
        Vec2 first = parts.getFirst();
        float minX = first.x;
        float minY = first.y;
        float maxX = first.x;
        float maxY = first.y;

        for (Vec2 part : parts) {
            minX = Math.min(minX, part.x);
            minY = Math.min(minY, part.y);
            maxX = Math.max(maxX, part.x);
            maxY = Math.max(maxY, part.y);
        }

        render(graphics, posX, posY, -(minX + maxX) / 2.0F, -(minY + maxY) / 2.0F);
    }

    private void render(GuiGraphics graphics, int posX, int posY, float offsetX, float offsetY) {
        for (Vec2 part : current()) {
            int partX = posX + (int) ((x + part.x + offsetX) * grid.tileSize());
            int partY = posY + (int) ((y + part.y + offsetY) * grid.tileSize());
            grid.renderImage(graphics, partX, partY, color + 1);
        }
    }

    public void setRotation(int direction) {
        rotation = direction;
        if (rotation >= variants.size()) {
            rotation = variants.size() - 1;
        }
        if (rotation < 0) {
            rotation = 0;
        }
    }
}
