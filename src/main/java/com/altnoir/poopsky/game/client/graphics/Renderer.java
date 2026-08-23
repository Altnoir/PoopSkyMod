package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphics;

public class Renderer {
    public void render(GuiGraphics graphics, int posX, int posY) {
    }

    public void renderScaled(GuiGraphics graphics, int posX, int posY, float scale) {
        render(graphics, posX, posY);
    }
}
