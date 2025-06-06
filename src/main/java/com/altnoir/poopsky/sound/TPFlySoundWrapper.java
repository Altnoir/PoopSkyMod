package com.altnoir.poopsky.sound;

import com.altnoir.poopsky.entity.p.PlugEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TPFlySoundWrapper {
    private final PlugEntity attachedInstance;

    private TPFlySound sound;

    public TPFlySoundWrapper(PlugEntity attachedInstance) {
        this.attachedInstance = attachedInstance;
    }

    public void stop() {
        if (attachedInstance.level().isClientSide) {
            stop0();
        }
    }

    public void play() {
        if (attachedInstance.level().isClientSide) {
            play0();
        }
    }

    public void tick() {
        if (attachedInstance.level().isClientSide) {
            tick0();
        }
    }

    private void stop0() {
        if (sound != null) {
            Minecraft.getInstance().getSoundManager().stop(sound);
            sound = null;
        }
    }

    private void play0() {
        if (sound == null) {
            sound = new TPFlySound(attachedInstance);
            Minecraft.getInstance().getSoundManager().play(sound);
        }
    }

    private void tick0() {
        if (sound != null) {
            sound.tick(); // You might also want to check `sound.isStopped()`
        }
    }
}