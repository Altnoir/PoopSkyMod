package com.altnoir.poopsky.game.operation;

import com.altnoir.poopsky.game.Button;
import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.game.model.RhythmGameState;
import com.altnoir.poopsky.game.rhythm.RhythmSong;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;

import java.util.List;

public class ServerRhythmGame extends ServerGame {
    private final RhythmGameState state = new RhythmGameState();

    @Override
    protected void prepareState() {
        state.prepare(random);
    }

    @Override
    protected void buttonDown(Button button) {
        super.buttonDown(button);
        if (stage != GameStage.PLAYING) {
            return;
        }
        int lane = laneFor(button);
        if (lane < 0) {
            return;
        }
        // 命中（含同轨道同拍和弦）时，逐个播放每个音符的全部音色（叠加音色同时响）
        List<RhythmSong.Note> hits = state.pressLane(lane);
        if (!hits.isEmpty()) {
            score = state.getRewardPoints();
            for (RhythmSong.Note hit : hits) {
                for (SoundEvent instrument : hit.instruments()) {
                    playSound(instrument, notePitch(hit.pitch()), 1.0F);
                }
            }
        }
    }

    @Override
    protected void gameTick() {
        state.tick();
        score = state.getRewardPoints();
        if (state.isDead() && stage == GameStage.PLAYING) {
            stage = GameStage.DIED;
            playSound(PoSoundEvents.GAME_OVER.get(), 0.9F, 1.0F);
        } else if (state.isSongFinished() && stage == GameStage.PLAYING) {
            stage = GameStage.WON;
            playSound(PoSoundEvents.NEW_BEST.get(), 1.5F, 1.0F);
        }
    }

    /** 音符盒音高换算（与原版音符盒一致）：pitch = 2^((note-12)/12)，note 0-24。 */
    private static float notePitch(int note) {
        return (float) Math.pow(2.0, (note - 12) / 12.0);
    }

    @Override
    public CompoundTag writeSnapshot() {
        CompoundTag tag = super.writeSnapshot();
        state.writeSnapshot(tag);
        return tag;
    }

    private static int laneFor(Button button) {
        return switch (button) {
            case LEFT -> 0;
            case UP -> 1;
            case DOWN -> 2;
            case RIGHT -> 3;
            default -> -1;
        };
    }
}
