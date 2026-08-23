package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface Renderer {
    void render(GuiGraphicsExtractor graphics, int posX, int posY);

    default void renderScaled(GuiGraphicsExtractor graphics, int posX, int posY, float scale) {
        render(graphics, posX, posY);
    }
}

