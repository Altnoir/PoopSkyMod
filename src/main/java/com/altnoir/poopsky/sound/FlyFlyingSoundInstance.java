package com.altnoir.poopsky.sound;

import com.altnoir.poopsky.entity.p.FlyEntity;
import com.altnoir.poopsky.init.PSoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.BeeSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlyFlyingSoundInstance extends BeeSoundInstance {
    public FlyFlyingSoundInstance(FlyEntity fly) {
        super(fly, PSoundEvents.ENTITY_FLY_LOOP.get(), SoundSource.NEUTRAL);
    }

    @Override
    protected AbstractTickableSoundInstance getAlternativeSoundInstance() {
        return new FlyAggressiveSoundInstance((FlyEntity) this.bee);
    }

    @Override
    protected boolean shouldSwitchSounds() {
        return this.bee.isAngry();
    }
}
