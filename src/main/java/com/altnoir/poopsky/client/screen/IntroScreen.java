package com.altnoir.poopsky.client.screen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.IntroController;
import com.altnoir.poopsky.impl.sound.PoSoundEvents;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
    private static final Component LOADING_TEXT = Component.literal("Loading...");

    private static final float VIRTUAL_WIDTH = 1920.0F;
    private static final float VIRTUAL_HEIGHT = 1080.0F;
    private static final float INTRO_DURATION = 17.0F;
    private static final float FADE_TO_BLACK_DURATION = 1.0F;
    private static final float BLACK_REVEAL_DURATION = 1.0F;
    private static final float LOADING_COMPLETE_HOLD_DURATION = 1.0F;
    private static final float TICKS_PER_SECOND = 20.0F;
    private static final float TITLE_FADE_START = 4.0F;
    private static final float TITLE_FADE_END = 7.5F;
    private static final float TEXTURE_FADE_START = 8.3F;
    private static final float TEXTURE_FADE_END = 9.8F;
    private static final float ICON_FADE_START = 2.0F;
    private static final float ICON_FADE_END = 4.0F;
    private static final float REFLECTION_START = 9.5F;
    private static final float REFLECTION_DURATION = 3.2F;
    private static final float REFLECTION_SIDE_DELAY = 3.0F;
    private static final float REFLECTION_LOADING_CYCLE = 6.9F;
    private static final float REFLECTION_OFFSET_X = 14.0F;
    private static final float REFLECTION_OFFSET_Y = 19.0F;
    private static final float REFLECTION_ALPHA = 0.32F;
    private static final float MIN_FONT_ALPHA = 4.0F / 255.0F;
    private static final float MIN_TEXTURE_ALPHA = 1.0F / 255.0F;

    private static final float TITLE_LEFT = 494.0F;
    private static final float TITLE_TOP = 438.0F;
    private static final float TITLE_TEXT_Y = 460.0F;
    private static final float TITLE_RIGHT = 1448.0F;
    private static final float TITLE_BOTTOM = 668.0F;
    private static final float ICON_SIZE = 88.0F;
    private static final float ICON_OFFSET_X = -25.0F;
    private static final float ICON_TOP = 500.0F;
    private static final float YEAR_WIDTH = 188.0F;
    private static final float YEAR_TEXT_Y = 761.0F;
    private static final float LOADING_TEXT_Y = 946.0F;
    private static final float LOADING_TEXT_SCALE = 2.0F;

    private static final int TILE_SIZE = 1024;
    private static final float TILE_START_X = 521.0F;
    private static final float TILE_START_Y = 790.0F;
    private static final float TILE_SPEED_X = -46.0F;
    private static final float TILE_SPEED_Y = -100.0F;

    private int playbackTicks;
    private int transitionTicks = -1;
    private int completionTicks = -1;
    private SoundInstance sound;
    private TextureTarget titleMaskTarget;
    private TitleLayout titleLayout;
    private boolean progressRequired;
    private boolean progressRendered;
    private boolean loadingStarted;
    private boolean restartSoundNextTick;
    private boolean soundWasActive;
    private boolean playbackStarted;
    private boolean stopped;

    public IntroScreen() {
        super(Component.empty());
    }

    public void startPlayback() {
        if (this.playbackStarted || this.minecraft == null) return;

        this.playbackStarted = true;
        this.minecraft.getMusicManager().stopPlaying();
        this.sound = new IntroSoundInstance(PoSoundEvents.POOPSKY_INTRO.get(), 0.0F);
        this.minecraft.getSoundManager().play(this.sound);
        this.soundWasActive = false;
        KeyMapping.releaseAll();
    }

    public void abort() {
        this.stopPlayback();
    }

    public void resumePlaybackSound() {
        if (this.canPlayIntroSound() && !this.isSoundActive()) {
            this.restartSoundNextTick = true;
        }
    }

    @Override
    protected void init() {
        this.titleLayout = createTitleLayout(this.font);
        this.minecraft.getTextureManager().getTexture(POOP_TEXTURE);
        this.minecraft.getTextureManager().getTexture(SKY_TEXTURE);
        RenderTarget mainTarget = this.minecraft.getMainRenderTarget();
        this.ensureTitleMaskTarget(mainTarget);
        mainTarget.bindWrite(true);
        GLFW.glfwSetInputMode(this.minecraft.getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
        this.startPlayback();
    }

    @Override
    public void tick() {
        this.playbackTicks++;
        if (this.transitionTicks >= 0) {
            this.transitionTicks++;
        }
        if (this.completionTicks >= 0) {
            this.completionTicks++;
        }

        this.updatePlaybackSound();
        float elapsed = this.elapsedSeconds();

        if (this.transitionTicks < 0 && elapsed >= INTRO_DURATION) {
            boolean ready = IntroController.isReadyToFinish();

            if (!ready) {
                this.startLoadingIfNeeded();
            } else if (!this.progressRequired || this.completionSeconds() >= LOADING_COMPLETE_HOLD_DURATION) {
                this.transitionTicks = 0;
            }
        }

        if (this.transitionTicks >= 0
                && this.transitionSeconds() >= FADE_TO_BLACK_DURATION + BLACK_REVEAL_DURATION) {
            this.finishPlayback();
        }
    }

    private void startLoadingIfNeeded() {
        if (this.progressRendered && !this.loadingStarted) {
            this.loadingStarted = true;
            IntroController.onAnimationComplete();
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

        boolean active = this.isSoundActive();

        if (active) {
            this.soundWasActive = true;
        } else if (this.soundWasActive) {
            this.soundWasActive = false;
            this.restartSoundNextTick = true;
        }
    }

    private boolean canPlayIntroSound() {
        return !this.stopped && this.playbackStarted && this.elapsedSeconds() < INTRO_DURATION;
    }

    private boolean isSoundActive() {
        return this.sound != null && this.minecraft.getSoundManager().isActive(this.sound);
    }

    private void restartPlaybackSound() {
        if (!this.canPlayIntroSound()) {
            return;
        }

        if (this.sound != null) {
            this.minecraft.getSoundManager().stop(this.sound);
        }

        this.sound = new IntroSoundInstance(PoSoundEvents.POOPSKY_INTRO.get(), this.elapsedSeconds());

        this.minecraft.getSoundManager().play(this.sound);
        this.soundWasActive = false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float elapsed = this.elapsedSeconds(partialTick);
        float transition = this.transitionSeconds(partialTick);
        if (this.transitionTicks >= 0 && transition >= FADE_TO_BLACK_DURATION) {
            float blackAlpha = 1.0F - smooth(
                    transition,
                    FADE_TO_BLACK_DURATION,
                    FADE_TO_BLACK_DURATION + BLACK_REVEAL_DURATION
            );
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

        float titleAlpha = smooth(elapsed, TITLE_FADE_START, TITLE_FADE_END);
        float textureAlpha = smooth(elapsed, TEXTURE_FADE_START, TEXTURE_FADE_END);
        float iconAlpha = smooth(elapsed, ICON_FADE_START, ICON_FADE_END);
        float yearAlpha = smooth(elapsed, 9.5F, 12.0F);

        if (textureAlpha > MIN_TEXTURE_ALPHA) {
            this.drawTitleReflections(guiGraphics, elapsed, textureAlpha);
        }
        if (titleAlpha > MIN_FONT_ALPHA) {
            this.drawTitleGlyphs(guiGraphics, alphaColor(titleAlpha));
        }
        if (textureAlpha > MIN_TEXTURE_ALPHA) {
            this.drawMaskedTexture(guiGraphics, elapsed, textureAlpha);
        }
        if (iconAlpha > MIN_TEXTURE_ALPHA) {
            this.drawPoopIcon(guiGraphics, iconAlpha);
        }
        if (yearAlpha > MIN_FONT_ALPHA) {
            this.drawYear(guiGraphics, yearAlpha);
        }
        if (elapsed >= INTRO_DURATION && this.transitionTicks < 0
                && (this.progressRequired || !IntroController.isReadyToFinish())) {
            this.drawLoadingText(guiGraphics);
            this.progressRequired = true;
            if (IntroController.isReadyToFinish()) {
                if (this.completionTicks < 0) {
                    this.completionTicks = 0;
                }
            } else {
                this.progressRendered = true;
            }
        }

        guiGraphics.pose().popPose();

        if (this.transitionTicks >= 0) {
            float blackAlpha = smooth(transition, 0.0F, FADE_TO_BLACK_DURATION);
            guiGraphics.fill(0, 0, this.width, this.height, alphaColor(blackAlpha) & 0xFF000000);
        }
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
        if (this.titleMaskTarget != null) {
            this.titleMaskTarget.destroyBuffers();
            this.titleMaskTarget = null;
        }
        IntroController.onScreenClosed(this);
    }

    private void drawTitleGlyphs(GuiGraphics guiGraphics, int color) {
        this.drawScaledText(guiGraphics, TITLE, TITLE_LEFT, TITLE_TEXT_Y, this.titleLayout.scale(), color);
    }

    private void drawPoopIcon(GuiGraphics guiGraphics, float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(POOP_TEXTURE, Mth.floor(this.titleLayout.iconX()), Mth.floor(ICON_TOP), Mth.floor(ICON_SIZE), Mth.floor(ICON_SIZE),
                0.0F, 0.0F, 16, 16, 16, 16);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    private void drawYear(GuiGraphics guiGraphics, float alpha) {
        float scale = YEAR_WIDTH / this.font.width(YEAR);
        float x = (VIRTUAL_WIDTH - YEAR_WIDTH) * 0.5F;
        this.drawScaledText(guiGraphics, YEAR, x, YEAR_TEXT_Y, scale, alphaColor(alpha));
    }

    private void drawLoadingText(GuiGraphics guiGraphics) {
        float x = (VIRTUAL_WIDTH - this.font.width(LOADING_TEXT) * LOADING_TEXT_SCALE) * 0.5F;
        this.drawScaledText(guiGraphics, LOADING_TEXT, x, LOADING_TEXT_Y, LOADING_TEXT_SCALE, 0xFFFFFFFF);
    }

    private void drawMaskedTexture(GuiGraphics guiGraphics, float elapsed, float alpha) {
        guiGraphics.flush();
        RenderTarget mainTarget = this.minecraft.getMainRenderTarget();
        TextureTarget maskTarget = this.ensureTitleMaskTarget(mainTarget);
        maskTarget.clear(Minecraft.ON_OSX);
        maskTarget.bindWrite(true);

        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.drawTitleGlyphs(guiGraphics, 0xFFFFFFFF);
            guiGraphics.flush();

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO,
                    GlStateManager.SourceFactor.DST_ALPHA,
                    GlStateManager.DestFactor.ZERO);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
            this.drawTextureTiles(guiGraphics, elapsed);
        } finally {
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.defaultBlendFunc();
            mainTarget.bindWrite(true);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        maskTarget.blitToScreen(mainTarget.width, mainTarget.height, false);
        RenderSystem.enableDepthTest();
    }

    private void drawTitleReflections(GuiGraphics guiGraphics, float elapsed, float textureAlpha) {
        if (elapsed <= REFLECTION_START) return;

        float phase = Mth.positiveModulo(elapsed - REFLECTION_START, REFLECTION_LOADING_CYCLE);

        float leftAmount = reflectionAmount(phase);
        float rightAmount = reflectionAmount(phase - REFLECTION_SIDE_DELAY);
        if (leftAmount > 0.0F) {
            this.drawReflectionPair(guiGraphics, elapsed, textureAlpha, true, leftAmount);
        } else if (rightAmount > 0.0F) {
            this.drawReflectionPair(guiGraphics, elapsed, textureAlpha, false, rightAmount);
        }
    }

    private void drawReflectionPair(GuiGraphics guiGraphics, float elapsed, float textureAlpha,
                                    boolean moveLeft, float amount) {
        float horizontalOffset = (moveLeft ? -1.0F : 1.0F) * REFLECTION_OFFSET_X * amount;
        float verticalOffset = REFLECTION_OFFSET_Y * amount;

        guiGraphics.flush();
        RenderTarget mainTarget = this.minecraft.getMainRenderTarget();
        TextureTarget maskTarget = this.ensureTitleMaskTarget(mainTarget);
        maskTarget.clear(Minecraft.ON_OSX);
        maskTarget.bindWrite(true);

        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.drawReflectionGlyphs(guiGraphics, horizontalOffset, -verticalOffset);
            this.drawReflectionGlyphs(guiGraphics, horizontalOffset, verticalOffset);

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO,
                    GlStateManager.SourceFactor.DST_ALPHA,
                    GlStateManager.DestFactor.ZERO);
            guiGraphics.setColor(0.5F, 0.72F, 0.82F, textureAlpha * REFLECTION_ALPHA * amount);
            this.drawTextureTiles(guiGraphics, elapsed);
            guiGraphics.flush();
        } finally {
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.defaultBlendFunc();
            mainTarget.bindWrite(true);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        maskTarget.blitToScreen(mainTarget.width, mainTarget.height, false);
        RenderSystem.enableDepthTest();
    }

    private void drawReflectionGlyphs(GuiGraphics guiGraphics, float offsetX, float offsetY) {
        this.enableVirtualScissor(guiGraphics, TITLE_LEFT + offsetX, TITLE_TOP + offsetY, TITLE_RIGHT + offsetX, TITLE_BOTTOM + offsetY);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(offsetX, offsetY, 0.0F);
        this.drawTitleGlyphs(guiGraphics, 0xFFFFFFFF);
        guiGraphics.pose().popPose();
        guiGraphics.flush();
        guiGraphics.disableScissor();
    }

    private void enableVirtualScissor(GuiGraphics guiGraphics, float left, float top, float right, float bottom) {
        float canvasScale = Math.min(this.width / VIRTUAL_WIDTH, this.height / VIRTUAL_HEIGHT);
        float canvasX = (this.width - VIRTUAL_WIDTH * canvasScale) * 0.5F;
        float canvasY = (this.height - VIRTUAL_HEIGHT * canvasScale) * 0.5F;
        guiGraphics.enableScissor(
                Mth.floor(canvasX + left * canvasScale),
                Mth.floor(canvasY + top * canvasScale),
                Mth.ceil(canvasX + right * canvasScale),
                Mth.ceil(canvasY + bottom * canvasScale));
    }

    private TextureTarget ensureTitleMaskTarget(RenderTarget mainTarget) {
        if (this.titleMaskTarget == null) {
            this.titleMaskTarget = new TextureTarget(mainTarget.width, mainTarget.height, false, Minecraft.ON_OSX);
            this.titleMaskTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        } else if (this.titleMaskTarget.width != mainTarget.width || this.titleMaskTarget.height != mainTarget.height) {
            this.titleMaskTarget.resize(mainTarget.width, mainTarget.height, Minecraft.ON_OSX);
        }
        return this.titleMaskTarget;
    }

    private void drawTextureTiles(GuiGraphics guiGraphics, float elapsed) {
        float scrollTime = Math.max(0.0F, elapsed - 7.0F);
        int startX = Mth.floor(Mth.positiveModulo(TILE_START_X + TILE_SPEED_X * scrollTime, TILE_SIZE));
        int startY = Mth.floor(Mth.positiveModulo(TILE_START_Y + TILE_SPEED_Y * scrollTime, TILE_SIZE));

        while (startX > TITLE_LEFT - REFLECTION_OFFSET_X) startX -= TILE_SIZE;
        while (startY > TITLE_TOP - REFLECTION_OFFSET_Y) startY -= TILE_SIZE;

        for (int x = startX; x < TITLE_RIGHT + REFLECTION_OFFSET_X; x += TILE_SIZE) {
            for (int y = startY; y < TITLE_BOTTOM + REFLECTION_OFFSET_Y; y += TILE_SIZE) {
                guiGraphics.blit(SKY_TEXTURE, x, y, 0.0F, 0.0F, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
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
        float secondPLeft = TITLE_LEFT + font.width(titleText("poo")) * scale;
        float secondPWidth = font.width(titleText("p")) * scale;
        float iconX = secondPLeft + (secondPWidth - ICON_SIZE) * 0.5F + ICON_OFFSET_X;
        return new TitleLayout(scale, iconX);
    }

    private static Component titleText(String text) {
        return Component.literal(text).withStyle(style -> style.withFont(FONT));
    }

    private void finishPlayback() {
        this.stopPlayback();
        if (this.minecraft.screen == this) {
            this.minecraft.setScreen(null);
        }
    }

    private void stopPlayback() {
        if (this.stopped) return;
        this.stopped = true;
        this.restartSoundNextTick = false;
        this.minecraft.getSoundManager().stop(this.sound);
    }

    private float elapsedSeconds() {
        return this.playbackTicks / TICKS_PER_SECOND;
    }

    private float transitionSeconds() {
        return this.transitionTicks < 0 ? 0.0F : this.transitionTicks / TICKS_PER_SECOND;
    }

    private float completionSeconds() {
        return this.completionTicks < 0 ? 0.0F : this.completionTicks / TICKS_PER_SECOND;
    }

    private float elapsedSeconds(float partialTick) {
        return (this.playbackTicks + Mth.clamp(partialTick, 0.0F, 1.0F)) / TICKS_PER_SECOND;
    }

    private float transitionSeconds(float partialTick) {
        return this.transitionTicks < 0
                ? 0.0F
                : (this.transitionTicks + Mth.clamp(partialTick, 0.0F, 1.0F)) / TICKS_PER_SECOND;
    }

    private static float smooth(float time, float start, float end) {
        float progress = Mth.clamp((time - start) / (end - start), 0.0F, 1.0F);
        return progress * progress * (3.0F - 2.0F * progress);
    }

    private static float reflectionAmount(float time) {
        if (time <= 0.0F || time >= REFLECTION_DURATION) return 0.0F;
        float progress = time / REFLECTION_DURATION;
        float easedProgress = progress * progress * (3.0F - 2.0F * progress);
        return Mth.sin(easedProgress * Mth.PI);
    }

    private static int alphaColor(float alpha) {
        return Mth.floor(Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F) << 24 | 0xFFFFFF;
    }

    private record TitleLayout(float scale, float iconX) {
    }

    private static final class IntroSoundInstance extends SimpleSoundInstance {
        private static final int SKIP_BUFFER_SIZE = 16_384;
        private final float startSeconds;

        private IntroSoundInstance(SoundEvent soundEvent, float startSeconds) {
            super(soundEvent.getLocation(), SoundSource.MUSIC, 1.0F, 1.0F, SoundInstance.createUnseededRandom(),
                    false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
            this.startSeconds = Math.max(0.0F, startSeconds);
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
