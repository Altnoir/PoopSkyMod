package com.altnoir.poopsky.client.screen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoAnimationController;
import com.altnoir.poopsky.init.PoSoundEvents;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class PoemScreen extends Screen {
    private static final String TEXT_DIRECTORY = "texts/poopsky/";
    private static final Identifier FALLBACK_TEXT_LOCATION = PoopSky.loc(TEXT_DIRECTORY + "en_us.txt");
    private static final Identifier TITLE_LOCATION = PoopSky.loc("textures/gui/poopsky.png");
    private static final Identifier VIGNETTE_LOCATION = PoopSky.mcloc("textures/misc/credits_vignette.png");
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
        if (!this.lines.isEmpty()) {
            return;
        }

        Identifier textLocation = this.getTextLocation();
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

    private Identifier getTextLocation() {
        String languageCode = this.minecraft.getLanguageManager().getSelected();
        Identifier localizedTextLocation = PoopSky.loc(TEXT_DIRECTORY + languageCode + ".txt");
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
        if (this.playbackTicks <= SCROLL_START_TICK) {
            return;
        }

        this.previousScroll = this.scroll;
        this.scrollingTicks++;
        this.scroll = Math.max(0.0F, this.scroll + this.calculateScrollSpeed());
        if (this.scroll > this.endScrollPosition()) {
            this.finish();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.extractVignette(guiGraphics);

        float renderedScroll = Mth.lerp(partialTick, this.previousScroll, this.scroll);
        float offset = -renderedScroll;
        int contentLeft = this.width / 2 - CONTENT_WIDTH / 2;
        int titleLeft = this.width / 2 - TITLE_WIDTH / 2;
        int titleTop = (this.height - TITLE_HEIGHT) / 2;
        int lineY = this.height + 24;
        boolean scrolling = this.playbackTicks >= SCROLL_START_TICK;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.width / 2.0F, this.height / 2.0F);
        guiGraphics.pose().rotate(scrolling ? Mth.PI : this.titleRotation(partialTick));
        guiGraphics.pose().translate(-this.width / 2.0F, -this.height / 2.0F + offset);
        guiGraphics.blit(TITLE_LOCATION,
                titleLeft, titleTop, titleLeft + TITLE_WIDTH, titleTop + TITLE_HEIGHT,
                0.0F, 1.0F, 0.0F, 1.0F);

        if (scrolling) {
            for (FormattedCharSequence line : this.lines) {
                if (lineY + offset + 20.0F > 0.0F && lineY + offset < this.height) {
                    guiGraphics.text(this.font, line, contentLeft, lineY, 0xFFFFFFFF);
                }
                lineY += 12;
            }
        }
        guiGraphics.pose().popMatrix();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture sky = textureManager.getTexture(AbstractEndPortalRenderer.END_SKY_LOCATION);
        AbstractTexture portal = textureManager.getTexture(AbstractEndPortalRenderer.END_PORTAL_LOCATION);
        TextureSetup textures = TextureSetup.doubleTexture(
                sky.getTextureView(), sky.getSampler(), portal.getTextureView(), portal.getSampler());
        guiGraphics.fill(RenderPipelines.END_PORTAL, textures, 0, 0, this.width, this.height);
    }

    private void extractVignette(GuiGraphicsExtractor guiGraphics) {
        guiGraphics.blit(RenderPipelines.VIGNETTE, VIGNETTE_LOCATION, 0, 0,
                0.0F, 0.0F, this.width, this.height, this.width, this.height);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (keyCode == 265) {
            this.direction = -1;
        } else if (keyCode == 341 || keyCode == 345) {
            this.speedupModifiers.add(keyCode);
        } else if (keyCode == 32) {
            this.speedupActive = true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        int keyCode = event.key();
        if (keyCode == 265) {
            this.direction = 1;
        }
        if (keyCode == 32) {
            this.speedupActive = false;
        } else if (keyCode == 341 || keyCode == 345) {
            this.speedupModifiers.remove(keyCode);
        }
        return super.keyReleased(event);
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

    private float calculateScrollSpeed() {
        float progress = Mth.clamp(this.scrollingTicks / (float) SCROLL_DECELERATION_TICKS, 0.0F, 1.0F);
        float baseSpeed = Mth.lerp((float) Mth.smoothstep(progress), INITIAL_SCROLL_SPEED, BASE_SCROLL_SPEED);
        if (!this.speedupActive) {
            return baseSpeed * this.direction;
        }
        return baseSpeed * (5.0F + this.speedupModifiers.size() * 15.0F) * this.direction;
    }

    private float titleRotation(float partialTick) {
        float progress = Mth.clamp(
                (this.playbackTicks + partialTick - TITLE_HOLD_TICKS) / TITLE_ROTATION_TICKS, 0.0F, 1.0F);
        return Mth.DEG_TO_RAD * (float) Mth.smoothstep(progress) * 180.0F;
    }

    private float endScrollPosition() {
        if (this.lines.isEmpty()) {
            return this.height + TITLE_HEIGHT;
        }
        int lastLineY = this.height + 24 + (this.lines.size() - 1) * 12;
        return lastLineY + 20.0F;
    }

    private void finish() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        PoAnimationController.finish(this);
    }
}
