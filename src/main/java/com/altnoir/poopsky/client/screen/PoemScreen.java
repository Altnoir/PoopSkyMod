package com.altnoir.poopsky.client.screen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoAnimationController;
import com.altnoir.poopsky.init.PoSoundEvents;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class PoemScreen extends Screen {
    private static final String TEXT_DIRECTORY = "texts/poopsky/";
    private static final ResourceLocation FALLBACK_TEXT_LOCATION = PoopSky.loc(TEXT_DIRECTORY + "en_us.txt");
    private static final ResourceLocation TITLE_LOCATION = PoopSky.loc("textures/gui/poopsky.png");
    private static final ResourceLocation VIGNETTE_LOCATION = PoopSky.mcloc("textures/misc/credits_vignette.png");

    private static final int CONTENT_WIDTH = 256;
    private static final int TITLE_TEXTURE_WIDTH = 710;
    private static final int TITLE_TEXTURE_HEIGHT = 154;
    private static final int TITLE_WIDTH = 192;
    private static final int TITLE_HEIGHT = 42;
    private static final float BASE_SCROLL_SPEED = 0.5F;
    private static final float INITIAL_SCROLL_SPEED = 4.0F;
    private static final int SCROLL_DECELERATION_TICKS = 40;
    private static final int TITLE_HOLD_TICKS = 10;
    private static final int TITLE_ROTATION_TICKS = 30;
    private static final int SCROLL_START_TICK = TITLE_HOLD_TICKS + TITLE_ROTATION_TICKS;
    private static final Music POEM_MUSIC = new Music(PoSoundEvents.THEME, 0, 0, true);

    private final List<FormattedCharSequence> lines = new ArrayList<>();
    private final IntSet speedupModifiers = new IntOpenHashSet();
    private float scroll;
    private float previousScroll;
    private int playbackTicks;
    private int scrollingTicks;
    private int direction = 1;
    private boolean speedupActive;
    private boolean finished;

    public PoemScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    protected void init() {
        if (!this.lines.isEmpty()) return;

        ResourceLocation textLocation = this.getTextLocation();
        try (Reader reader = this.minecraft.getResourceManager().openAsReader(textLocation);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    this.lines.add(FormattedCharSequence.EMPTY);
                } else {
                    this.lines.addAll(this.font.split(Component.literal(line), CONTENT_WIDTH));
                }
            }
        } catch (Exception exception) {
            PoopSky.LOGGER.error("Couldn't load PoopSky poem text from {}", textLocation, exception);
        }

    }

    private ResourceLocation getTextLocation() {
        String languageCode = this.minecraft.getLanguageManager().getSelected();
        ResourceLocation localizedTextLocation = PoopSky.loc(TEXT_DIRECTORY + languageCode + ".txt");
        if (this.minecraft.getResourceManager().getResource(localizedTextLocation).isPresent()) {
            return localizedTextLocation;
        }
        return FALLBACK_TEXT_LOCATION;
    }

    @Override
    public void tick() {
        this.minecraft.getMusicManager().tick();
        this.minecraft.getSoundManager().tick(false);
        this.playbackTicks++;
        if (this.playbackTicks <= SCROLL_START_TICK) return;

        this.previousScroll = this.scroll;
        this.scrollingTicks++;
        this.scroll = Math.max(0.0F, this.scroll + this.calculateScrollSpeed());
        if (this.scroll > this.endScrollPosition()) {
            this.finish();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderVignette(guiGraphics);

        float renderedScroll = Mth.lerp(partialTick, this.previousScroll, this.scroll);
        float offset = -renderedScroll;
        int contentLeft = this.width / 2 - CONTENT_WIDTH / 2;
        int titleLeft = this.width / 2 - TITLE_WIDTH / 2;
        int titleTop = (this.height - TITLE_HEIGHT) / 2;
        int lineY = this.height + 24;
        float rotation = this.titleRotation(partialTick);
        boolean scrolling = this.playbackTicks >= SCROLL_START_TICK;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.width / 2.0F, this.height / 2.0F, 0.0F);
        if (scrolling) {
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(180.0F));
        } else {
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rotation));
        }
        guiGraphics.pose().translate(-this.width / 2.0F, -this.height / 2.0F, 0.0F);
        guiGraphics.pose().translate(0.0F, offset, 0.0F);

        guiGraphics.blit(
                TITLE_LOCATION,
                titleLeft,
                titleTop,
                TITLE_WIDTH,
                TITLE_HEIGHT,
                0.0F,
                0.0F,
                TITLE_TEXTURE_WIDTH,
                TITLE_TEXTURE_HEIGHT,
                TITLE_TEXTURE_WIDTH,
                TITLE_TEXTURE_HEIGHT
        );

        if (scrolling) {
            for (FormattedCharSequence line : this.lines) {
                if (lineY + offset + 20.0F > 0.0F && lineY + offset < this.height) {
                    this.drawVanillaLine(guiGraphics, line, contentLeft, lineY, 0xFFFFFFFF);
                }
                lineY += 12;
            }
        }

        guiGraphics.flush();
        guiGraphics.pose().popPose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.direction = -1;
        } else if (keyCode == 341 || keyCode == 345) {
            this.speedupModifiers.add(keyCode);
        } else if (keyCode == 32) {
            this.speedupActive = true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.direction = 1;
        }
        if (keyCode == 32) {
            this.speedupActive = false;
        } else if (keyCode == 341 || keyCode == 345) {
            this.speedupModifiers.remove(keyCode);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        this.finish();
    }

    @Override
    public void removed() {
        this.minecraft.getMusicManager().stopPlaying(POEM_MUSIC);
        PoAnimationController.onScreenClosed(this);
    }

    @Override
    public Music getBackgroundMusic() {
        return POEM_MUSIC;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillRenderType(RenderType.endPortal(), 0, 0, this.width, this.height, 0);
    }

    private float calculateScrollSpeed() {
        float progress = Mth.clamp(this.scrollingTicks / (float) SCROLL_DECELERATION_TICKS, 0.0F, 1.0F);
        float baseSpeed = Mth.lerp((float) Mth.smoothstep(progress), INITIAL_SCROLL_SPEED, BASE_SCROLL_SPEED);
        if (!this.speedupActive) {
            return baseSpeed * this.direction;
        }
        return baseSpeed * (5.0F + this.speedupModifiers.size() * 15.0F) * this.direction;
    }

    private float titleRotation(float partialTick) {
        float progress = Mth.clamp((this.playbackTicks + partialTick - TITLE_HOLD_TICKS) / TITLE_ROTATION_TICKS, 0.0F, 1.0F);
        return (float) Mth.smoothstep(progress) * 180.0F;
    }

    private float endScrollPosition() {
        if (this.lines.isEmpty()) {
            return this.height + TITLE_HEIGHT;
        }
        int lastLineY = this.height + 24 + (this.lines.size() - 1) * 12;
        return lastLineY + 20.0F;
    }

    private void renderVignette(GuiGraphics guiGraphics) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        guiGraphics.blit(VIGNETTE_LOCATION, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private void drawVanillaLine(GuiGraphics guiGraphics, FormattedCharSequence line, float startX, float y, int color) {
        Matrix4f matrix = guiGraphics.pose().last().pose();
        float alpha = (color >>> 24) / 255.0F;
        float defaultRed = (color >> 16 & 255) / 255.0F;
        float defaultGreen = (color >> 8 & 255) / 255.0F;
        float defaultBlue = (color & 255) / 255.0F;
        float[] x = {startX};

        line.accept((index, style, codePoint) -> {
            FontSet fontSet = this.font.getFontSet(style.getFont());
            boolean bold = style.isBold();
            BakedGlyph glyph = fontSet.getGlyph(codePoint);
            VertexConsumer consumer = guiGraphics.bufferSource().getBuffer(
                    glyph.renderType(Font.DisplayMode.NORMAL));
            float red = defaultRed;
            float green = defaultGreen;
            float blue = defaultBlue;
            if (style.getColor() != null) {
                int textColor = style.getColor().getValue();
                red = (textColor >> 16 & 255) / 255.0F;
                green = (textColor >> 8 & 255) / 255.0F;
                blue = (textColor & 255) / 255.0F;
            }
            glyph.render(style.isItalic(), x[0], y, matrix, consumer,
                    red, green, blue, alpha, LightTexture.FULL_BRIGHT);
            x[0] += fontSet.getGlyphInfo(codePoint, false).getAdvance(bold);
            return true;
        });
    }

    private void finish() {
        if (this.finished) return;

        this.finished = true;
        PoAnimationController.finish(this);
    }
}
