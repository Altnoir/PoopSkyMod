package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class Image implements Renderer {
    private final ResourceLocation file;
    private final int fileWidth, fileHeight, x, y, width, height;

    public Image(ResourceLocation file, int width, int height) {
        this(file, width, height, 0, 0, width, height);
    }

    public Image(ResourceLocation file, int fileWidth, int fileHeight, int x, int y, int width, int height) {
        this.file = file;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(GuiGraphics graphics, int posX, int posY) {
        graphics.blit(file, posX, posY, x, y, width, height, fileWidth, fileHeight);
    }

    @Override
    public void renderScaled(GuiGraphics graphics, int posX, int posY, float scale) {
        int w = Math.max(1, Math.round(width * scale));
        int h = Math.max(1, Math.round(height * scale));
        graphics.blit(file, posX, posY, w, h, x, y, width, height, fileWidth, fileHeight);
    }
}
