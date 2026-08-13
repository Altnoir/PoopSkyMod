package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoFluids;
import com.altnoir.poopsky.init.PoParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract BlockPos blockPosition();

    @Redirect(
            method = "doWaterSplashEffect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
            )
    )
    private void poopsky$replaceUrineBubble(
            Level level,
            ParticleOptions particle,
            double x,
            double y,
            double z,
            double velocityX,
            double velocityY,
            double velocityZ
    ) {
        if (particle == ParticleTypes.BUBBLE && poopsky$isInUrine(level)) {
            particle = PoParticles.POOP_PARTICLE.get();
        }
        level.addParticle(particle, x, y, z, velocityX, velocityY, velocityZ);
    }

    private boolean poopsky$isInUrine(Level level) {
        BlockPos pos = blockPosition();
        return level.getFluidState(pos).is(PoFluids.URINE.get())
                || level.getFluidState(pos).is(PoFluids.FLOWING_URINE.get())
                || level.getFluidState(pos.below()).is(PoFluids.URINE.get())
                || level.getFluidState(pos.below()).is(PoFluids.FLOWING_URINE.get());
    }
}
