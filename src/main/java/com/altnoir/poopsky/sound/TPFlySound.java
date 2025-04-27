package com.altnoir.poopsky.sound;

import com.altnoir.poopsky.entity.ToiletPlugEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class TPFlySound extends MovingSoundInstance {
    private final ToiletPlugEntity player;
    private int tickCount;

    public TPFlySound(ToiletPlugEntity player) {
        super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1F;
    }

    public void tick() {
        ++this.tickCount;
        if (!this.player.isRemoved() && (this.tickCount <= 20 || this.player.hasPassengers())) {
            this.x = (double)((float)this.player.getX());
            this.y = (double)((float)this.player.getY());
            this.z = (double)((float)this.player.getZ());
            float f = (float)this.player.getVelocity().lengthSquared();
            if ((double)f >= 1.0E-7D) {
                this.volume = MathHelper.clamp(f / 4.0F, 0.0F, 1.0F);
            } else {
                this.volume = 0.0F;
            }

            if (this.tickCount < 20) {
                this.volume = 0.0F;
            } else if (this.tickCount < 40) {
                this.volume = (float)((double)this.volume * ((double)(this.tickCount - 20) / 20.0D));
            }
            if (this.volume > 0.8F) {
                this.pitch = 1.0F + (this.volume - 0.8F);
            } else {
                this.pitch = 1.0F;
            }
        } else {
            this.setDone();
        }
    }
}
