package com.altnoir.poopsky.client.creative;

import com.altnoir.poopsky.impl.creative.PoSectionedCreativeModeTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;

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

    public static void onRender(CreativeModeInventoryScreen screen, GuiGraphics graphics) {
        if (CreativeModeInventoryScreen.selectedTab instanceof PoSectionedCreativeModeTab tab) {
            render(graphics, tab, screen.scrollOffs);
        }
    }

    private static void render(GuiGraphics graphics, PoSectionedCreativeModeTab tab, float scrollOffset) {
        int firstVisibleRow = tab.visibleStartRow(scrollOffset);
        Font font = Minecraft.getInstance().font;

        for (PoSectionedCreativeModeTab.SectionLayout section : tab.sectionLayouts()) {
            int visibleRow = section.headingRow() - firstVisibleRow;
            if (visibleRow < 0 || visibleRow >= VISIBLE_ROWS) {
                continue;
            }
            int y = GRID_TOP + visibleRow * ROW_HEIGHT;
            renderBanner(graphics, y);
            graphics.drawString(font, section.title(), GRID_LEFT + 7, y + 5, TEXT_HIGHLIGHT, false);
        }
    }

    private static void renderBanner(GuiGraphics graphics, int top) {
        graphics.fill(GRID_LEFT, top, GRID_LEFT + GRID_WIDTH, top + ROW_HEIGHT, BACKGROUND);
        graphics.fill(GRID_LEFT, top, GRID_LEFT + 1, top + ROW_HEIGHT, BORDER_MUTED);
        graphics.fill(GRID_LEFT + GRID_WIDTH - 1, top, GRID_LEFT + GRID_WIDTH, top + ROW_HEIGHT, BORDER_MUTED);
        graphics.fill(GRID_LEFT + 1, top, GRID_LEFT + GRID_WIDTH, top + 1, BORDER_PRIMARY);
        graphics.fill(GRID_LEFT + 1, top + ROW_HEIGHT - 1, GRID_LEFT + GRID_WIDTH, top + ROW_HEIGHT, BORDER_MUTED);
    }

    private PoSectionedCreativeTabRenderer() {
    }
}
