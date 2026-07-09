package com.altnoir.poopsky.client.sound;

import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.init.PSoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class FlyBuzzSoundInstance extends AbstractTickableSoundInstance {
    private final FlyEntity fly;

    public FlyBuzzSoundInstance(FlyEntity fly) {
        super(PSoundEvents.ENTITY_FLY_AMBIENT.get(), SoundSource.NEUTRAL, RandomSource.create());
        this.fly = fly;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1F;
        this.pitch = 1.0F;
        this.x = fly.getX();
        this.y = fly.getY();
        this.z = fly.getZ();
    }

    @Override
    public void tick() {
        if (this.fly.isRemoved()) {
            this.stop();
            return;
        }

        this.x = this.fly.getX();
        this.y = this.fly.getY();
        this.z = this.fly.getZ();

        if (this.fly.isAlive()) {
            if (this.fly.isFlying()) {
                this.volume = Mth.lerp(0.3F, this.volume, 1.0F);
                this.pitch = Mth.lerp(0.1F, this.pitch, 1.1F + (float) this.fly.getDeltaMovement().lengthSqr() * 0.1F);
            } else {
                this.volume = Mth.lerp(0.3F, this.volume, 0.15F);
                this.pitch = Mth.lerp(0.1F, this.pitch, 0.9F);
            }
        } else {
            this.volume = Mth.lerp(0.3F, this.volume, 0.0F);
            if (this.volume < 0.01F) {
                this.stop();
            }
        }
    }
}