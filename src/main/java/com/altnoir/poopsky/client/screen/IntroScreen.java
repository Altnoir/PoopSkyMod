package com.altnoir.poopsky.client.screen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.IntroController;
import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.init.PoSoundEvents;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class IntroScreen extends Screen {
    private static final ResourceLocation FONT = PoopSky.loc("poopsky_intro");
    private static final ResourceLocation POOP_TEXTURE = PoopSky.loc("textures/item/shit.png");
    private static final ResourceLocation SKY_TEXTURE = PoopSky.loc("textures/gui/poopsky_intro/depth_blue.png");

    private static final float VIRTUAL_WIDTH = 1920.0F;
    private static final float VIRTUAL_HEIGHT = 1080.0F;
    private static final float INTRO_DURATION = 17.0F;
    private static final float SETTLE_DURATION = 1.4F;
    private static final float STILL_DURATION = 0.45F;
    private static final float SHATTER_START = SETTLE_DURATION + STILL_DURATION;
    private static final float SHATTER_FADE_DURATION = 0.12F;
    private static final float SHATTER_DURATION = 2.25F;
    private static final float WORLD_REVEAL_DURATION = 1.2F;
    private static final float WORLD_REVEAL_START = SHATTER_START + SHATTER_DURATION;
    private static final float COMPLETION_DURATION = WORLD_REVEAL_START + WORLD_REVEAL_DURATION;
    private static final float TICKS_PER_SECOND = 20.0F;
    private static final int INTRO_TICKS = Mth.ceil(INTRO_DURATION * TICKS_PER_SECOND);
    private static final int SHATTER_SOUND_TICK = Mth.ceil(SHATTER_START * TICKS_PER_SECOND);
    private static final int COMPLETION_TICKS = Mth.ceil(COMPLETION_DURATION * TICKS_PER_SECOND);
    private static final float TITLE_FADE_START = 4.0F;
    private static final float TITLE_FADE_END = 8.0F;
    private static final float TEXTURE_FADE_START = 7.6F;
    private static final float TEXTURE_FADE_END = 9.4F;
    private static final float ICON_FADE_START = 2.0F;
    private static final float ICON_FADE_END = 4.5F;
    private static final float REFLECTION_START = 9.5F;
    private static final float REFLECTION_DURATION = 3.2F;
    private static final float REFLECTION_SECOND_DELAY = 3.0F;
    private static final float REFLECTION_OFFSET_X = 26.0F;
    private static final float REFLECTION_OFFSET_Y = 30.0F;
    private static final float REFLECTION_ALPHA = 0.55F;
    private static final int POOP_COLUMNS = 36;
    private static final int POOP_ROWS = 10;
    private static final float MIN_FONT_ALPHA = 4.0F / 255.0F;
    private static final float MIN_TEXTURE_ALPHA = 1.0F / 255.0F;
    private static final int DEPTH_BUFFER_BIT = 0x00000100;
    private static final int DEPTH_EQUAL = 0x0202;
    private static final int DEPTH_LEQUAL = 0x0203;

    private static final float TITLE_LEFT = 494.0F;
    private static final float TITLE_TEXT_Y = 460.0F;
    private static final float TITLE_RIGHT = 1448.0F;
    private static final float ICON_SIZE = 88.0F;
    private static final float MODERN_UI_ICON_OFFSET_X = 8.0F;
    private static final float MODERN_UI_ICON_OFFSET_Y = 60.0F;
    private static final float ICON_TOP = 500.0F;
    private static final float YEAR_WIDTH = 188.0F;
    private static final float YEAR_X = (VIRTUAL_WIDTH - YEAR_WIDTH) * 0.5F;
    private static final float YEAR_TEXT_Y = 761.0F;

    private static final int TILE_SIZE = 1024;
    private static final float TILE_START_X = 521.0F;
    private static final float TILE_START_Y = 790.0F;
    private static final float TILE_SPEED_X = -46.0F;
    private static final float TILE_SPEED_Y = -100.0F;

    private int playbackTicks;
    private final Component title;
    private final Component year;
    private int completionTicks = -1;
    private SoundInstance sound;
    private TitleLayout titleLayout;
    private PoopScatter[] poopScatter;
    private int maskLeft;
    private int maskTop;
    private int maskRight;
    private int maskBottom;
    private int completionSoundStage;

    public IntroScreen(String title, String year) {
        super(Component.empty());
        this.title = titleText(title == null || title.isBlank() ? "poopsky" : title);
        this.year = titleText(year == null || year.isBlank() ? "2026" : year);
    }

    private void startPlayback() {
        if (this.sound != null) return;

        this.minecraft.getMusicManager().stopPlaying();
        this.playIntroSound();
        KeyMapping.releaseAll();
    }

    private void playIntroSound() {
        this.sound = SimpleSoundInstance.forMusic(PoSoundEvents.POOPSKY_INTRO.get());
        this.minecraft.getSoundManager().play(this.sound);
    }

    public void abort() {
        this.stopPlayback();
    }

    @Override
    protected void init() {
        if (this.titleLayout == null) {
            this.titleLayout = createTitleLayout(this.font, this.title, this.year);
            this.poopScatter = createPoopScatter(this.titleLayout);
        }
        float scale = Math.min(this.width / VIRTUAL_WIDTH, this.height / VIRTUAL_HEIGHT);
        float offsetX = (this.width - VIRTUAL_WIDTH * scale) * 0.5F;
        float offsetY = (this.height - VIRTUAL_HEIGHT * scale) * 0.5F;
        this.maskLeft = Mth.floor(offsetX + (TITLE_LEFT - REFLECTION_OFFSET_X) * scale) - 1;
        this.maskTop = Mth.floor(offsetY + (TITLE_TEXT_Y - REFLECTION_OFFSET_Y) * scale) - 1;
        this.maskRight = Mth.ceil(offsetX + (TITLE_RIGHT + REFLECTION_OFFSET_X) * scale) + 1;
        this.maskBottom = Mth.ceil(offsetY + (TITLE_TEXT_Y + this.titleLayout.height()
                + REFLECTION_OFFSET_Y) * scale) + 1;
        this.minecraft.getTextureManager().getTexture(POOP_TEXTURE);
        this.minecraft.getTextureManager().getTexture(SKY_TEXTURE);
        GLFW.glfwSetInputMode(this.minecraft.getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
        this.startPlayback();
    }

    @Override
    public void tick() {
        this.playbackTicks++;
        if (this.completionTicks >= 0) {
            this.completionTicks++;
            this.updateCompletionSounds();
        }

        if (this.completionTicks < 0 && this.playbackTicks >= INTRO_TICKS) {
            this.completionTicks = 0;
        }

        if (this.completionTicks >= COMPLETION_TICKS) {
            IntroController.finish(this);
        }
    }

    private final RandomSource random = RandomSource.create();
    private int nextExplosionTick = SHATTER_SOUND_TICK;

    private void updateCompletionSounds() {
        if (this.completionSoundStage >= 6 || this.completionTicks < this.nextExplosionTick) {
            return;
        }
        float pitch = 0.78F + this.random.nextFloat() * 0.35F;
        float volume = 0.85F + this.random.nextFloat() * 0.2F;

        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(PoSoundEvents.FART.value(), pitch, volume));

        this.completionSoundStage++;
        this.nextExplosionTick += 3;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float elapsed = this.elapsedSeconds(partialTick);
        float completion = this.completionSeconds(partialTick);
        if (completion >= WORLD_REVEAL_START) {
            if (this.minecraft.level == null) {
                guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);
                return;
            }
            float blackAlpha = 1.0F - smooth(
                    completion, WORLD_REVEAL_START, WORLD_REVEAL_START + WORLD_REVEAL_DURATION);
            guiGraphics.fill(0, 0, this.width, this.height, alphaColor(blackAlpha) & 0xFF000000);
            return;
        }

        guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);

        float canvasScale = Math.min(this.width / VIRTUAL_WIDTH, this.height / VIRTUAL_HEIGHT);
        float canvasX = (this.width - VIRTUAL_WIDTH * canvasScale) * 0.5F;
        float canvasY = (this.height - VIRTUAL_HEIGHT * canvasScale) * 0.5F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(canvasX, canvasY, 0.0F);
        guiGraphics.pose().scale(canvasScale, canvasScale, 1.0F);
        float shatterTime = completion - SHATTER_START;
        if (shatterTime >= 0.0F) {
            float shake = 28.0F * (1.0F - smooth(shatterTime, 0.0F, 0.55F));
            guiGraphics.pose().translate(
                    Mth.sin(shatterTime * 54.0F) * shake,
                    Mth.cos(shatterTime * 43.0F) * shake * 0.68F,
                    0.0F);
        }

        float yearAlpha = smooth(elapsed, 9.5F, 12.0F)
                * (1.0F - smooth(completion, SHATTER_START, SHATTER_START + 0.3F));
        float textureTime = deceleratedTextureTime(elapsed, completion);

        if (completion >= SHATTER_START) {
            float scatterAlpha = smooth(shatterTime, 0.0F, SHATTER_FADE_DURATION);
            float remainingTitleAlpha = 1.0F - scatterAlpha;
            if (remainingTitleAlpha > MIN_TEXTURE_ALPHA) {
                this.drawMaskedTitle(guiGraphics, textureTime, remainingTitleAlpha, remainingTitleAlpha);
            }
            if (scatterAlpha > MIN_TEXTURE_ALPHA) {
                this.drawPoopScatter(guiGraphics, shatterTime, scatterAlpha);
            }
        } else {
            float textureAlpha = smooth(elapsed, TEXTURE_FADE_START, TEXTURE_FADE_END);
            if (textureAlpha > MIN_TEXTURE_ALPHA) {
                this.drawTitleReflections(guiGraphics, elapsed, textureTime, textureAlpha);
            }
            float titleAlpha = smooth(elapsed, TITLE_FADE_START, TITLE_FADE_END);
            if (titleAlpha > MIN_FONT_ALPHA) {
                if (textureAlpha > MIN_TEXTURE_ALPHA) {
                    this.drawMaskedTitle(guiGraphics, textureTime, titleAlpha, textureAlpha);
                } else {
                    this.drawTitleGlyphs(guiGraphics, alphaColor(titleAlpha));
                }
            }
            float glow = smooth(completion, SHATTER_START - 0.32F, SHATTER_START - 0.06F);
            if (glow > MIN_FONT_ALPHA) {
                this.drawTitleGlyphs(guiGraphics, alphaColor(glow * 0.42F));
            }
            float iconAlpha = smooth(elapsed, ICON_FADE_START, ICON_FADE_END);
            if (iconAlpha > MIN_TEXTURE_ALPHA) {
                this.drawPoopIcon(guiGraphics, iconAlpha);
            }
        }
        if (yearAlpha > MIN_FONT_ALPHA) {
            this.drawYear(guiGraphics, yearAlpha);
        }

        guiGraphics.pose().popPose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            IntroController.finish(this);
        }
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        GLFW.glfwSetInputMode(this.minecraft.getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        this.stopPlayback();
        IntroController.onScreenClosed(this);
    }

    private void drawTitleGlyphs(GuiGraphics guiGraphics, int color) {
        this.drawScaledText(guiGraphics, this.title, TITLE_LEFT, TITLE_TEXT_Y, this.titleLayout.scale(), color);
    }

    private void drawPoopIcon(GuiGraphics guiGraphics, float alpha) {
        guiGraphics.flush();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, POOP_TEXTURE);

        float left = this.titleLayout.iconX();
        float top = this.titleLayout.iconY();
        float right = left + ICON_SIZE;
        float bottom = top + ICON_SIZE;
        int color = FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F);
        Matrix4f matrix = guiGraphics.pose().last().pose();
        BufferBuilder builder = RenderSystem.renderThreadTesselator()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.addVertex(matrix, left, top, 0.0F).setUv(0.0F, 0.0F).setColor(color);
        builder.addVertex(matrix, left, bottom, 0.0F).setUv(0.0F, 1.0F).setColor(color);
        builder.addVertex(matrix, right, bottom, 0.0F).setUv(1.0F, 1.0F).setColor(color);
        builder.addVertex(matrix, right, top, 0.0F).setUv(1.0F, 0.0F).setColor(color);
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }

    private void drawYear(GuiGraphics guiGraphics, float alpha) {
        this.drawScaledText(guiGraphics, this.year, YEAR_X, YEAR_TEXT_Y,
                this.titleLayout.yearScale(), alphaColor(alpha));
    }

    private void drawMaskedTitle(GuiGraphics guiGraphics, float textureTime, float titleAlpha, float textureAlpha) {
        this.drawMaskedTexture(guiGraphics,
                () -> this.drawTitleGlyphs(guiGraphics, alphaColor(titleAlpha)),
                textureTime, 1.0F, 1.0F, 1.0F, textureAlpha);
    }

    private void drawMaskedTexture(GuiGraphics guiGraphics, Runnable drawMask, float textureTime,
                                   float red, float green, float blue, float alpha) {
        this.beginDepthMask(guiGraphics);
        try {
            drawMask.run();
            guiGraphics.flush();
            this.beginDepthMaskedDrawing();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.drawTextureTiles(guiGraphics, textureTime, red, green, blue, alpha);
        } finally {
            this.endDepthMask(guiGraphics);
        }
    }

    private void beginDepthMask(GuiGraphics guiGraphics) {
        guiGraphics.flush();
        guiGraphics.enableScissor(this.maskLeft, this.maskTop, this.maskRight, this.maskBottom);
        RenderSystem.depthMask(true);
        RenderSystem.clearDepth(1.0D);
        RenderSystem.clear(DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
    }

    private void beginDepthMaskedDrawing() {
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(DEPTH_EQUAL);
    }

    private void endDepthMask(GuiGraphics guiGraphics) {
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(DEPTH_LEQUAL);
        RenderSystem.defaultBlendFunc();
        guiGraphics.disableScissor();
    }

    private void drawTitleReflections(GuiGraphics guiGraphics, float elapsed, float textureTime, float textureAlpha) {
        if (elapsed <= REFLECTION_START) return;

        float reflectionTime = elapsed - REFLECTION_START;
        this.drawReflectionPair(guiGraphics, textureTime, textureAlpha, -1.0F, reflectionTime);
        this.drawReflectionPair(guiGraphics, textureTime, textureAlpha, 1.0F,
                reflectionTime - REFLECTION_SECOND_DELAY);
    }

    private void drawReflectionPair(GuiGraphics guiGraphics, float textureTime, float textureAlpha,
                                    float direction, float reflectionTime) {
        if (reflectionTime <= 0.0F || reflectionTime >= REFLECTION_DURATION) return;

        float linearProgress = reflectionTime / REFLECTION_DURATION;
        float progress = (float) Mth.smoothstep(linearProgress);
        float horizontalOffset = direction * REFLECTION_OFFSET_X * progress;
        float verticalOffset = REFLECTION_OFFSET_Y * progress;
        this.drawMaskedTexture(guiGraphics, () -> {
                    this.drawReflectionGlyphs(guiGraphics, horizontalOffset, -verticalOffset, 0xFF000000);
                    this.drawReflectionGlyphs(guiGraphics, -horizontalOffset, verticalOffset, 0xFF000000);
                }, textureTime, 0.34F, 0.52F, 0.66F,
                textureAlpha * REFLECTION_ALPHA * Mth.sin(linearProgress * Mth.PI));
    }

    private void drawPoopScatter(GuiGraphics guiGraphics, float shatterTime, float alpha) {
        guiGraphics.flush();
        Matrix4f matrix = guiGraphics.pose().last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, POOP_TEXTURE);

        BufferBuilder builder = RenderSystem.renderThreadTesselator()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        int color = FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F);
        float gravity = 190.0F * shatterTime * shatterTime;
        for (PoopScatter poop : this.poopScatter) {
            float centerX = poop.x() + poop.velocityX() * shatterTime;
            float centerY = poop.y() + poop.velocityY() * shatterTime + gravity;
            float halfSize = poop.halfSize();
            if (centerX + halfSize < 0.0F || centerX - halfSize > VIRTUAL_WIDTH
                    || centerY + halfSize < 0.0F || centerY - halfSize > VIRTUAL_HEIGHT) {
                continue;
            }
            addPoopQuad(builder, matrix, centerX, centerY, halfSize, poop.spin() * shatterTime, color);
        }

        MeshData mesh = builder.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
        RenderSystem.disableBlend();
    }

    private static void addPoopQuad(BufferBuilder builder, Matrix4f matrix, float centerX, float centerY,
                                    float halfSize, float angle, int color) {
        float downY = halfSize * Mth.cos(angle);
        float rightY = halfSize * Mth.sin(angle);
        float downX = -rightY;
        builder.addVertex(matrix, centerX - downY - downX, centerY - rightY - downY, 0.0F)
                .setUv(0.0F, 0.0F).setColor(color);
        builder.addVertex(matrix, centerX - downY + downX, centerY - rightY + downY, 0.0F)
                .setUv(0.0F, 1.0F).setColor(color);
        builder.addVertex(matrix, centerX + downY + downX, centerY + rightY + downY, 0.0F)
                .setUv(1.0F, 1.0F).setColor(color);
        builder.addVertex(matrix, centerX + downY - downX, centerY + rightY - downY, 0.0F)
                .setUv(1.0F, 0.0F).setColor(color);
    }

    private void drawReflectionGlyphs(GuiGraphics guiGraphics, float offsetX, float offsetY, int color) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(offsetX, offsetY, 0.0F);
        this.drawTitleGlyphs(guiGraphics, color);
        guiGraphics.pose().popPose();
    }

    private void drawTextureTiles(GuiGraphics guiGraphics, float elapsed,
                                  float red, float green, float blue, float alpha) {
        float scrollTime = elapsed - 7.0F;
        int startX = Mth.floor(Mth.positiveModulo(TILE_START_X + TILE_SPEED_X * scrollTime, TILE_SIZE));
        int startY = Mth.floor(Mth.positiveModulo(TILE_START_Y + TILE_SPEED_Y * scrollTime, TILE_SIZE));
        float minX = TITLE_LEFT - REFLECTION_OFFSET_X;
        float minY = TITLE_TEXT_Y - REFLECTION_OFFSET_Y;
        float maxX = TITLE_RIGHT + REFLECTION_OFFSET_X;
        float maxY = TITLE_TEXT_Y + this.titleLayout.height() + REFLECTION_OFFSET_Y;

        while (startX > minX) startX -= TILE_SIZE;
        while (startY > minY) startY -= TILE_SIZE;

        Matrix4f matrix = guiGraphics.pose().last().pose();
        int color = FastColor.ARGB32.colorFromFloat(alpha, red, green, blue);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, SKY_TEXTURE);
        BufferBuilder builder = RenderSystem.renderThreadTesselator()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (int x = startX; x < maxX; x += TILE_SIZE) {
            for (int y = startY; y < maxY; y += TILE_SIZE) {
                builder.addVertex(matrix, x, y, 0.0F).setUv(0.0F, 0.0F).setColor(color);
                builder.addVertex(matrix, x, y + TILE_SIZE, 0.0F).setUv(0.0F, 1.0F).setColor(color);
                builder.addVertex(matrix, x + TILE_SIZE, y + TILE_SIZE, 0.0F).setUv(1.0F, 1.0F).setColor(color);
                builder.addVertex(matrix, x + TILE_SIZE, y, 0.0F).setUv(1.0F, 0.0F).setColor(color);
            }
        }
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }

    private void drawScaledText(GuiGraphics guiGraphics, Component text, float x, float y, float scale, int color) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        this.drawVanillaText(guiGraphics, text.getString(), color);
        guiGraphics.pose().popPose();
    }

    private void drawVanillaText(GuiGraphics guiGraphics, String text, int color) {
        FontSet fontSet = this.font.getFontSet(FONT);
        Matrix4f matrix = guiGraphics.pose().last().pose();
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float alpha = (color >>> 24) / 255.0F;
        float x = 0.0F;

        for (int offset = 0; offset < text.length(); ) {
            int codePoint = text.codePointAt(offset);
            GlyphInfo glyphInfo = fontSet.getGlyphInfo(codePoint, false);
            BakedGlyph glyph = fontSet.getGlyph(codePoint);
            VertexConsumer consumer = guiGraphics.bufferSource().getBuffer(glyph.renderType(Font.DisplayMode.NORMAL));
            glyph.render(false, x, 0.0F, matrix, consumer, red, green, blue, alpha, LightTexture.FULL_BRIGHT);
            x += glyphInfo.getAdvance(false);
            offset += Character.charCount(codePoint);
        }
    }

    private static TitleLayout createTitleLayout(Font font, Component title, Component year) {
        float titleWidth = Math.max(measureVanillaText(font, title.getString()), 1.0F);
        float scale = (TITLE_RIGHT - TITLE_LEFT) / titleWidth;
        float height = font.lineHeight * scale;
        float iconX;
        float iconY = ICON_TOP;
        if (title.getString().equals("poopsky")) {
            FontSet fontSet = font.getFontSet(FONT);
            BakedGlyph secondP = fontSet.getGlyph('p');
            float secondPCenter = measureVanillaText(font, "poo") + (secondP.left + secondP.right) * 0.5F;
            iconX = TITLE_LEFT + secondPCenter * scale - ICON_SIZE * 0.5F;
            if (PoMods.MODERNUI.isLoaded()) {
                iconX += MODERN_UI_ICON_OFFSET_X;
                iconY += MODERN_UI_ICON_OFFSET_Y;
            }
        } else {
            iconX = (VIRTUAL_WIDTH - ICON_SIZE) * 0.5F;
        }
        float yearScale = YEAR_WIDTH / Math.max(measureVanillaText(font, year.getString()), 1.0F);
        return new TitleLayout(scale, height, iconX, iconY, yearScale);
    }

    private static float measureVanillaText(Font font, String text) {
        FontSet fontSet = font.getFontSet(FONT);
        float width = 0.0F;
        for (int offset = 0; offset < text.length(); ) {
            int codePoint = text.codePointAt(offset);
            width += fontSet.getGlyphInfo(codePoint, false).getAdvance(false);
            offset += Character.charCount(codePoint);
        }
        return width;
    }

    private static PoopScatter[] createPoopScatter(TitleLayout layout) {
        PoopScatter[] poop = new PoopScatter[POOP_COLUMNS * POOP_ROWS];
        RandomSource random = RandomSource.create(0x44454C544152554EL);
        float width = TITLE_RIGHT - TITLE_LEFT;
        float height = layout.height();
        float cellWidth = width / POOP_COLUMNS;
        float cellHeight = height / POOP_ROWS;
        int index = 0;

        for (int row = 0; row < POOP_ROWS; row++) {
            for (int column = 0; column < POOP_COLUMNS; column++) {
                float x = TITLE_LEFT + (column + 0.5F) * cellWidth
                        + Mth.lerp(random.nextFloat(), -cellWidth * 0.32F, cellWidth * 0.32F);
                float y = TITLE_TEXT_Y + (row + 0.5F) * cellHeight
                        + Mth.lerp(random.nextFloat(), -cellHeight * 0.3F, cellHeight * 0.3F);
                float outwardVelocity = (x - (TITLE_LEFT + TITLE_RIGHT) * 0.5F) * 0.62F;
                float velocityX = outwardVelocity + Mth.lerp(random.nextFloat(), -92.0F, 92.0F);
                float velocityY = Mth.lerp(random.nextFloat(), -105.0F, 138.0F);
                float sizeRandom = random.nextFloat();
                float halfSize = 5.0F + sizeRandom * sizeRandom * 20.0F;
                float spin = Mth.lerp(random.nextFloat(), -5.2F, 5.2F);
                poop[index++] = new PoopScatter(x, y, velocityX, velocityY, halfSize, spin);
            }
        }
        return poop;
    }

    private static Component titleText(String text) {
        return Component.literal(text).withStyle(style -> style.withFont(FONT));
    }

    private void stopPlayback() {
        if (this.sound != null) {
            this.minecraft.getSoundManager().stop(this.sound);
            this.sound = null;
        }
    }

    private float elapsedSeconds(float partialTick) {
        return (this.playbackTicks + partialTick) / TICKS_PER_SECOND;
    }

    private float completionSeconds(float partialTick) {
        return (this.completionTicks + partialTick) / TICKS_PER_SECOND;
    }

    private static float deceleratedTextureTime(float elapsed, float completion) {
        if (completion < 0.0F) return elapsed;

        float movingTime = Math.min(completion, SETTLE_DURATION);
        float progress = movingTime / SETTLE_DURATION;
        float traveled = movingTime
                - movingTime * progress * progress
                + movingTime * progress * progress * progress * 0.5F;
        return elapsed - completion + traveled;
    }

    private static float smooth(float time, float start, float end) {
        float progress = Mth.clamp((time - start) / (end - start), 0.0F, 1.0F);
        return (float) Mth.smoothstep(progress);
    }

    private static int alphaColor(float alpha) {
        return Mth.floor(Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F) << 24 | 0xFFFFFF;
    }

    private record TitleLayout(float scale, float height, float iconX, float iconY, float yearScale) {
    }

    private record PoopScatter(float x, float y, float velocityX, float velocityY, float halfSize, float spin) {
    }
}