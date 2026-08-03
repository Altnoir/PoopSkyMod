package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public final class TimeBellOverlay {
    private static final float FADE_TICKS = 9.0F;
    private static final float FLASH_TICKS = 5.0F;
    private static final int VIGNETTE_STEPS = 12;
    private static final int SEPIA_COLOR = 0x70471F;
    private static final int FLASH_COLOR = 0xFFE8AF;
    private static final float FOV_KICK_DURATION = 30.0F;
    private static final float FOV_BOOST = 1.25F;
    public static final double MAX_FOV = 175.0;

    private static volatile boolean frozen;
    private static float intensity;
    private static float flash;
    private static float fovKickTimer;

    private TimeBellOverlay() {
    }

    public static void setFrozen(boolean value) {
        if (value && !frozen) {
            flash = 1.0F;
            fovKickTimer = FOV_KICK_DURATION;
        }
        frozen = value;
    }

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        float deltaTicks = Math.min(deltaTracker.getRealtimeDeltaTicks(), 2.0F);
        fovKickTimer = Math.max(0.0F, fovKickTimer - deltaTicks);

        if (!Config.freezeFilter) {
            intensity = 0.0F;
            flash = 0.0F;
            return;
        }
        intensity = Mth.clamp(
                intensity + (frozen ? deltaTicks : -deltaTicks) / FADE_TICKS,
                0.0F,
                1.0F);
        flash = Math.max(0.0F, flash - deltaTicks / FLASH_TICKS);
        if (intensity <= 0.0F && flash <= 0.0F) return;

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();

        guiGraphics.fill(0, 0, width, height, color(SEPIA_COLOR, intensity * 0.38F));
        drawVignette(guiGraphics, width, height, intensity);

        if (flash > 0.0F) {
            float flashAlpha = flash * flash * 0.2F;
            guiGraphics.fill(0, 0, width, height, color(FLASH_COLOR, flashAlpha));
        }
        guiGraphics.flush();
    }

    private static void drawVignette(GuiGraphics guiGraphics, int width, int height, float alpha) {
        int maxInsetX = Math.max(VIGNETTE_STEPS, Mth.floor(width * 0.20F));
        int maxInsetY = Math.max(VIGNETTE_STEPS, Mth.floor(height * 0.20F));

        for (int step = 0; step < VIGNETTE_STEPS; step++) {
            int left = maxInsetX * step / VIGNETTE_STEPS;
            int right = width - left;
            int top = maxInsetY * step / VIGNETTE_STEPS;
            int bottom = height - top;
            int nextLeft = maxInsetX * (step + 1) / VIGNETTE_STEPS;
            int nextRight = width - nextLeft;
            int nextTop = maxInsetY * (step + 1) / VIGNETTE_STEPS;
            int nextBottom = height - nextTop;
            float strength = 1.0F - step / (float) VIGNETTE_STEPS;
            int shade = color(0x0B0705, alpha * strength * strength * 0.52F);

            guiGraphics.fill(left, top, right, nextTop, shade);
            guiGraphics.fill(left, nextBottom, right, bottom, shade);
            guiGraphics.fill(left, nextTop, nextLeft, nextBottom, shade);
            guiGraphics.fill(nextRight, nextTop, right, nextBottom, shade);
        }
    }

    private static int color(int rgb, float alpha) {
        return Mth.floor(Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F) << 24 | rgb;
    }

    public static double getFovMultiplier() {
        if (fovKickTimer <= 0.0F) return 1.0;
        float progress = 1.0F - fovKickTimer / FOV_KICK_DURATION;
        float raw = progress < 0.5F ? progress * 2.0F : (1.0F - progress) * 2.0F;
        float t = 1.0F - raw;
        float peaky = 1.0F - t * t * t;
        return 1.0 + peaky * FOV_BOOST;
    }
}
