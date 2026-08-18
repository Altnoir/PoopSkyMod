package com.altnoir.poopsky.game.client;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.Game;
import com.altnoir.poopsky.game.GameDefinition;
import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.util.GameUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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
        return definition == null ? getClass().getSimpleName() : definition.gameName();
    }

    public void render(GuiGraphics graphics, int posX, int posY) {
        render(graphics, posX, posY, 1.0F);
    }

    public void render(GuiGraphics graphics, int posX, int posY, float partialTick) {
        renderBackground(graphics, posX, posY);
        renderGame(graphics, posX, posY, partialTick);
        renderOverlay(graphics, posX, posY);
    }

    protected void renderBackground(GuiGraphics graphics, int posX, int posY) {
        if (getBackground() != null) {
            graphics.blit(getBackground(), posX, posY, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        }
    }

    protected void renderGame(GuiGraphics graphics, int posX, int posY) {
    }

    protected void renderGame(GuiGraphics graphics, int posX, int posY, float partialTick) {
        renderGame(graphics, posX, posY);
    }

    public void renderOverlay(GuiGraphics graphics, int posX, int posY) {
        Font font = Minecraft.getInstance().font;
        GameStage stage = getStage();

        if (stage == GameStage.PLAYING) {
            if (showScore()) {
                Component score = (scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty()).append(String.valueOf(getScore()));
                graphics.drawString(font, score, posX + 2, posY + 2, 0x373737, false);
                graphics.drawString(font, score, posX + 1, posY + 1, scoreColor(), false);
            }
            return;
        }

        if (showPressAnyKey()) {
            Component pressAnyKey = Component.translatable("gui.gamingconsole.press_any_key");
            int offset = getTicks() % 40 <= 20 ? 0 : 1;
            drawCentered(graphics, font, pressAnyKey, posX + 1, posY + HEIGHT - font.lineHeight - 1 - offset, 0x373737);
            drawCentered(graphics, font, pressAnyKey, posX, posY + HEIGHT - font.lineHeight - 2 - offset, 0xFFFFFF);
        }

        if (stage != GameStage.DIED && stage != GameStage.WON) {
            return;
        }

        graphics.blit(PoopSky.loc("textures/gui/score_board.png"), posX + (WIDTH - 140) / 2, posY + (HEIGHT - 100) / 2, 0, 0, 140, 100, 140, 100);

        boolean died = stage == GameStage.DIED;
        drawResult(graphics, font, posX, posY, died);

        Component score = Component.translatable("gui.gamingconsole.score").append(": ").append(String.valueOf(getScore())).withStyle(ChatFormatting.YELLOW);
        drawCentered(graphics, font, score, posX, posY + RESULT_TEXT_OFFSET + 35 + font.lineHeight, getColor(score));

        int bestScore = isArcadeGame() ? GameUtils.getArcadeBestScore(arcadeMachinePos, getGameName()) : 0;
        Component best = Component.translatable(getScore() >= bestScore ? "gui.gamingconsole.new_best_score" : "gui.gamingconsole.best_score")
                .append(": ").append(String.valueOf(bestScore))
                .withStyle(getScore() >= bestScore ? ChatFormatting.GREEN : ChatFormatting.YELLOW);

        drawCentered(graphics, font, best, posX, posY + RESULT_TEXT_OFFSET + 50 + font.lineHeight, getColor(best));
    }

    private void drawResult(GuiGraphics graphics, Font font, int x, int y, boolean died) {
        Component shadow = Component.translatable(died ? "gui.gamingconsole.died" : "gui.gamingconsole.won")
                .withStyle(ChatFormatting.BOLD, died ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN);

        int color = getColor(shadow);
        drawCentered(graphics, font, shadow, x, y + RESULT_TEXT_OFFSET + 29, color);
        drawCentered(graphics, font, shadow, x, y + RESULT_TEXT_OFFSET + 31, color);
        drawCentered(graphics, font, shadow, x, y + RESULT_TEXT_OFFSET + 30, color);
        drawCentered(graphics, font, shadow, x + 1, y + RESULT_TEXT_OFFSET + 30, color);
        drawCentered(graphics, font, shadow, x - 1, y + RESULT_TEXT_OFFSET + 30, color);

        Component text = Component.translatable(died ? "gui.gamingconsole.died" : "gui.gamingconsole.won")
                .withStyle(ChatFormatting.BOLD, died ? ChatFormatting.RED : ChatFormatting.GREEN);

        drawCentered(graphics, font, text, x, y + RESULT_TEXT_OFFSET + 30, getColor(text));
    }

    private void drawCentered(GuiGraphics graphics, Font font, Component component, int x, int y, int color) {
        graphics.drawString(font, component, x + (WIDTH - font.width(component.getVisualOrderText())) / 2, y, color, false);
    }

    private int getColor(Component component) {
        return component.getStyle().getColor() == null ? 0xFFFFFF : component.getStyle().getColor().getValue();
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

    public boolean requiresPerFrameRender() {
        return false;
    }
}
