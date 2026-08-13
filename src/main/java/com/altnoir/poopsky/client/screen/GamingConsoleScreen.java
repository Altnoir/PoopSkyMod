package com.altnoir.poopsky.client.screen;

import com.altnoir.poopsky.client.ClientUtils;
import com.altnoir.poopsky.client.arcade.ArcadeWorldScreenRenderer;
import com.altnoir.poopsky.client.games.controls.Button;
import com.altnoir.poopsky.client.games.util.Game;
import com.altnoir.poopsky.content.item.p.GameDiscItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GamingConsoleScreen extends Screen {
    private static final int W = 87;
    private static final int S = 83;
    private static final int A = 65;
    private static final int D = 68;
    private static final int E = 69;
    private static final int SPACE = 32;
    private static final int ENTER = 257;

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
        ArcadeWorldScreenRenderer.setGame(arcadeMachinePos, cartridge, arcadeGame);
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
            ArcadeWorldScreenRenderer.clearGame(arcadeMachinePos, game);
        }
        super.onClose();
        game = null;
    }
}
