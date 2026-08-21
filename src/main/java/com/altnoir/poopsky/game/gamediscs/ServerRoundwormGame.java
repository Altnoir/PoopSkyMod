package com.altnoir.poopsky.game.gamediscs;

import com.altnoir.poopsky.game.Button;
import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.game.model.RoundwormGameState;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec2;

public class ServerRoundwormGame extends ServerGame {
    private final RoundwormGameState state = new RoundwormGameState();

    @Override
    public void prepare() {
        super.prepare();
        state.prepare(random);
    }

    @Override
    protected int gameTickDuration() {
        return 5;
    }

    @Override
    protected void buttonDown(Button button) {
        super.buttonDown(button);
        if (stage != GameStage.PLAYING) {
            return;
        }
        Vec2 next = switch (button) {
            case UP -> new Vec2(0, -1);
            case DOWN -> new Vec2(0, 1);
            case LEFT -> new Vec2(-1, 0);
            case RIGHT -> new Vec2(1, 0);
            default -> null;
        };
        if (state.requestDirection(next)) {
            playSound(SoundEvents.SLIME_SQUISH, 0.1F, 0.5F);
        }
    }

    @Override
    protected void gameTick() {
        switch (state.tick(random)) {
            case DIED -> {
                stage = GameStage.DIED;
                playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 2.0F);
            }
            case ATE -> {
                score++;
                playSound(SoundEvents.GENERIC_EAT, 0.8F, 0.8F);
            }
            case MOVED -> {
            }
        }
    }

    @Override
    public CompoundTag writeSnapshot() {
        CompoundTag tag = super.writeSnapshot();
        state.writeSnapshot(tag);
        return tag;
    }
}
