package com.altnoir.poopsky.client.games.util;

public enum ParticleLevel {
    RUNNING_GAME,
    GAME,
    OVERLAY;

    public boolean isFor(GameStage stage) {
        return (this == RUNNING_GAME && stage == GameStage.PLAYING) || this == GAME || (this == OVERLAY && stage == GameStage.DIED || stage == GameStage.WON);
    }
}
