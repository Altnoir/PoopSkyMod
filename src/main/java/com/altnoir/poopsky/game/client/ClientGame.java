package com.altnoir.poopsky.game.client;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.util.GameStage;
import com.altnoir.poopsky.game.util.GameUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ClientGame {
    private static final int RESULT_TEXT_OFFSET = 30;
    public static final int WIDTH = 224;
    public static final int HEIGHT = 160;

    private GameStage stage = GameStage.START;
    private int score;
    private int ticks = 1;

    @Nullable
    private BlockPos arcadeMachinePos;

    public void setArcadeMachine(BlockPos pos) {
        this.arcadeMachinePos = pos;
    }

    public boolean isArcadeGame() {
        return arcadeMachinePos != null;
    }

    protected GameStage getStage() {
        return stage;
    }

    protected int getScore() {
        return score;
    }

    protected int getTicks() {
        return ticks;
    }

    public String getGameName() {
        return this.getClass().getSimpleName();
    }

    public void applySnapshot(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        String stageName = tag.getString("stage");
        if (stageName.isEmpty()) {
            return;
        }
        stage = GameStage.valueOf(stageName);
        score = tag.getInt("score");
        ticks = tag.getInt("ticks");
    }

    public void render(GuiGraphics graphics, int posX, int posY) {
        if (getBackground() != null) {
            graphics.blit(getBackground(), posX, posY, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        }
        renderOverlay(graphics, posX, posY);
    }

    public void renderOverlay(GuiGraphics graphics, int posX, int posY) {
        Font font = Minecraft.getInstance().font;

        if (getStage() != GameStage.PLAYING) {
            if (showPressAnyKey()) {
                graphics.drawString(
                        font,
                        Component.translatable("gui.gamingconsole.press_any_key"),
                        posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.press_any_key").getVisualOrderText())) / 2 + 1,
                        posY + HEIGHT - font.lineHeight - 1 - (getTicks() % 40 <= 20 ? 0 : 1),
                        0x373737,
                        false
                );
                graphics.drawString(
                        font,
                        Component.translatable("gui.gamingconsole.press_any_key"),
                        posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.press_any_key").getVisualOrderText())) / 2,
                        posY + HEIGHT - font.lineHeight - 2 - (getTicks() % 40 <= 20 ? 0 : 1),
                        0xFFFFFF,
                        false
                );
            }

            if (getStage() == GameStage.DIED || getStage() == GameStage.WON) {
                graphics.blit(PoopSky.loc("textures/gui/score_board.png"), posX + (WIDTH - 140) / 2, posY + (HEIGHT - 100) / 2,
                        0, 0, 140, 100, 140, 100);

                Component component = getStage() == GameStage.DIED
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

                component = getStage() == GameStage.DIED
                        ? Component.translatable("gui.gamingconsole.died").withStyle(ChatFormatting.BOLD, ChatFormatting.RED)
                        : Component.translatable("gui.gamingconsole.won").withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN);
                graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2, posY + RESULT_TEXT_OFFSET + 30,
                        Objects.requireNonNull(component.getStyle().getColor()).getValue(), false);

                component = Component.translatable("gui.gamingconsole.score").append(": ").append(String.valueOf(getScore())).withStyle(ChatFormatting.YELLOW);
                graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2, posY + RESULT_TEXT_OFFSET + 35 + font.lineHeight,
                        Objects.requireNonNull(component.getStyle().getColor()).getValue(), false);

                int bestScore = isArcadeGame() ? GameUtils.getArcadeBestScore(arcadeMachinePos, getGameName()) : 0;
                component = Component.translatable(getScore() >= bestScore ? "gui.gamingconsole.new_best_score" : "gui.gamingconsole.best_score")
                        .append(": ").append(String.valueOf(bestScore))
                        .withStyle(getScore() >= bestScore ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
                graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2, posY + RESULT_TEXT_OFFSET + 50 + font.lineHeight,
                        Objects.requireNonNull(component.getStyle().getColor()).getValue(), false);
            }
        } else if (showScore()) {
            graphics.drawString(font,
                    (scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty()).append(String.valueOf(getScore())),
                    posX + 2, posY + 2, 0x373737, false);
            graphics.drawString(font,
                    (scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty()).append(String.valueOf(getScore())),
                    posX + 1, posY + 1, scoreColor(), false);
        }
    }

    public ResourceLocation getBackground() {
        return null;
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
}