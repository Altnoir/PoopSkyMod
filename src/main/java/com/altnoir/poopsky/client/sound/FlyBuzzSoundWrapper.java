package com.altnoir.poopsky.client.sound;

import com.altnoir.poopsky.common.entity.p.FlyEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlyBuzzSoundWrapper {
    private final FlyEntity fly;
    private FlyBuzzSoundInstance sound;

    public FlyBuzzSoundWrapper(FlyEntity fly) {
        this.fly = fly;
    }

    public void tick() {
        if (fly.level().isClientSide) {
            tick0();
        }
    }

    public void stop() {
        if (fly.level().isClientSide) {
            stop0();
        }
    }

    private void tick0() {
        if (sound == null) {
            sound = new FlyBuzzSoundInstance(fly);
            Minecraft.getInstance().getSoundManager().play(sound);
        } else if (sound.isStopped()) {
            sound = new FlyBuzzSoundInstance(fly);
            Minecraft.getInstance().getSoundManager().play(sound);
        } else {
            sound.tick();
        }
    }

    private void stop0() {
        if (sound != null) {
            Minecraft.getInstance().getSoundManager().stop(sound);
            sound = null;
        }
    }
}