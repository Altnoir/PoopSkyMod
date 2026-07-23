package com.altnoir.poopsky.client.screen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.IntroController;
import com.altnoir.poopsky.impl.sound.PoSoundEvents;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class IntroScreen extends Screen {
    private static final ResourceLocation FONT = PoopSky.loc("poopsky_intro");
    private static final ResourceLocation POOP_TEXTURE = PoopSky.loc("textures/item/poop.png");
    private static final ResourceLocation SKY_TEXTURE = PoopSky.loc("textures/gui/poopsky_intro/depth-blue.png");

    private static final Component TITLE = titleText("poopsky");
    private static final Component YEAR = Component.literal("2026");

    private static final float VIRTUAL_WIDTH = 1920.0F;
    private static final float VIRTUAL_HEIGHT = 1080.0F;
    private static final float INTRO_DURATION = 17.0F;
    private static final float SETTLE_DURATION = 1.4F;
    private static final float STILL_DURATION = 0.45F;
    private static final float SHATTER_START = SETTLE_DURATION + STILL_DURATION;
    private static final float SHATTER_DURATION = 2.25F;
    private static final float WORLD_REVEAL_DURATION = 1.2F;
    private static final float WORLD_REVEAL_START = SHATTER_START + SHATTER_DURATION;
    private static final float LOADING_COMPLETE_HOLD_DURATION = WORLD_REVEAL_START + WORLD_REVEAL_DURATION;
    private static final float TICKS_PER_SECOND = 20.0F;
    private static final int INTRO_TICKS = Mth.ceil(INTRO_DURATION * TICKS_PER_SECOND);
    private static final int SHATTER_SOUND_TICK = Mth.ceil(SHATTER_START * TICKS_PER_SECOND);
    private static final int COMPLETION_TICKS = Mth.ceil(LOADING_COMPLETE_HOLD_DURATION * TICKS_PER_SECOND);
    private static final float TITLE_FADE_START = 4.0F;
    private static final float TITLE_FADE_END = 7.5F;
    private static final float TEXTURE_FADE_START = 8.0F;
    private static final float TEXTURE_FADE_END = 9.5F;
    private static final float ICON_FADE_START = 2.0F;
    private static final float ICON_FADE_END = 4.0F;
    private static final float REFLECTION_START = 9.5F;
    private static final float REFLECTION_DURATION = 3.2F;
    private static final float REFLECTION_SECOND_DELAY = 3.0F;
    private static final float REFLECTION_OFFSET_X = 26.0F;
    private static final float REFLECTION_OFFSET_Y = 30.0F;
    private static final float REFLECTION_RED = 0.34F;
    private static final float REFLECTION_GREEN = 0.52F;
    private static final float REFLECTION_BLUE = 0.66F;
    private static final float REFLECTION_ALPHA = 0.32F;
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
    private static final float ICON_OFFSET_X = -25.0F;
    private static final float ICON_TOP = 500.0F;
    private static final float YEAR_WIDTH = 188.0F;
    private static final float YEAR_X = (VIRTUAL_WIDTH - YEAR_WIDTH) * 0.5F;
    private static final float YEAR_TEXT_Y = 761.0F;
    private static final float LOADING_TEXT_Y = 946.0F;
    private static final float LOADING_TEXT_SCALE = 2.0F;

    private static final int TILE_SIZE = 1024;
    private static final float TILE_START_X = 521.0F;
    private static final float TILE_START_Y = 790.0F;
    private static final float TILE_SPEED_X = -46.0F;
    private static final float TILE_SPEED_Y = -100.0F;

    private int playbackTicks;
    private int completionTicks = -1;
    private SoundInstance sound;
    private TitleLayout titleLayout;
    private PoopScatter[] poopScatter;
    private int maskLeft;
    private int maskTop;
    private int maskRight;
    private int maskBottom;
    private int loadingProgress = -1;
    private Component loadingText = Component.empty();
    private float loadingTextX;
    private boolean loadingStarted;
    private boolean restartSoundNextTick;
    private boolean soundWasActive;

    public IntroScreen() {
        super(Component.empty());
    }

    private void startPlayback() {
        if (this.sound != null) return;

        this.minecraft.getMusicManager().stopPlaying();
        this.sound = new IntroSoundInstance(PoSoundEvents.POOPSKY_INTRO.get(), 0.0F);
        this.minecraft.getSoundManager().play(this.sound);
        KeyMapping.releaseAll();
    }

    public void abort() {
        this.stopPlayback();
    }

    public void resumePlaybackSound() {
        if (this.canPlayIntroSound() && !this.minecraft.getSoundManager().isActive(this.sound)) {
            this.restartSoundNextTick = true;
        }
    }

    @Override
    protected void init() {
        if (this.titleLayout == null) {
            this.titleLayout = createTitleLayout(this.font);
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
            if (this.completionTicks == SHATTER_SOUND_TICK) {
                this.minecraft.getSoundManager().play(
                        SimpleSoundInstance.forUI(PoSoundEvents.FART.get(), 0.58F, 1.35F));
                this.minecraft.getSoundManager().play(
                        SimpleSoundInstance.forUI(PoSoundEvents.FART.get(), 1.05F, 0.8F));
            }
        }

        this.updatePlaybackSound();

        if (this.completionTicks < 0 && this.playbackTicks >= INTRO_TICKS) {
            this.loadingStarted = true;
            if (IntroController.isReadyToFinish()) {
                this.completionTicks = 0;
            }
        }

        if (this.completionTicks >= COMPLETION_TICKS) {
            this.minecraft.setScreen(null);
        }
    }

    private void updatePlaybackSound() {
        if (this.restartSoundNextTick) {
            this.restartSoundNextTick = false;
            this.restartPlaybackSound();
        }

        if (!this.canPlayIntroSound()) {
            return;
        }

        boolean active = this.minecraft.getSoundManager().isActive(this.sound);

        if (active) {
            this.soundWasActive = true;
        } else if (this.soundWasActive) {
            this.soundWasActive = false;
            this.restartSoundNextTick = true;
        }
    }

    private boolean canPlayIntroSound() {
        return this.sound != null && this.playbackTicks < INTRO_TICKS;
    }

    private void restartPlaybackSound() {
        if (!this.canPlayIntroSound()) {
            return;
        }

        this.minecraft.getSoundManager().stop(this.sound);

        this.sound = new IntroSoundInstance(PoSoundEvents.POOPSKY_INTRO.get(),
                this.playbackTicks / TICKS_PER_SECOND);

        this.minecraft.getSoundManager().play(this.sound);
        this.soundWasActive = false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float elapsed = this.elapsedSeconds(partialTick);
        float completion = this.completionSeconds(partialTick);
        if (completion >= WORLD_REVEAL_START) {
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

        if (completion >= SHATTER_START) {
            this.drawPoopScatter(guiGraphics, shatterTime);
        } else {
            float textureTime = deceleratedTextureTime(elapsed, completion);
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
            if (this.loadingStarted) {
                this.drawLoadingText(guiGraphics, completion);
            }
        }
        if (yearAlpha > MIN_FONT_ALPHA) {
            this.drawYear(guiGraphics, yearAlpha);
        }

        guiGraphics.pose().popPose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
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
        this.drawScaledText(guiGraphics, TITLE, TITLE_LEFT, TITLE_TEXT_Y, this.titleLayout.scale(), color);
    }

    private void drawPoopIcon(GuiGraphics guiGraphics, float alpha) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(POOP_TEXTURE, Mth.floor(this.titleLayout.iconX()), Mth.floor(ICON_TOP),
                Mth.floor(ICON_SIZE), Mth.floor(ICON_SIZE),
                0.0F, 0.0F, 16, 16, 16, 16);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawYear(GuiGraphics guiGraphics, float alpha) {
        this.drawScaledText(guiGraphics, YEAR, YEAR_X, YEAR_TEXT_Y,
                this.titleLayout.yearScale(), alphaColor(alpha));
    }

    private void drawLoadingText(GuiGraphics guiGraphics, float completion) {
        int progress = completion >= 0.0F ? 100 : IntroController.getLoadingProgress();
        if (progress != this.loadingProgress) {
            this.loadingProgress = progress;
            this.loadingText = Component.literal("Loading... " + progress + "%");
            this.loadingTextX = (VIRTUAL_WIDTH - this.font.width(this.loadingText) * LOADING_TEXT_SCALE) * 0.5F;
        }
        this.drawScaledText(guiGraphics, this.loadingText, this.loadingTextX, LOADING_TEXT_Y,
                LOADING_TEXT_SCALE, 0xFFFFFFFF);
    }

    private void drawMaskedTitle(GuiGraphics guiGraphics, float textureTime, float titleAlpha, float textureAlpha) {
        this.beginDepthMask(guiGraphics);
        try {
            this.drawTitleGlyphs(guiGraphics, alphaColor(titleAlpha));
            guiGraphics.flush();
            this.beginDepthMaskedDrawing();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.drawTextureTiles(guiGraphics, textureTime, 1.0F, 1.0F, 1.0F, textureAlpha);
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
        this.beginDepthMask(guiGraphics);
        try {
            this.drawReflectionGlyphs(guiGraphics, horizontalOffset, -verticalOffset, 0xFF000000);
            this.drawReflectionGlyphs(guiGraphics, -horizontalOffset, verticalOffset, 0xFF000000);
            guiGraphics.flush();
            this.beginDepthMaskedDrawing();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.drawTextureTiles(guiGraphics, textureTime, REFLECTION_RED, REFLECTION_GREEN, REFLECTION_BLUE,
                    textureAlpha * REFLECTION_ALPHA * Mth.sin(linearProgress * Mth.PI));
        } finally {
            this.endDepthMask(guiGraphics);
        }
    }

    private void drawPoopScatter(GuiGraphics guiGraphics, float shatterTime) {
        guiGraphics.flush();
        Matrix4f matrix = guiGraphics.pose().last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, POOP_TEXTURE);

        BufferBuilder builder = RenderSystem.renderThreadTesselator()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float gravity = 190.0F * shatterTime * shatterTime;
        for (PoopScatter poop : this.poopScatter) {
            float centerX = poop.x() + poop.velocityX() * shatterTime;
            float centerY = poop.y() + poop.velocityY() * shatterTime + gravity;
            float halfSize = poop.halfSize();
            if (centerX + halfSize < 0.0F || centerX - halfSize > VIRTUAL_WIDTH
                    || centerY + halfSize < 0.0F || centerY - halfSize > VIRTUAL_HEIGHT) {
                continue;
            }
            addPoopQuad(builder, matrix, centerX, centerY, halfSize, poop.spin() * shatterTime);
        }

        MeshData mesh = builder.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
        RenderSystem.disableBlend();
    }

    private static void addPoopQuad(BufferBuilder builder, Matrix4f matrix, float centerX, float centerY,
                                    float halfSize, float angle) {
        float downY = halfSize * Mth.cos(angle);
        float rightY = halfSize * Mth.sin(angle);
        float downX = -rightY;
        builder.addVertex(matrix, centerX - downY - downX, centerY - rightY - downY, 0.0F)
                .setUv(0.0F, 0.0F);
        builder.addVertex(matrix, centerX - downY + downX, centerY - rightY + downY, 0.0F)
                .setUv(0.0F, 1.0F);
        builder.addVertex(matrix, centerX + downY + downX, centerY + rightY + downY, 0.0F)
                .setUv(1.0F, 1.0F);
        builder.addVertex(matrix, centerX + downY - downX, centerY + rightY - downY, 0.0F)
                .setUv(1.0F, 0.0F);
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
        guiGraphics.drawString(this.font, text, 0, 0, color, false);
        guiGraphics.pose().popPose();
    }

    private static TitleLayout createTitleLayout(Font font) {
        float scale = (TITLE_RIGHT - TITLE_LEFT) / font.width(TITLE);
        float height = font.lineHeight * scale;
        float secondPLeft = TITLE_LEFT + font.width(titleText("poo")) * scale;
        float secondPWidth = font.width(titleText("p")) * scale;
        float iconX = secondPLeft + (secondPWidth - ICON_SIZE) * 0.5F + ICON_OFFSET_X;
        float yearScale = YEAR_WIDTH / font.width(YEAR);
        return new TitleLayout(scale, height, iconX, yearScale);
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
        this.restartSoundNextTick = false;
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

    private record TitleLayout(float scale, float height, float iconX, float yearScale) {
    }

    private record PoopScatter(float x, float y, float velocityX, float velocityY, float halfSize, float spin) {
    }

    private static final class IntroSoundInstance extends SimpleSoundInstance {
        private static final int SKIP_BUFFER_SIZE = 16_384;
        private final float startSeconds;

        private IntroSoundInstance(SoundEvent soundEvent, float startSeconds) {
            super(soundEvent.getLocation(), SoundSource.MUSIC, 1.0F, 1.0F, SoundInstance.createUnseededRandom(),
                    false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
            this.startSeconds = startSeconds;
        }

        @Override
        public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
            return soundBuffers.getStream(sound.getPath(), looping)
                    .thenApply(stream -> skipTo(stream, this.startSeconds));
        }

        private static AudioStream skipTo(AudioStream stream, float seconds) {
            AudioFormat format = stream.getFormat();
            long bytesToSkip = (long) (format.getFrameRate() * format.getFrameSize() * seconds);

            try {
                while (bytesToSkip > 0L) {
                    ByteBuffer skipped = stream.read((int) Math.min(SKIP_BUFFER_SIZE, bytesToSkip));
                    if (!skipped.hasRemaining()) break;
                    bytesToSkip -= skipped.remaining();
                }
                return stream;
            } catch (IOException exception) {
                try {
                    stream.close();
                } catch (IOException suppressed) {
                    exception.addSuppressed(suppressed);
                }
                throw new CompletionException(exception);
            }
        }
    }
}
