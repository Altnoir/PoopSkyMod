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
    private int color;
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

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(GuiGraphics graphics, int posX, int posY) {
        grid.getImages().setImage(color + 1);
        for (Vec2 part : current()) {
            grid.getImages().render(graphics, posX + (x + (int) part.x) * grid.tileSize(), posY + (y + (int) part.y) * grid.tileSize());
        }
    }

    public void renderCentered(GuiGraphics graphics, int posX, int posY) {
        grid.getImages().setImage(color + 1);
        Vec2 smallest = null;
        Vec2 biggest = null;
        for (int i = 0; i < current().size(); i++) {
            Vec2 part = current().get(i);
            if (smallest == null) {
                smallest = part;
            }
            if (biggest == null) {
                biggest = part;
            }
            smallest = new Vec2(Math.min(smallest.x, part.x), Math.min(smallest.y, part.y));
            biggest = new Vec2(Math.max(biggest.x, part.x), Math.max(biggest.y, part.y));
        }
        Vec2 addition = smallest.add(biggest.negated()).scale(0.5f).add(biggest).negated();
        for (Vec2 part : current()) {
            grid.getImages().render(graphics, posX + (int) ((x + part.x + addition.x) * grid.tileSize()), posY + (int) ((y + (int) part.y + addition.y) * grid.tileSize()));
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
