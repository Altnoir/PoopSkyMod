package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class TimeBellOverlay {
    private static volatile boolean frozen = false;

    public static void setFrozen(boolean value) {
        frozen = value;
    }

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!frozen || Config.freezeFilter) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();

        guiGraphics.fill(0, 0, width, height, 0xA08B4513);
    }
}