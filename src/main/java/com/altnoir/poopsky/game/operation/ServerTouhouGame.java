package com.altnoir.poopsky.game.operation;

import com.altnoir.poopsky.game.Button;
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
    protected void prepareState() {
        state.prepare(random);
    }

    @Override
    protected void gameTick() {
        TouhouGameState.TickResult result = state.tick(
                isDown(Button.UP),
                isDown(Button.DOWN),
                isDown(Button.LEFT),
                isDown(Button.RIGHT),
                isDown(Button.BUTTON1),
                isDown(Button.BUTTON2),
                random
        );

        score = state.getScore();

        if (result.shot()) {
            playSound(PoSoundEvents.SHOOT.get(), 1.0F, 0.5F);
        }
        if (result.bossHit()) {
            playSound(PoSoundEvents.ENTITY_POOLIME_HURT.get(), 1.2F, 0.5F);
        }
        if (result.bossKilled()) {
            playSound(PoSoundEvents.EXPLOSION.get(), 1.0F, 0.5F);
        }
        if (result.powerUpPickup()) {
            playSound(SoundEvents.ITEM_PICKUP, 1.2F, 0.6F);
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
