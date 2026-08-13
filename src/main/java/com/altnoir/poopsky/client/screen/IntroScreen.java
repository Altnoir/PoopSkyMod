package com.altnoir.poopsky.client.screen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.IntroController;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.lwjgl.glfw.GLFW;

public class IntroScreen extends Screen {
    private static final Identifier FONT = PoopSky.loc("poopsky_intro");
    private static final Identifier POOP_TEXTURE = PoopSky.loc("textures/item/shit.png");

    private static final float INTRO_DURATION = 17.0F;
    private static final float SETTLE_DURATION = 1.4F;
    private static final float STILL_DURATION = 0.45F;
    private static final float SHATTER_START = SETTLE_DURATION + STILL_DURATION;
    private static final float SHATTER_DURATION = 2.25F;
    private static final float WORLD_REVEAL_DURATION = 1.2F;
    private static final float WORLD_REVEAL_START = SHATTER_START + SHATTER_DURATION;
    private static final float COMPLETION_DURATION = WORLD_REVEAL_START + WORLD_REVEAL_DURATION;
    private static final float TICKS_PER_SECOND = 20.0F;
    private static final int INTRO_TICKS = Mth.ceil(INTRO_DURATION * TICKS_PER_SECOND);
    private static final int SHATTER_SOUND_TICK = Mth.ceil(SHATTER_START * TICKS_PER_SECOND);
    private static final int COMPLETION_TICKS = Mth.ceil(COMPLETION_DURATION * TICKS_PER_SECOND);
    private static final int ICON_SIZE = 44;

    private final Component title;
    private final Component year;
    private final RandomSource random = RandomSource.create();
    private int playbackTicks;
    private int completionTicks = -1;
    private int completionSoundStage;
    private int nextExplosionTick = SHATTER_SOUND_TICK;
    private SoundInstance sound;

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
        this.minecraft.getTextureManager().getTexture(POOP_TEXTURE);
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
        float completion = this.completionSeconds(partialTick);
        if (completion >= WORLD_REVEAL_START) {
            this.extractWorldReveal(guiGraphics, completion);
            return;
        }

        float elapsed = this.elapsedSeconds(partialTick);
        float titleAlpha = smooth(elapsed, 4.0F, 8.0F);
        float iconAlpha = smooth(elapsed, 2.0F, 4.5F);
        float yearAlpha = smooth(elapsed, 9.5F, 12.0F);
        float shatterTime = completion - SHATTER_START;

        guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);
        if (shatterTime >= 0.0F) {
            this.drawShatter(guiGraphics, shatterTime);
            return;
        }

        int centerX = this.width / 2;
        int titleY = this.height / 2 - 30;
        if (titleAlpha > 0.0F) {
            guiGraphics.centeredText(this.font, this.title, centerX, titleY, alphaColor(titleAlpha));
        }
        if (iconAlpha > 0.0F) {
            IntroScreen.blitPoop(guiGraphics, centerX - ICON_SIZE / 2, titleY + 26, ICON_SIZE);
        }
        if (yearAlpha > 0.0F) {
            guiGraphics.centeredText(this.font, this.year, centerX, titleY + 94, alphaColor(yearAlpha));
        }
    }

    private void extractWorldReveal(GuiGraphicsExtractor guiGraphics, float completion) {
        if (this.minecraft.level == null) {
            guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);
            return;
        }
        float alpha = 1.0F - smooth(completion, WORLD_REVEAL_START, WORLD_REVEAL_START + WORLD_REVEAL_DURATION);
        guiGraphics.fill(0, 0, this.width, this.height, alphaColor(alpha) & 0xFF000000);
    }

    private void drawShatter(GuiGraphicsExtractor guiGraphics, float shatterTime) {
        float progress = smooth(shatterTime, 0.0F, SHATTER_DURATION);
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int count = 28;
        for (int i = 0; i < count; i++) {
            float angle = Mth.TWO_PI * i / count;
            float distance = 18.0F + progress * progress * (70.0F + i % 5 * 24.0F);
            int x = Mth.floor(centerX + Mth.cos(angle) * distance);
            int y = Mth.floor(centerY + Mth.sin(angle) * distance + progress * progress * 80.0F);
            IntroScreen.blitPoop(guiGraphics, x, y, 10 + i % 8);
        }
    }

    private static void blitPoop(GuiGraphicsExtractor guiGraphics, int x, int y, int size) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, POOP_TEXTURE, x, y, 0.0F, 0.0F, size, size, 16, 16);
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
        this.stopPlayback();
        IntroController.onScreenClosed(this);
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

    private static Component titleText(String text) {
        return Component.literal(text).withStyle(style -> style.withFont(new FontDescription.Resource(FONT)));
    }

    private static float smooth(float time, float start, float end) {
        float progress = Mth.clamp((time - start) / (end - start), 0.0F, 1.0F);
        return (float) Mth.smoothstep(progress);
    }

    private static int alphaColor(float alpha) {
        return Mth.floor(Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F) << 24 | 0xFFFFFF;
    }
}
