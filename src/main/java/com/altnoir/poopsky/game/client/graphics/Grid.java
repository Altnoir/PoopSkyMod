package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Grid {
    private final int[][] map;
    private final int tileSize;
    private final MultiImage images;

    public Grid(int width, int height, int tileSize, MultiImage images) {
        map = new int[width][height];
        this.tileSize = tileSize;
        this.images = images;
    }

    public int tileSize() {
        return tileSize;
    }

    public void set(int x, int y, int value) {
        map[x][y] = value;
    }

    public void render(GuiGraphicsExtractor graphics, int posX, int posY) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                renderImage(graphics, posX + x * tileSize, posY + y * tileSize, map[x][y]);
            }
        }
    }

    public void renderImage(GuiGraphicsExtractor graphics, int posX, int posY, int image) {
        images.setImage(image).render(graphics, posX, posY);
    }
}

