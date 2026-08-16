package com.altnoir.poopsky.game;

import net.minecraft.nbt.CompoundTag;

public abstract class Game {
    public static final int WIDTH = 224;
    public static final int HEIGHT = 160;

    protected GameStage stage = GameStage.START;
    protected int score;
    protected int ticks = 1;

    public GameStage getStage() {
        return stage;
    }

    public int getScore() {
        return score;
    }

    public int getTicks() {
        return ticks;
    }

    public void prepare() {
        score = 0;
        stage = GameStage.START;
        ticks = 1;
    }

    public void start() {
        stage = GameStage.PLAYING;
        ticks = 1;
    }

    public CompoundTag writeSnapshot() {
        CompoundTag tag = new CompoundTag();
        tag.putString("stage", stage.name());
        tag.putInt("score", score);
        tag.putInt("ticks", ticks);
        return tag;
    }

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
    }
}
