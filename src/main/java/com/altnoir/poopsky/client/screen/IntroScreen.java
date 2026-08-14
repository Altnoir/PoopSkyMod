package com.altnoir.poopsky.client.screen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.IntroController;
import com.altnoir.poopsky.client.renderer.IntroGlyphRenderState;
import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3x2f;
import org.lwjgl.glfw.GLFW;

public class IntroScreen extends Screen {
    private static final Identifier FONT = PoopSky.loc("poopsky_intro");
    private static final Identifier POOP_TEXTURE = PoopSky.loc("textures/item/shit.png");
    private static final Identifier SKY_TEXTURE = PoopSky.loc("textures/gui/poopsky_intro/depth_blue.png");
    private static final FontDescription INTRO_FONT = new FontDescription.Resource(FONT);

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
    private static final int POOP_COLUMNS = 36;
    private static final int POOP_ROWS = 10;
    private static final float MIN_FONT_ALPHA = 4.0F / 255.0F;
    private static final float MIN_TEXTURE_ALPHA = 1.0F / 255.0F;

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

    private static final float TILE_SIZE = 1024.0F;
    private static final float TILE_START_X = 521.0F;
    private static final float TILE_START_Y = 790.0F;
    private static final float TILE_SPEED_X = -46.0F;
    private static final float TILE_SPEED_Y = -100.0F;

    private final Component title;
    private final Component year;
    private final RandomSource random = RandomSource.create();
    private int playbackTicks;
    private int completionTicks = -1;
    private int completionSoundStage;
    private int nextExplosionTick = SHATTER_SOUND_TICK;
    private SoundInstance sound;
    private TitleLayout titleLayout;
    private PoopScatter[] poopScatter;

    public IntroScreen(String title, String year) {
        super(Component.empty());
        this.title = titleText(title == null || title.isBlank() ? "poopsky" : title);
        this.year = titleText(year == null || year.isBlank() ? "2026" : year);
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
        this.minecraft.getTextureManager().getTexture(POOP_TEXTURE);
        this.minecraft.getTextureManager().getTexture(SKY_TEXTURE);
        GLFW.glfwSetInputMode(this.minecraft.getWindow().handle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
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

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        float elapsed = this.elapsedSeconds(partialTick);
        float completion = this.completionSeconds(partialTick);
        if (completion >= WORLD_REVEAL_START) {
            this.extractWorldReveal(guiGraphics, completion);
            return;
        }

        guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);
        float canvasScale = Math.min(this.width / VIRTUAL_WIDTH, this.height / VIRTUAL_HEIGHT);
        float canvasX = (this.width - VIRTUAL_WIDTH * canvasScale) * 0.5F;
        float canvasY = (this.height - VIRTUAL_HEIGHT * canvasScale) * 0.5F;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(canvasX, canvasY);
        guiGraphics.pose().scale(canvasScale, canvasScale);
        float shatterTime = completion - SHATTER_START;
        if (shatterTime >= 0.0F) {
            float shake = 28.0F * (1.0F - smooth(shatterTime, 0.0F, 0.55F));
            guiGraphics.pose().translate(
                    Mth.sin(shatterTime * 54.0F) * shake,
                    Mth.cos(shatterTime * 43.0F) * shake * 0.68F);
        }
        float yearAlpha = smooth(elapsed, 9.5F, 12.0F)
                * (1.0F - smooth(completion, SHATTER_START, SHATTER_START + 0.3F));
        float textureTime = deceleratedTextureTime(elapsed, completion);

        if (completion >= SHATTER_START) {
            float scatterAlpha = smooth(shatterTime, 0.0F, SHATTER_FADE_DURATION);
            float remainingTitleAlpha = 1.0F - scatterAlpha;
            if (remainingTitleAlpha > MIN_TEXTURE_ALPHA) {
                this.drawTexturedTitle(guiGraphics, textureTime,
                        remainingTitleAlpha, remainingTitleAlpha);
            }
            if (scatterAlpha > MIN_TEXTURE_ALPHA) {
                this.drawPoopScatter(guiGraphics, shatterTime, scatterAlpha);
            }
        } else {
            float textureAlpha = smooth(elapsed, TEXTURE_FADE_START, TEXTURE_FADE_END);
            float titleAlpha = smooth(elapsed, TITLE_FADE_START, TITLE_FADE_END);
            if (titleAlpha > MIN_FONT_ALPHA) {
                if (textureAlpha > MIN_TEXTURE_ALPHA) {
                    this.drawTexturedTitle(guiGraphics, textureTime, titleAlpha, textureAlpha);
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

        guiGraphics.pose().popMatrix();
    }

    private void extractWorldReveal(GuiGraphicsExtractor guiGraphics, float completion) {
        if (this.minecraft.level == null) {
            guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);
            return;
        }
        float blackAlpha = 1.0F - smooth(
                completion, WORLD_REVEAL_START, WORLD_REVEAL_START + WORLD_REVEAL_DURATION);
        guiGraphics.fill(0, 0, this.width, this.height, alphaColor(blackAlpha) & 0xFF000000);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
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
        GLFW.glfwSetInputMode(this.minecraft.getWindow().handle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        this.stopPlayback();
        IntroController.onScreenClosed(this);
    }

    private void drawTitleGlyphs(GuiGraphicsExtractor guiGraphics, int color) {
        this.drawScaledText(guiGraphics, this.title, TITLE_LEFT, TITLE_TEXT_Y, this.titleLayout.scale(), color);
    }

    private void drawPoopIcon(GuiGraphicsExtractor guiGraphics, float alpha) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.titleLayout.iconX(), this.titleLayout.iconY());
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, POOP_TEXTURE, 0, 0,
                0.0F, 0.0F, Math.round(ICON_SIZE), Math.round(ICON_SIZE),
                16, 16, 16, 16, alphaColor(alpha));
        guiGraphics.pose().popMatrix();
    }

    private void drawYear(GuiGraphicsExtractor guiGraphics, float alpha) {
        this.drawScaledText(guiGraphics, this.year, YEAR_X, YEAR_TEXT_Y,
                this.titleLayout.yearScale(), alphaColor(alpha));
    }

    private void drawTexturedTitle(GuiGraphicsExtractor guiGraphics, float textureTime,
                                   float titleAlpha, float textureAlpha) {
        this.drawMovingTitleTexture(guiGraphics, textureTime, textureAlpha);
        guiGraphics.nextStratum();
        this.drawTitleGlyphs(guiGraphics, alphaColor(titleAlpha));
    }

    private void drawMovingTitleTexture(GuiGraphicsExtractor guiGraphics,
                                        float textureTime, float alpha) {
        float scrollTime = textureTime - 7.0F;
        float textureOriginX = TILE_START_X + TILE_SPEED_X * scrollTime;
        float textureOriginY = TILE_START_Y + TILE_SPEED_Y * scrollTime;

        int startX = Mth.floor(Mth.positiveModulo(textureOriginX, TILE_SIZE));
        int startY = Mth.floor(Mth.positiveModulo(textureOriginY, TILE_SIZE));
        float minX = TITLE_LEFT;
        float minY = TITLE_TEXT_Y;
        float maxX = TITLE_RIGHT;
        float maxY = TITLE_TEXT_Y + this.titleLayout.height();
        while (startX > minX) {
            startX -= Mth.floor(TILE_SIZE);
        }
        while (startY > minY) {
            startY -= Mth.floor(TILE_SIZE);
        }
        guiGraphics.enableScissor(
                Mth.floor(minX),
                Mth.floor(minY),
                Mth.ceil(maxX),
                Mth.ceil(maxY));
        for (float tileX = startX; tileX < maxX; tileX += TILE_SIZE) {
            for (float tileY = startY; tileY < maxY; tileY += TILE_SIZE) {
                guiGraphics.blit(
                        RenderPipelines.GUI_TEXTURED,
                        SKY_TEXTURE,
                        Mth.floor(tileX),
                        Mth.floor(tileY),
                        0.0F,
                        0.0F,
                        Mth.ceil(TILE_SIZE),
                        Mth.ceil(TILE_SIZE),
                        256,
                        256,
                        256,
                        256,
                        alphaColor(alpha));
            }
        }
        guiGraphics.disableScissor();
    }

    private void drawPoopScatter(GuiGraphicsExtractor guiGraphics, float shatterTime, float alpha) {
        float gravity = 190.0F * shatterTime * shatterTime;
        int color = alphaColor(alpha);
        for (PoopScatter poop : this.poopScatter) {
            float centerX = poop.x() + poop.velocityX() * shatterTime;
            float centerY = poop.y() + poop.velocityY() * shatterTime + gravity;
            float halfSize = poop.halfSize();
            if (centerX + halfSize < 0.0F || centerX - halfSize > VIRTUAL_WIDTH
                    || centerY + halfSize < 0.0F || centerY - halfSize > VIRTUAL_HEIGHT) {
                continue;
            }
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(centerX, centerY);
            guiGraphics.pose().rotate(poop.spin() * shatterTime);
            guiGraphics.pose().scale(halfSize / 8.0F, halfSize / 8.0F);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, POOP_TEXTURE, -8, -8,
                    0.0F, 0.0F, 16, 16, 16, 16, color);
            guiGraphics.pose().popMatrix();
        }
    }

    private void drawScaledText(GuiGraphicsExtractor guiGraphics, Component text,
                                float x, float y, float scale, int color) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(scale, scale);
        GlyphSource glyphSource = this.font.getGlyphSource(INTRO_FONT);
        float cursor = 0.0F;
        for (int offset = 0; offset < text.getString().length(); ) {
            int codePoint = text.getString().codePointAt(offset);
            BakedGlyph glyph = glyphSource.getGlyph(codePoint);
            TextRenderable renderable = glyph.createGlyph(cursor, 0.0F, color, 0, Style.EMPTY, 0.0F, 0.0F);
            guiGraphics.guiRenderState.addGlyphToCurrentLayer(new IntroGlyphRenderState(
                    new Matrix3x2f(guiGraphics.pose()), renderable));
            cursor += glyph.info().getAdvance(false);
            offset += Character.charCount(codePoint);
        }
        guiGraphics.pose().popMatrix();
    }

    private void startPlayback() {
        if (this.sound != null) {
            return;
        }
        this.minecraft.getMusicManager().stopPlaying();
        this.sound = SimpleSoundInstance.forMusic(PoSoundEvents.POOPSKY_INTRO.get());
        this.minecraft.getSoundManager().play(this.sound);
        KeyMapping.releaseAll();
    }

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
        if (completion < 0.0F) {
            return elapsed;
        }
        float movingTime = Math.min(completion, SETTLE_DURATION);
        float progress = movingTime / SETTLE_DURATION;
        float traveled = movingTime
                - movingTime * progress * progress
                + movingTime * progress * progress * progress * 0.5F;
        return elapsed - completion + traveled;
    }

    private static TitleLayout createTitleLayout(Font font, Component title, Component year) {
        float titleWidth = Math.max(measureText(font, title.getString()), 1.0F);
        float scale = (TITLE_RIGHT - TITLE_LEFT) / titleWidth;
        float height = font.lineHeight * scale;
        float iconX;
        float iconY = ICON_TOP;
        if (title.getString().equals("poopsky")) {
            GlyphSource glyphSource = font.getGlyphSource(INTRO_FONT);
            TextRenderable secondP = glyphSource.getGlyph('p')
                    .createGlyph(0.0F, 0.0F, 0xFFFFFFFF, 0, Style.EMPTY, 0.0F, 0.0F);
            float secondPCenter = measureText(font, "poo") + (secondP.left() + secondP.right()) * 0.5F;
            iconX = TITLE_LEFT + secondPCenter * scale - ICON_SIZE * 0.5F;
            if (PoMods.MODERNUI.isLoaded()) {
                iconX += MODERN_UI_ICON_OFFSET_X;
                iconY += MODERN_UI_ICON_OFFSET_Y;
            }
        } else {
            iconX = (VIRTUAL_WIDTH - ICON_SIZE) * 0.5F;
        }
        float yearScale = YEAR_WIDTH / Math.max(measureText(font, year.getString()), 1.0F);
        return new TitleLayout(scale, height, iconX, iconY, yearScale);
    }

    private static float measureText(Font font, String text) {
        GlyphSource glyphSource = font.getGlyphSource(INTRO_FONT);
        float width = 0.0F;
        for (int offset = 0; offset < text.length(); ) {
            int codePoint = text.codePointAt(offset);
            width += glyphSource.getGlyph(codePoint).info().getAdvance(false);
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
        return Component.literal(text).withStyle(style -> style.withFont(INTRO_FONT));
    }

    private static float smooth(float time, float start, float end) {
        float progress = Mth.clamp((time - start) / (end - start), 0.0F, 1.0F);
        return (float) Mth.smoothstep(progress);
    }

    private static int alphaColor(float alpha) {
        return colorFromFloat(alpha, 1.0F, 1.0F, 1.0F);
    }

    private static int colorFromFloat(float alpha, float red, float green, float blue) {
        return Mth.floor(Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F) << 24
                | Mth.floor(Mth.clamp(red, 0.0F, 1.0F) * 255.0F) << 16
                | Mth.floor(Mth.clamp(green, 0.0F, 1.0F) * 255.0F) << 8
                | Mth.floor(Mth.clamp(blue, 0.0F, 1.0F) * 255.0F);
    }

    private record TitleLayout(float scale, float height, float iconX, float iconY, float yearScale) {
    }

    private record PoopScatter(float x, float y, float velocityX, float velocityY, float halfSize, float spin) {
    }
}
