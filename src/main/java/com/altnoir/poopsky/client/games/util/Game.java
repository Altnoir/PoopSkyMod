package com.altnoir.poopsky.client.games.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.PacketDistributor;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.ClientUtils;
import com.altnoir.poopsky.client.games.audio.SoundPlayer;
import com.altnoir.poopsky.client.games.controls.Button;
import com.altnoir.poopsky.client.games.controls.Controls;
import com.altnoir.poopsky.client.games.graphics.ParticleColor;
import com.altnoir.poopsky.client.games.graphics.Renderer;
import com.altnoir.poopsky.impl.network.LightArcadeBlockUpdatePacket;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class Game {
    public static final int WIDTH = 224;
    public static final int HEIGHT = 160;
    private static final int RESULT_TEXT_OFFSET = 30;

    public GameStage stage = GameStage.START;
    public Controls controls = new Controls(this);
    public SoundPlayer soundPlayer = new SoundPlayer();
    public final Random random = new Random();
    public int ticks = 0;
    public int score = 0;
    public int lives = maxLives();

    @Nullable
    private BlockPos arcadeMachinePos;
    private boolean arcadeSettled;
    private int settledScore;
    private final List<Particle> particles = new ArrayList<>();

    public int maxLives() {
        return 1;
    }

    public void prepare() {
        score = 0;
        lives = maxLives();
        arcadeSettled = false;
        settledScore = 0;
        respawn();
    }

    public void setArcadeMachine(BlockPos pos) {
        this.arcadeMachinePos = pos;
    }

    public boolean isArcadeGame() {
        return arcadeMachinePos != null;
    }

    @Nullable
    public BlockPos getArcadeMachinePos() {
        return arcadeMachinePos;
    }

    private String getGameName() {
        return this.getClass().getSimpleName();
    }

    public void start() {
        stage = GameStage.PLAYING;
        ticks = 1;
        arcadeSettled = false;
    }

    public void die() {
        finishArcadeGame();
        if (isArcadeGame()) {
            int bestScore = ClientUtils.getArcadeBestScore(arcadeMachinePos, getGameName());
            if (bestScore < settledScore) {
                soundPlayer.playNewBest();
                spawnConfetti();
            } else {
                soundPlayer.playGameOver();
            }
        } else {
            soundPlayer.playGameOver();
        }

        stage = GameStage.DIED;
        ticks = 1;
    }

    public void lostLife() {
        lives--;
        soundPlayer.play(net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE.value());
        respawn();
        if (lives <= 0) {
            die();
        }
    }

    public void respawn() {
        stage = GameStage.START;
        ticks = 1;
        particles.clear();
    }

    public void win() {
        finishArcadeGame();
        soundPlayer.playNewBest();
        spawnConfetti();
        stage = GameStage.WON;
        ticks = 1;
    }

    public void closeArcadeGame() {
        finishArcadeGame();
    }

    private void finishArcadeGame() {
        if (isArcadeGame() && !arcadeSettled) {
            settledScore = score;
            sendArcadeScoreUpdate();
            arcadeSettled = true;
            score = 0;
        }
    }

    public void tick() {
        if (stage == GameStage.PLAYING && ticks % gameTickDuration() == 0) {
            gameTick();
        }
        int i = 0;
        while (i < particles.size()) {
            Particle particle = particles.get(i);
            particle.tick();
            if (particle.isDead()) {
                particles.remove(i);
                i--;
            }
            i++;
        }
        ticks++;
    }

    private void sendArcadeScoreUpdate() {
        if (!isArcadeGame()) {
            return;
        }

        PacketDistributor.sendToServer(new LightArcadeBlockUpdatePacket(arcadeMachinePos, getGameName(), score));
    }

    public void gameTick() {
    }

    public void render(GuiGraphics graphics, int posX, int posY) {
        if (getBackground() != null) {
            graphics.blit(getBackground(), posX, posY, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        }
        renderOverlay(graphics, posX, posY);
    }

    public void renderOverlay(GuiGraphics graphics, int posX, int posY) {
        Font font = Minecraft.getInstance().font;

        if (stage != GameStage.PLAYING) {
            if (showPressAnyKey()) {
                graphics.drawString(
                        font,
                        Component.translatable("gui.gamingconsole.press_any_key"),
                        posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.press_any_key").getVisualOrderText())) / 2 + 1,
                        posY + HEIGHT - font.lineHeight - 1 - (ticks % 40 <= 20 ? 0 : 1),
                        0x373737,
                        false
                );
                graphics.drawString(
                        font,
                        Component.translatable("gui.gamingconsole.press_any_key"),
                        posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.press_any_key").getVisualOrderText())) / 2,
                        posY + HEIGHT - font.lineHeight - 2 - (ticks % 40 <= 20 ? 0 : 1),
                        0xFFFFFF,
                        false
                );
            }

            if (stage == GameStage.DIED || stage == GameStage.WON) {
                graphics.blit(PoopSky.loc("textures/gui/score_board.png"), posX, posY, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);

                Component component = stage == GameStage.DIED
                        ? Component.translatable("gui.gamingconsole.died").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_RED)
                        : Component.translatable("gui.gamingconsole.won").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GREEN);

                graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2, posY + RESULT_TEXT_OFFSET + 29,
                        Objects.requireNonNull(component.getStyle().getColor()).getValue(), false);
                graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2, posY + RESULT_TEXT_OFFSET + 31,
                        component.getStyle().getColor().getValue(), false);
                graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2 + 1, posY + RESULT_TEXT_OFFSET + 30,
                        component.getStyle().getColor().getValue(), false);
                graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2 - 1, posY + RESULT_TEXT_OFFSET + 30,
                        component.getStyle().getColor().getValue(), false);

                component = stage == GameStage.DIED
                        ? Component.translatable("gui.gamingconsole.died").withStyle(ChatFormatting.BOLD, ChatFormatting.RED)
                        : Component.translatable("gui.gamingconsole.won").withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN);
                graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2, posY + RESULT_TEXT_OFFSET + 30,
                        Objects.requireNonNull(component.getStyle().getColor()).getValue(), false);

                component = Component.translatable("gui.gamingconsole.score").append(": ").append(String.valueOf(settledScore)).withStyle(ChatFormatting.YELLOW);
                graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2, posY + RESULT_TEXT_OFFSET + 35 + font.lineHeight,
                        Objects.requireNonNull(component.getStyle().getColor()).getValue(), false);

                int bestScore = isArcadeGame() ? ClientUtils.getArcadeBestScore(arcadeMachinePos, getGameName()) : 0;
                component = Component.translatable(settledScore >= bestScore ? "gui.gamingconsole.new_best_score" : "gui.gamingconsole.best_score")
                        .append(": ").append(String.valueOf(bestScore))
                        .withStyle(settledScore >= bestScore ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
                graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2, posY + RESULT_TEXT_OFFSET + 50 + font.lineHeight,
                        Objects.requireNonNull(component.getStyle().getColor()).getValue(), false);
            }
        } else {
            if (showScore()) {
                graphics.drawString(font,
                        (scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty()).append(String.valueOf(score)),
                        posX + 2, posY + 2, 0x373737, false);
                graphics.drawString(font,
                        (scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty()).append(String.valueOf(score)),
                        posX + 1, posY + 1, scoreColor(), false);
            }
        }

        for (Particle particle : particles) {
            if (particle.isForOverlay()) {
                particle.render(graphics, posX, posY, stage);
            }
        }
    }

    public void renderParticles(GuiGraphics graphics, int posX, int posY) {
        for (Particle particle : particles) {
            particle.render(graphics, posX, posY, stage);
        }
    }

    public void buttonDown(Button button) {
        soundPlayer.playClick(true);
        if ((stage == GameStage.START || stage == GameStage.RETRY) && ticks > 8) {
            start();
        } else if ((stage == GameStage.WON || stage == GameStage.DIED) && ticks > 8) {
            prepare();
        }
    }

    public Particle addParticle(Particle particle) {
        particles.add(particle);
        return particle;
    }

    public void spawnParticleExplosion(Supplier<Renderer> renderer, Vec2 pos, int count, int speed, int lifetime, ParticleLevel level) {
        for (int i = 0; i < count; i++) {
            Particle particle = new Particle(pos, renderer.get(), random.nextInt(lifetime / 2, lifetime), level);
            particle.setVelocity(new Vec2(random.nextFloat(-speed, speed), random.nextFloat(-speed, speed)));
            particles.add(particle);
        }
    }

    public void spawnParticleExplosion(Vec2 pos, int count, int speed, int lifetime, ParticleLevel level) {
        soundPlayer.play(SoundEvents.GENERIC_EXPLODE.value(), 1.5f, 0.1f);
        for (int i = 0; i < count; i++) {
            Particle particle = new ExplosionParticle(pos, random.nextInt(lifetime / 2, lifetime), level);
            particle.setVelocity(new Vec2(random.nextFloat(-speed, speed), random.nextFloat(-speed, speed)));
            particles.add(particle);
        }
    }

    public void spawnConfetti() {
        for (int i = 0; i < 30; i++) {
            Particle particle = new ConfettiParticle(new Vec2(0, HEIGHT), ParticleColor.random(random), random.nextInt(50, 70), ParticleLevel.OVERLAY);
            particle.setVelocity(new Vec2(random.nextFloat(1, 10), random.nextFloat(-25, -10)));
            particles.add(particle);
        }
        for (int i = 0; i < 30; i++) {
            Particle particle = new ConfettiParticle(new Vec2(WIDTH, HEIGHT), ParticleColor.random(random), random.nextInt(50, 70), ParticleLevel.OVERLAY);
            particle.setVelocity(new Vec2(random.nextFloat(-10, -1), random.nextFloat(-25, -10)));
            particles.add(particle);
        }
    }

    public void buttonUp(Button button) {
        soundPlayer.playClick(false);
    }

    public int gameTickDuration() {
        return 1;
    }

    public ResourceLocation getBackground() {
        return null;
    }

    public boolean showScoreBox() {
        return true;
    }

    public boolean showScore() {
        return true;
    }

    public boolean showPressAnyKey() {
        return true;
    }

    public int scoreColor() {
        return 0xFFFFFF;
    }

    public boolean scoreText() {
        return true;
    }

    public Component getName() {
        return Component.empty();
    }

    public ResourceLocation getIcon() {
        return null;
    }

    public ChatFormatting getColor() {
        return ChatFormatting.YELLOW;
    }

    public boolean isEmpty() {
        return this.getClass().equals(Game.class);
    }
}
