package com.altnoir.poopsky.game;

import com.altnoir.poopsky.content.item.p.GameDiskItem;
import net.minecraft.sounds.SoundEvent;

import java.util.Random;

public abstract class ServerGame extends Game {
    protected final Random random;
    protected boolean upDown;
    protected boolean downDown;
    protected boolean leftDown;
    protected boolean rightDown;
    protected boolean button1Down;
    protected boolean button2Down;
    private SoundEmitter soundEmitter = (event, pitch, volume) -> {
    };
    private GameDefinition gameDefinition;

    protected ServerGame() {
        this.random = new Random();
    }

    void setGameDefinition(GameDefinition gameDefinition) {
        this.gameDefinition = gameDefinition;
    }

    public void setSoundEmitter(SoundEmitter soundEmitter) {
        this.soundEmitter = soundEmitter;
    }

    protected void playSound(SoundEvent event, float pitch, float volume) {
        soundEmitter.play(event, pitch, volume);
    }

    public final void setButton(Button button, boolean pressed) {
        switch (button) {
            case UP -> upDown = pressed;
            case DOWN -> downDown = pressed;
            case LEFT -> leftDown = pressed;
            case RIGHT -> rightDown = pressed;
            case BUTTON1 -> button1Down = pressed;
            case BUTTON2 -> button2Down = pressed;
        }

        if (pressed) {
            buttonDown(button);
        } else {
            buttonUp(button);
        }
    }

    protected void buttonDown(Button button) {
        if (stage == GameStage.START) {
            start();
        } else if (stage == GameStage.DIED || stage == GameStage.WON) {
            prepare();
        }
    }

    protected void buttonUp(Button button) {
    }

    public final void tick() {
        if (stage == GameStage.PLAYING && ticks % gameTickDuration() == 0) {
            gameTick();
        }
        extraTick();
        ticks++;
    }

    protected int gameTickDuration() {
        return 1;
    }

    protected abstract void gameTick();

    protected void extraTick() {
    }

    public String getGameName() {
        return gameDefinition != null ? gameDefinition.gameName() : getClass().getSimpleName();
    }

    public static ServerGame create(GameDiskItem disc) {
        GameDefinition definition = GameDefinitions.byDiscItem(disc);
        ServerGame game = definition.newServerGame();
        game.prepare();
        return game;
    }

    @FunctionalInterface
    public interface SoundEmitter {
        void play(SoundEvent event, float pitch, float volume);
    }
}
