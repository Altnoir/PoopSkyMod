package com.altnoir.poopsky.game.operation;

import com.altnoir.poopsky.game.Button;
import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.game.model.PongGameState;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;

public class ServerPongGame extends ServerGame {
    private final PongGameState state = new PongGameState();

    @Override
    protected void prepareState() {
        state.prepare(random);
    }

    @Override
    protected void gameTick() {
        PongGameState.TickResult result = state.tick(
                isDown(Button.UP),
                isDown(Button.DOWN),
                random
        );
        if (result.wallBounce()) {
            playSound(SoundEvents.BONE_BLOCK_BREAK, 0.5F, 0.8F);
        }
        if (result.playerBounce() || result.opponentBounce()) {
            playSound(SoundEvents.BONE_BLOCK_BREAK, 0.7F, 1.0F);
        }
        if (result.playerScored()) {
            score++;
            playSound(PoSoundEvents.POINT.get(), 1.0F, 0.7F);
            if (score >= 10) {
                stage = GameStage.WON;
                playSound(PoSoundEvents.NEW_BEST.get(), 1.5F, 2.0F);
            }
        }
        if (result.opponentScored() && state.getOpponentScore() >= 10) {
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
