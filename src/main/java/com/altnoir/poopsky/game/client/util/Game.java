package com.altnoir.poopsky.game.client.util;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.ClientUtils;
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

public class Game {
    public static final int WIDTH = 224;
    public static final int HEIGHT = 160;
    private static final int RESULT_TEXT_OFFSET = 30;

    public GameStage stage = GameStage.START;
    public int ticks = 1;
    public int score;
    private int settledScore;

    @Nullable
    private BlockPos arcadeMachinePos;

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
        settledScore = tag.getInt("settled_score");
    }

    public void prepare() {
        score = 0;
        settledScore = 0;
        stage = GameStage.START;
        ticks = 1;
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
        } else if (showScore()) {
            graphics.drawString(font,
                    (scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty()).append(String.valueOf(score)),
                    posX + 2, posY + 2, 0x373737, false);
            graphics.drawString(font,
                    (scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty()).append(String.valueOf(score)),
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
