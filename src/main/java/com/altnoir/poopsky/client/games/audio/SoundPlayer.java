package com.altnoir.poopsky.client.games.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import com.altnoir.poopsky.init.PoSoundEvents;

import java.util.Random;


public class SoundPlayer {
    private final SoundManager manager;
    private final Random random;

    public SoundPlayer() {
        manager = Minecraft.getInstance().getSoundManager();
        random = new Random();
    }

    public void play(SoundEvent event) {
        play(event, 1f);
    }
    public void play(SoundEvent event, float pitch) {
        play(event, pitch, 1f);
    }
    public void play(SoundEvent event, float pitch, float volume) {
        manager.play(SimpleSoundInstance.forUI(event, pitch, volume));
    }
    public void playRandom(SoundEvent event, float minPitch, float maxPitch, float volume) {
        play(event, random.nextFloat(minPitch, maxPitch), volume);
    }
    public void playRandom(SoundEvent event, float minPitch, float maxPitch, float minVolume, float maxVolume) {
        playRandom(event, minPitch, maxPitch, random.nextFloat(minVolume, maxVolume));
    }

    public void playClick(boolean down) {
        playRandom(PoSoundEvents.CLICK.get(), down ? 0.3f : 1f, down ? 0.6f : 1.5f, down ? 0.7f : 0.2f, down ? 0.9f : 0.4f);
    }
    public void playJump() {
        playRandom(PoSoundEvents.JUMP.get(), 0.8f, 1.2f, 0.8f, 1.2f);
    }
    public void playPoint() {
        play(PoSoundEvents.POINT.get(), 1f, 0.7f);
    }
    public void playNewBest() {
        play(PoSoundEvents.NEW_BEST.get(), 1.5f, 2f);
    }
    public void playGameOver() {
        play(PoSoundEvents.GAME_OVER.get(), 0.9f, 2f);
    }
    public void playSelect() {
        play(PoSoundEvents.SELECT.get(), 0.5f, 0.5f);
    }
    public void playConfirm() {
        play(PoSoundEvents.CONFIRM.get(), 1f, 0.5f);
    }
}
