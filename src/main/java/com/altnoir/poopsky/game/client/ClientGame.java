package com.altnoir.poopsky.game.client;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.Game;
import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.GameDefinition;
import com.altnoir.poopsky.game.util.GameUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ClientGame extends Game {
    private static final int RESULT_TEXT_OFFSET = 30;

    @Nullable
    private GameDefinition definition;

    @Nullable
    private BlockPos arcadeMachinePos;

    public void setGameDefinition(GameDefinition definition) {
        this.definition = definition;
    }

    public void setArcadeMachine(BlockPos pos) {
        this.arcadeMachinePos = pos;
    }

    public boolean isArcadeGame() {
        return arcadeMachinePos != null;
    }

    public String getGameName() {
        return definition != null ? definition.gameName() : this.getClass().getSimpleName();
    }

    public void render(GuiGraphics graphics, int posX, int posY) {
        renderBackground(graphics, posX, posY);
        renderGame(graphics, posX, posY);
        renderOverlay(graphics, posX, posY);
    }

    protected void renderBackground(GuiGraphics graphics, int posX, int posY) {
        if (getBackground() != null) {
            graphics.blit(getBackground(), posX, posY, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        }
    }

    protected void renderGame(GuiGraphics graphics, int posX, int posY) {
    }

    public void renderOverlay(GuiGraphics graphics, int posX, int posY) {
        Font font = Minecraft.getInstance().font;

        if (getStage() != GameStage.PLAYING) {
            if (showPressAnyKey()) {
                Component pressAnyKey = Component.translatable("gui.gamingconsole.press_any_key");
                int blinkOffset = getTicks() % 40 <= 20 ? 0 : 1;
                graphics.drawString(
                        font,
                        pressAnyKey,
                        posX + (WIDTH - font.width(pressAnyKey.getVisualOrderText())) / 2 + 1,
                        posY + HEIGHT - font.lineHeight - 1 - blinkOffset,
                        0x373737,
                        false
                );
                graphics.drawString(
                        font,
                        pressAnyKey,
                        posX + (WIDTH - font.width(pressAnyKey.getVisualOrderText())) / 2,
                        posY + HEIGHT - font.lineHeight - 2 - blinkOffset,
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

                int componentColor = Objects.requireNonNull(component.getStyle().getColor()).getValue();
                drawCentered(graphics, font, component, posX, posY + RESULT_TEXT_OFFSET + 29, componentColor);
                drawCentered(graphics, font, component, posX, posY + RESULT_TEXT_OFFSET + 31, componentColor);
                drawCentered(graphics, font, component, posX, posY + RESULT_TEXT_OFFSET + 30, componentColor);
                drawCentered(graphics, font, component, posX + 1, posY + RESULT_TEXT_OFFSET + 30, componentColor);
                drawCentered(graphics, font, component, posX - 1, posY + RESULT_TEXT_OFFSET + 30, componentColor);

                component = getStage() == GameStage.DIED
                        ? Component.translatable("gui.gamingconsole.died").withStyle(ChatFormatting.BOLD, ChatFormatting.RED)
                        : Component.translatable("gui.gamingconsole.won").withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN);
                drawCentered(graphics, font, component, posX, posY + RESULT_TEXT_OFFSET + 30,
                        Objects.requireNonNull(component.getStyle().getColor()).getValue());

                component = Component.translatable("gui.gamingconsole.score").append(": ").append(String.valueOf(getScore())).withStyle(ChatFormatting.YELLOW);
                drawCentered(graphics, font, component, posX, posY + RESULT_TEXT_OFFSET + 35 + font.lineHeight,
                        Objects.requireNonNull(component.getStyle().getColor()).getValue());

                int bestScore = isArcadeGame() ? GameUtils.getArcadeBestScore(arcadeMachinePos, getGameName()) : 0;
                component = Component.translatable(getScore() >= bestScore ? "gui.gamingconsole.new_best_score" : "gui.gamingconsole.best_score")
                        .append(": ").append(String.valueOf(bestScore))
                        .withStyle(getScore() >= bestScore ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
                drawCentered(graphics, font, component, posX, posY + RESULT_TEXT_OFFSET + 50 + font.lineHeight,
                        Objects.requireNonNull(component.getStyle().getColor()).getValue());
            }
        } else if (showScore()) {
            Component score = (scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty())
                    .append(String.valueOf(getScore()));
            graphics.drawString(font, score, posX + 2, posY + 2, 0x373737, false);
            graphics.drawString(font, score, posX + 1, posY + 1, scoreColor(), false);
        }
    }

    private void drawCentered(GuiGraphics graphics, Font font, Component component, int posX, int posY, int color) {
        graphics.drawString(font, component, posX + (WIDTH - font.width(component.getVisualOrderText())) / 2, posY, color, false);
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
