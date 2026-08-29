package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class Image implements Renderer {
    private final Identifier file;
    private final int fileWidth, fileHeight, x, y, width, height;

    public Image(Identifier file, int width, int height) {
        this(file, width, height, 0, 0, width, height);
    }

    public Image(Identifier file, int fileWidth, int fileHeight, int x, int y, int width, int height) {
        this.file = file;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int posX, int posY) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, file, posX, posY,
                x, y, width, height, fileWidth, fileHeight);
    }

    @Override
    public void renderScaled(GuiGraphicsExtractor graphics, int posX, int posY, float scale) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(posX, posY);
        graphics.pose().scale(scale, scale);
        render(graphics, 0, 0);
        graphics.pose().popMatrix();
    }
}
