package com.altnoir.poopsky.client.screen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.ClientUtils;
import com.altnoir.poopsky.client.games.controls.Button;
import com.altnoir.poopsky.client.games.util.Game;
import com.altnoir.poopsky.content.item.p.GameDiscItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GamingConsoleScreen extends Screen {
    private static final ResourceLocation BACKGROUND = PoopSky.loc("textures/gui/gaming_console.png");
    private static final int CONSOLE_WIDTH = 300;
    private static final int CONSOLE_HEIGHT = 250;
    private static final int SCREEN_X = 10;
    private static final int SCREEN_Y = 10;

    private static final int W = 87;
    private static final int S = 83;
    private static final int A = 65;
    private static final int D = 68;
    private static final int E = 69;
    private static final int SPACE = 32;
    private static final int ENTER = 257;

    private static final VisualButton W_BUTTON = new VisualButton(BACKGROUND, 512, 512, 41, 169, 14, 24, 23, 256, 24);
    private static final VisualButton A_BUTTON = new VisualButton(BACKGROUND, 512, 512, 25, 185, 23, 15, 0, 256, 24);
    private static final VisualButton D_BUTTON = new VisualButton(BACKGROUND, 512, 512, 48, 185, 23, 15, 38, 256, 24);
    private static final VisualButton S_BUTTON = new VisualButton(BACKGROUND, 512, 512, 41, 193, 14, 23, 60, 256, 24);
    private static final VisualButton B1_BUTTON = new VisualButton(BACKGROUND, 512, 512, 104, 184, 16, 16, 234, 0, 24);
    private static final VisualButton B2_BUTTON = new VisualButton(BACKGROUND, 512, 512, 156, 176, 16, 16, 234, 0, 24);

    private final BlockPos arcadeMachinePos;
    private Game game;
    private int closeTimer;

    public GamingConsoleScreen(Component title, BlockPos arcadeMachinePos, GameDiscItem cartridge) {
        super(title);
        this.arcadeMachinePos = arcadeMachinePos;

        Game arcadeGame = ClientUtils.newGameFor(cartridge);
        arcadeGame.setArcadeMachine(arcadeMachinePos);
        arcadeGame.prepare();
        this.game = arcadeGame;
    }

    private int getConsoleX() {
        return (this.width - CONSOLE_WIDTH) / 2;
    }

    private int getConsoleY() {
        return (this.height - CONSOLE_HEIGHT) / 2;
    }

    @Override
    public void tick() {
        if (game != null) {
            game.tick();
        }
        if (closeTimer > 0) {
            closeTimer--;
            if (closeTimer == 0) {
                this.minecraft.setScreen(null);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderGameScreen(graphics, getConsoleX() + SCREEN_X, getConsoleY() + SCREEN_Y);
        renderButtons(graphics);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(BACKGROUND, getConsoleX(), getConsoleY(), 0, 64, 0, CONSOLE_WIDTH, CONSOLE_HEIGHT, 512, 512);
    }

    private void renderGameScreen(GuiGraphics graphics, int x, int y) {
        graphics.enableScissor(x, y, x + Game.WIDTH, y + Game.HEIGHT);
        if (!game.isEmpty()) {
            game.render(graphics, x, y);
        }
        graphics.disableScissor();
    }

    private void renderButtons(GuiGraphics graphics) {
        W_BUTTON.render(graphics, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.UP));
        A_BUTTON.render(graphics, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.LEFT));
        D_BUTTON.render(graphics, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.RIGHT));
        S_BUTTON.render(graphics, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.DOWN));
        B1_BUTTON.render(graphics, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.BUTTON1));
        B2_BUTTON.render(graphics, getConsoleX(), getConsoleY(), game.controls.isButtonDown(Button.BUTTON2));
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        boolean flag = false;

        if (key == E) {
            game.closeArcadeGame();
            closeTimer = 2;
            return true;
        }
        if (key == 256) {
            game.closeArcadeGame();
            closeTimer = 2;
            return true;
        }
        if (key == 82) {
            game.closeArcadeGame();
            game.prepare();
            flag = true;
            game.soundPlayer.playConfirm();
        }

        switch (key) {
            case W -> {
                game.controls.setButton(Button.UP, true);
                flag = true;
            }
            case S -> {
                game.controls.setButton(Button.DOWN, true);
                flag = true;
            }
            case A -> {
                game.controls.setButton(Button.LEFT, true);
                flag = true;
            }
            case D -> {
                game.controls.setButton(Button.RIGHT, true);
                flag = true;
            }
            case SPACE -> {
                game.controls.setButton(Button.BUTTON1, true);
                flag = true;
            }
            case ENTER -> {
                game.controls.setButton(Button.BUTTON2, true);
                flag = true;
            }
        }

        return super.keyPressed(key, scanCode, modifiers) || flag;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        boolean flag = false;
        switch (keyCode) {
            case W -> {
                game.controls.setButton(Button.UP, false);
                flag = true;
            }
            case S -> {
                game.controls.setButton(Button.DOWN, false);
                flag = true;
            }
            case A -> {
                game.controls.setButton(Button.LEFT, false);
                flag = true;
            }
            case D -> {
                game.controls.setButton(Button.RIGHT, false);
                flag = true;
            }
            case SPACE -> {
                game.controls.setButton(Button.BUTTON1, false);
                flag = true;
            }
            case ENTER -> {
                game.controls.setButton(Button.BUTTON2, false);
                flag = true;
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers) || flag;
    }

    @Override
    public void onClose() {
        if (game != null) {
            game.closeArcadeGame();
        }
        super.onClose();
        game = null;
    }
}
