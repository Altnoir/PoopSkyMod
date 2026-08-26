package com.altnoir.poopsky.game.operation;

import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.game.model.TouhouGameState;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;

public class ServerTouhouGame extends ServerGame {
    private final TouhouGameState state = new TouhouGameState();

    public ServerTouhouGame() {
        state.setBossLootConsumer(this::emitItem);
    }

    @Override
    public void prepare() {
        super.prepare();
        state.prepare(random);
    }

    @Override
    protected void gameTick() {
        TouhouGameState.TickResult result = state.tick(
                upDown,
                downDown,
                leftDown,
                rightDown,
                button1Down,
                button2Down,
                random
        );

        score = state.getScore();

        if (result.shot()) {
            playSound(PoSoundEvents.SHOOT.get(), 1.0F, 0.5F);
        }
        if (result.bossHit()) {
            playSound(SoundEvents.WOOL_HIT, 1.2F, 0.5F);
        }
        if (result.bossKilled()) {
            playSound(PoSoundEvents.EXPLOSION.get(), 1.0F, 0.5F);
        }
        if (result.powerUpPickup()) {
            playSound(PoSoundEvents.CLICK.get(), 1.2F, 0.5F);
        }
        if (result.playerHit()) {
            stage = GameStage.DIED;
            playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 1.0F);
        }
    }

    @Override
    public CompoundTag writeSnapshot() {
        CompoundTag tag = super.writeSnapshot();
        state.writeSnapshot(tag);
        return tag;
    }
}
