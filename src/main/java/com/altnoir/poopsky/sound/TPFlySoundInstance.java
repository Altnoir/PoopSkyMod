package com.altnoir.poopsky.sound;

import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class TPFlySoundInstance extends AbstractTickableSoundInstance {
    private final ToiletPlugEntity player;
    private int tickCount;

    public TPFlySoundInstance(ToiletPlugEntity player) {
        super(SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, RandomSource.create());
        this.player = player;
        this.looping = true;
        this.volume = 0.1F;
    }

    @Override
    public void tick() {
        ++this.tickCount;

        if (!this.player.isRemoved() && (this.tickCount <= 20 || this.player.getControllingPassenger() != null)) {
            this.x = this.player.getX();
            this.y = this.player.getY();
            this.z = this.player.getZ();

            float f = (float) this.player.getDeltaMovement().lengthSqr();
            if ((double) f >= 1.0E-7D) {
                this.volume = Mth.clamp(f / 4.0F, 0.0F, 1.0F);
            } else {
                this.volume = 0.0F;
            }

            if (this.tickCount < 20) {
                this.volume = 0.0F;
            } else if (this.tickCount < 40) {
                this.volume *= (this.tickCount - 20) / 20.0F;
            }

            this.pitch = this.volume > 0.8F ? 1.0F + (this.volume - 0.8F) : 1.0F;
        } else {
            this.stop();
        }
    }
}
