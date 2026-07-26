package com.altnoir.poopsky.client.creative;

import com.altnoir.poopsky.impl.creative.PoSectionedCreativeModeTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public final class PoSectionedCreativeTabRenderer {
    private static final int VISIBLE_ROWS = 5;
    private static final int GRID_LEFT = 8;
    private static final int GRID_TOP = 17;
    private static final int GRID_WIDTH = 162;
    private static final int ROW_HEIGHT = 18;

    private static final int BACKGROUND = 0xFF4A3728;
    private static final int BORDER_MUTED = 0xFF6B5440;
    private static final int BORDER_PRIMARY = 0xFF8B7355;
    private static final int TEXT_HIGHLIGHT = 0xFFD4C4A8;

    private static int visibleHeadingRows;

    public static void render(
            GuiGraphics graphics,
            PoSectionedCreativeModeTab tab,
            float scrollOffset,
            int screenLeft,
            int screenTop
    ) {
        int firstVisibleRow = tab.visibleStartRow(scrollOffset);
        int left = screenLeft + GRID_LEFT;
        int top = screenTop + GRID_TOP;
        Font font = Minecraft.getInstance().font;
        int headingRows = 0;

        for (PoSectionedCreativeModeTab.SectionLayout section : tab.sectionLayouts()) {
            int visibleRow = section.headingRow() - firstVisibleRow;
            if (visibleRow < 0 || visibleRow >= VISIBLE_ROWS) {
                continue;
            }
            headingRows |= 1 << visibleRow;

            int y = top + visibleRow * ROW_HEIGHT;
            if (section.bannerSprite() != null) {
                graphics.blitSprite(section.bannerSprite(), left, y, GRID_WIDTH, ROW_HEIGHT);
            } else {
                renderDefaultBanner(graphics, left, y);
            }
            graphics.drawString(
                    font,
                    section.title(),
                    left + 7,
                    y + 5,
                    TEXT_HIGHLIGHT,
                    false
            );
        }
        visibleHeadingRows = headingRows;
    }

    public static boolean isHeadingSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= VISIBLE_ROWS * 9) {
            return false;
        }
        return (visibleHeadingRows & 1 << (slotIndex / 9)) != 0;
    }

    public static void clearHeadingSlots() {
        visibleHeadingRows = 0;
    }

    private static void renderDefaultBanner(GuiGraphics graphics, int left, int top) {
        graphics.fill(left, top, left + GRID_WIDTH, top + ROW_HEIGHT, BACKGROUND);
        graphics.fill(left, top, left + 1, top + ROW_HEIGHT, BORDER_MUTED);
        graphics.fill(left + GRID_WIDTH - 1, top, left + GRID_WIDTH, top + ROW_HEIGHT, BORDER_MUTED);
        graphics.fill(left + 1, top, left + GRID_WIDTH, top + 1, BORDER_PRIMARY);
        graphics.fill(left + 1, top + ROW_HEIGHT - 1, left + GRID_WIDTH, top + ROW_HEIGHT, BORDER_MUTED);
    }

    private PoSectionedCreativeTabRenderer() {
    }
}