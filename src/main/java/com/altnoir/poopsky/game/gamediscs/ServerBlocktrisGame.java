package com.altnoir.poopsky.game.gamediscs;

import com.altnoir.poopsky.game.Button;
import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.game.model.BlocktrisGameState;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;

public class ServerBlocktrisGame extends ServerGame {
    private static final int MOVE_DELAY = 4;
    private static final int MOVE_REPEAT = 2;

    private final BlocktrisGameState state = new BlocktrisGameState();

    @Override
    public void prepare() {
        super.prepare();
        state.prepare(random);
    }

    @Override
    public void start() {
        super.start();
        state.start(random);
    }

    @Override
    protected void buttonDown(Button button) {
        super.buttonDown(button);
        if (stage != GameStage.PLAYING) {
            return;
        }

        switch (button) {
            case LEFT -> {
                moveHorizontal(-1);
                state.setPlacementCooldown(MOVE_DELAY);
            }
            case RIGHT -> {
                moveHorizontal(1);
                state.setPlacementCooldown(MOVE_DELAY);
            }
            case DOWN -> {
                if (state.moveCurrent(0, 1)) {
                    place();
                }
            }
            case BUTTON1 -> {
                state.hardDropCurrent();
                playSound(PoSoundEvents.EXPLOSION.get(), 0.7F, 0.5F);
                place();
                state.setPlacementCooldown(MOVE_DELAY);
            }
            case UP, BUTTON2 -> {
                if (state.rotateCurrent()) {
                    playSound(PoSoundEvents.SWING.get(), 1.5F, 0.5F);
                }
            }
            default -> {
            }
        }
    }

    @Override
    protected void gameTick() {
        if (state.moveCurrent(0, 1)) {
            place();
        }
    }

    @Override
    protected int gameTickDuration() {
        return Math.max(2, 10 - score / 10);
    }

    @Override
    protected void extraTick() {
        if (stage != GameStage.PLAYING) {
            return;
        }

        if (downDown && state.moveCurrent(0, 1)) {
            place();
            return;
        }

        state.tickPlacementCooldown();
        if (ticks % MOVE_REPEAT != 0 || state.getPlacementCooldown() > 0 || leftDown == rightDown) {
            return;
        }

        moveHorizontal(leftDown ? -1 : 1);
    }

    private void moveHorizontal(int direction) {
        if (!state.moveCurrent(direction, 0)) {
            playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
        }
    }

    private void place() {
        BlocktrisGameState.PlacementResult result = state.placeCurrent(random);
        for (int i = 0; i < result.linesCleared(); i++) {
            score++;
            playSound(PoSoundEvents.POINT.get(), 1.0F, 0.7F);
        }
        if (result.died()) {
            stage = GameStage.DIED;
            playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
        }
    }

    @Override
    public CompoundTag writeSnapshot() {
        CompoundTag tag = super.writeSnapshot();
        state.writeSnapshot(tag);
        return tag;
    }
}
