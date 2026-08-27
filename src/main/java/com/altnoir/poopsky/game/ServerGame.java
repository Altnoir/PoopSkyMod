package com.altnoir.poopsky.game;

import com.altnoir.poopsky.content.item.p.GameDiskItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Random;

public abstract class ServerGame extends Game {
    private static final int RESULT_INPUT_LOCK_TICKS = 20;

    private final EnumSet<Button> buttonsDown = EnumSet.noneOf(Button.class);
    protected final Random random = new Random();
    private SoundEmitter soundEmitter = (event, pitch, volume) -> {
    };
    private ItemEmitter itemEmitter = stack -> {
    };
    private GameDefinition gameDefinition;
    private int resultInputLockTicks;

    protected ServerGame() {
    }

    public static ServerGame create(GameDiskItem disc) {
        GameDefinition definition = GameDefinitions.byDiscItem(disc);
        ServerGame game = definition.newServerGame();
        game.prepare();
        return game;
    }

    void setGameDefinition(GameDefinition gameDefinition) {
        this.gameDefinition = gameDefinition;
    }

    @Override
    public final void prepare() {
        clearButtons();
        resultInputLockTicks = 0;
        super.prepare();
        prepareState();
    }

    protected void prepareState() {
    }

    @Override
    public final void start() {
        resultInputLockTicks = 0;
        super.start();
        startState();
    }

    protected void startState() {
    }

    public String getGameName() {
        return gameDefinition != null ? gameDefinition.gameName() : getClass().getSimpleName();
    }

    public boolean isResultInputLocked() {
        return resultInputLockTicks > 0;
    }

    public void setSoundEmitter(SoundEmitter soundEmitter) {
        this.soundEmitter = soundEmitter;
    }

    protected void playSound(SoundEvent event, float pitch, float volume) {
        soundEmitter.play(event, pitch, volume);
    }

    public void setItemEmitter(ItemEmitter itemEmitter) {
        this.itemEmitter = itemEmitter;
    }

    protected void emitItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            itemEmitter.emit(stack);
        }
    }

    protected boolean isDown(Button button) {
        return buttonsDown.contains(button);
    }

    public final void setButton(Button button, boolean pressed) {
        if (isResultInputLocked()) {
            return;
        }

        GameStage previousStage = stage;
        if (pressed) {
            buttonsDown.add(button);
            buttonDown(button);
        } else {
            buttonsDown.remove(button);
            buttonUp(button);
        }
        lockResultInputIfFinished(previousStage);
    }

    protected void buttonDown(Button button) {
        if (stage == GameStage.START) {
            start();
        } else if (isFinishedStage(stage)) {
            prepare();
        }
    }

    protected void buttonUp(Button button) {
    }

    public final void tick() {
        GameStage previousStage = stage;
        if (stage == GameStage.PLAYING && ticks % gameTickDuration() == 0) {
            gameTick();
        }
        extraTick();
        if (resultInputLockTicks > 0) {
            resultInputLockTicks--;
        }
        lockResultInputIfFinished(previousStage);
        ticks++;
    }

    protected int gameTickDuration() {
        return 1;
    }

    protected abstract void gameTick();

    protected void extraTick() {
    }

    private void lockResultInputIfFinished(GameStage previousStage) {
        if (previousStage == GameStage.PLAYING && isFinishedStage(stage)) {
            clearButtons();
            resultInputLockTicks = RESULT_INPUT_LOCK_TICKS;
        }
    }

    private void clearButtons() {
        buttonsDown.clear();
    }

    private static boolean isFinishedStage(GameStage stage) {
        return stage == GameStage.DIED || stage == GameStage.WON;
    }

    @FunctionalInterface
    public interface SoundEmitter {
        void play(SoundEvent event, float pitch, float volume);
    }

    @FunctionalInterface
    public interface ItemEmitter {
        void emit(ItemStack stack);
    }
}
