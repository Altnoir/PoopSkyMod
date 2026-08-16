package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphics;

public class Grid {
    private final int[][] map;
    private final int size;
    private final MultiImage images;

    public Grid(int width, int height, int tileSize, MultiImage images) {
        map = new int[width][height];
        size = tileSize;
        this.images = images;
    }

    public int tileSize() {
        return size;
    }

    public void set(int x, int y, int value) {
        map[x][y] = value;
    }

    public MultiImage getImages() {
        return images;
    }

    public void render(GuiGraphics graphics, int posX, int posY) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                renderTile(graphics, posX, posY, x, y);
            }
        }
    }

    private void renderTile(GuiGraphics graphics, int posX, int posY, int x, int y) {
        images.setImage(map[x][y]);
        images.render(graphics, posX + x * size, posY + y * size);
    }
}
