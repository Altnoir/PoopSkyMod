package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphics;

public interface Renderer {
    void render(GuiGraphics graphics, int posX, int posY);

    default void renderScaled(GuiGraphics graphics, int posX, int posY, float scale) {
        render(graphics, posX, posY);
    }
}
