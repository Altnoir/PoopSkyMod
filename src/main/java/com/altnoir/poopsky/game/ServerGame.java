package com.altnoir.poopsky.game;

import com.altnoir.poopsky.content.item.p.GameDiscItem;
import com.altnoir.poopsky.game.client.controls.Button;
import com.altnoir.poopsky.game.client.util.GameStage;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;

import java.util.Random;

public abstract class ServerGame {
    protected static final int GAME_WIDTH = 224;
    protected static final int GAME_HEIGHT = 160;

    public GameStage stage = GameStage.START;
    public int score;
    public int settledScore;
    public int ticks = 1;
    protected final Random random;
    protected boolean upDown;
    protected boolean downDown;
    protected boolean leftDown;
    protected boolean rightDown;
    protected boolean button1Down;
    protected boolean button2Down;
    private SoundEmitter soundEmitter = (event, pitch, volume) -> {
    };

    protected ServerGame() {
        this.random = new Random();
    }

    public void setSoundEmitter(SoundEmitter soundEmitter) {
        this.soundEmitter = soundEmitter;
    }

    protected void playSound(SoundEvent event, float pitch, float volume) {
        soundEmitter.play(event, pitch, volume);
    }

    public abstract void prepare();

    public void start() {
        stage = GameStage.PLAYING;
        ticks = 1;
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
        if (stage == GameStage.START || stage == GameStage.RETRY) {
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

    public CompoundTag writeSnapshot() {
        CompoundTag tag = new CompoundTag();
        tag.putString("stage", stage.name());
        tag.putInt("score", score);
        tag.putInt("settled_score", settledScore);
        tag.putInt("ticks", ticks);
        return tag;
    }

    public String getGameName() {
        if (this instanceof ServerPongGame) {
            return "PongGame";
        }
        if (this instanceof ServerRoundwormGame) {
            return "SlimeGame";
        }
        if (this instanceof ServerFlappyBirdGame) {
            return "FlappyBirdGame";
        }
        if (this instanceof ServerBlocktrisGame) {
            return "BlocktrisGame";
        }
        return getClass().getSimpleName();
    }

    public static ServerGame create(GameDiscItem disc) {
        ServerGame game;
        if (disc == PoItems.GAME_DISC_FLAPPY_BIRD.get()) {
            game = new ServerFlappyBirdGame();
        } else if (disc == PoItems.GAME_DISC_SLIME.get()) {
            game = new ServerRoundwormGame();
        } else if (disc == PoItems.GAME_DISC_BLOCKTRIS.get()) {
            game = new ServerBlocktrisGame();
        } else if (disc == PoItems.GAME_DISC_PONG.get()) {
            game = new ServerPongGame();
        } else {
            throw new IllegalArgumentException("Unknown arcade game " + disc);
        }
        game.prepare();
        return game;
    }

    @FunctionalInterface
    public interface SoundEmitter {
        void play(SoundEvent event, float pitch, float volume);
    }
}