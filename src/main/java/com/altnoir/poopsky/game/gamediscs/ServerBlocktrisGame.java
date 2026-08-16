package com.altnoir.poopsky.game.gamediscs;

import com.altnoir.poopsky.game.Button;
import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.game.model.BlocktrisGameState;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;

public class ServerBlocktrisGame extends ServerGame {
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
                state.moveCurrent(-1, 0);
                playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
                state.setPlacementCooldown(10);
            }
            case RIGHT -> {
                state.moveCurrent(1, 0);
                playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
                state.setPlacementCooldown(10);
            }
            case DOWN -> {
                if (state.moveCurrent(0, 1)) {
                    place();
                } else {
                    playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
                    state.setPlacementCooldown(10);
                }
            }
            case BUTTON1 -> {
                state.hardDropCurrent();
                playSound(PoSoundEvents.EXPLOSION.get(), 0.7F, 0.5F);
                state.setPlacementCooldown(0);
                place();
                state.setPlacementCooldown(10);
            }
            case BUTTON2 -> {
                state.rotateCurrent();
                playSound(PoSoundEvents.SWING.get(), 1.5F, 0.5F);
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
        return (int) (10f / ((float) score / 50f + 1f));
    }

    @Override
    protected void extraTick() {
        state.tickPlacementCooldown();
        if (stage != GameStage.PLAYING || ticks % 2 != 0 || state.getPlacementCooldown() > 0) {
            return;
        }
        if (leftDown) {
            state.moveCurrent(-1, 0);
            playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
        }
        if (rightDown) {
            state.moveCurrent(1, 0);
            playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
        }
        if (downDown && state.moveCurrent(0, 1)) {
            playSound(PoSoundEvents.SHOOT.get(), 2.5F, 0.1F);
            place();
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
