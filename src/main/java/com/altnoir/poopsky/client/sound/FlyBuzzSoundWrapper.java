package com.altnoir.poopsky.client.sound;

import com.altnoir.poopsky.content.entity.p.FlyEntity;
import net.minecraft.client.Minecraft;

public class FlyBuzzSoundWrapper {
    private final FlyEntity fly;
    private FlyBuzzSoundInstance sound;

    public FlyBuzzSoundWrapper(FlyEntity fly) {
        this.fly = fly;
    }

    public void tick() {
        if (!fly.level().isClientSide()) {
            return;
        }

        if (sound == null || sound.isStopped()) {
            sound = new FlyBuzzSoundInstance(fly);
            Minecraft.getInstance().getSoundManager().play(sound);
        } else {
            sound.tick();
        }
    }

    public void stop() {
        if (fly.level().isClientSide() && sound != null) {
            Minecraft.getInstance().getSoundManager().stop(sound);
            sound = null;
        }
    }
}
