package com.altnoir.poopsky.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

final class LiquidRecipeLayout {
    static final int FIRST_X = 1;
    static final int SECOND_X = 37;
    static final int OUTPUT_X = 91;
    static final int INPUT_Y = 1;
    static final int BELOW_X = (FIRST_X + SECOND_X) / 2;
    static final int BELOW_Y = 19;
    static final int WIDTH = OUTPUT_X + 17;
    static final int HEIGHT = BELOW_Y + 17;

    private static final int ARROW_X = 61;
    private static final int ARROW_Y = INPUT_Y + 1;

    private final IDrawable slot;
    private final IDrawable arrow;
    private final IDrawable plus;

    LiquidRecipeLayout(IDrawable slot, IDrawable arrow, IDrawable plus) {
        this.slot = slot;
        this.arrow = arrow;
        this.plus = plus;
    }

    void draw(GuiGraphics graphics, boolean hasBelow) {
        slot.draw(graphics, FIRST_X - 1, INPUT_Y - 1);
        plus.draw(graphics, 20, INPUT_Y + 2);
        slot.draw(graphics, SECOND_X - 1, INPUT_Y - 1);
        arrow.draw(graphics, ARROW_X, ARROW_Y);
        slot.draw(graphics, OUTPUT_X - 1, INPUT_Y - 1);
        if (hasBelow) {
            slot.draw(graphics, BELOW_X - 1, BELOW_Y - 1);
        }
    }

    boolean isOverArrow(double mouseX, double mouseY) {
        return mouseX >= ARROW_X && mouseX < ARROW_X + arrow.getWidth()
                && mouseY >= ARROW_Y && mouseY < ARROW_Y + arrow.getHeight();
    }
}
