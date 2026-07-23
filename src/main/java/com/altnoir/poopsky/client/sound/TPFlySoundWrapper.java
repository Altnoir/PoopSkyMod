package com.altnoir.poopsky.client.sound;

import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.api.distmarker.OnlyIn;

@Environment(EnvType.CLIENT)
public class TPFlySoundWrapper {
    private final ToiletPlugEntity attachedInstance;

    private TPFlySoundInstance sound;

    public TPFlySoundWrapper(ToiletPlugEntity attachedInstance) {
        this.attachedInstance = attachedInstance;
    }

    public void stop() {
        if (attachedInstance.level().isClientSide && sound != null) {
            Minecraft.getInstance().getSoundManager().stop(sound);
            sound = null;
        }
    }

    public void play() {
        if (attachedInstance.level().isClientSide && sound == null) {
            sound = new TPFlySoundInstance(attachedInstance);
            Minecraft.getInstance().getSoundManager().play(sound);
        }
    }

    public void tick() {
        if (attachedInstance.level().isClientSide && sound != null) {
            sound.tick();
        }
    }
}
