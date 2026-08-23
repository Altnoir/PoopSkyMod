package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Renderer {
    public void render(GuiGraphicsExtractor graphics, int posX, int posY) {
    }

    public void renderScaled(GuiGraphicsExtractor graphics, int posX, int posY, float scale) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(posX, posY);
        graphics.pose().scale(scale, scale);
        render(graphics, 0, 0);
        graphics.pose().popMatrix();
    }
}
