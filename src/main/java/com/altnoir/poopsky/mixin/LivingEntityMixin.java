package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.content.item.p.TimeBellItem;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.init.PoEffects;
import com.altnoir.poopsky.init.PoFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Redirect(
            method = "makeDrownParticles",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V")
    )
    private void poopsky$replaceUrineDrownBubble(Level level, ParticleOptions particle, double x, double y, double z,
                                                 double xd, double yd, double zd) {
        if (particle == ParticleTypes.BUBBLE) {
            poopsky$isInUrine((LivingEntity) (Object) this);
        }
        level.addParticle(particle, x, y, z, xd, yd, zd);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void poopsky$ignoreProjectiles(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (poopSky$hasCurse(self) && source.is(DamageTypeTags.IS_PROJECTILE)) {
            cir.setReturnValue(false);
        }
    }

    @ModifyArg(
            method = "actuallyHurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"),
            index = 1
    )
    private float poopsky$applyDamage(DamageSource damageSource, float amount) {
        LivingEntity self = (LivingEntity) (Object) this;
        float finalAmount = amount;
        if (poopSky$hasBleeding(self) && !damageSource.is(PoTags.DamageTypes.BYPASSES_BLEEDING)) {
            float amplifier = (self.getEffect(PoEffects.BLEEDING).getAmplifier() + 1) * 0.1F;
            finalAmount = finalAmount * (1 + amplifier);
        }
        if (damageSource.getEntity() instanceof LivingEntity attacker && poopSky$hasCurse(attacker)) {
            finalAmount = finalAmount * 2.0F;
        }
        return finalAmount;
    }

    @Inject(method = "hurtServer", at = @At("TAIL"))
    private void poopsky$removeInvulnerabilityDuringTimeFreeze(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            Entity self = (Entity) (Object) this;
            if (!self.level().isClientSide() && TimeBellItem.isTimeBellFreeze()) {
                self.invulnerableTime = 0;
            }
        }
    }

    @Unique
    private static boolean poopSky$hasCurse(LivingEntity self) {
        return self.hasEffect(PoEffects.SEEDBED_CURSE);
    }

    @Unique
    private static boolean poopSky$hasBleeding(LivingEntity self) {
        return self.hasEffect(PoEffects.BLEEDING);
    }

    private static boolean poopsky$isInUrine(LivingEntity entity) {
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();
        return level.getFluidState(pos).is(PoFluids.URINE.get())
                || level.getFluidState(pos).is(PoFluids.FLOWING_URINE.get())
                || level.getFluidState(pos.below()).is(PoFluids.URINE.get())
                || level.getFluidState(pos.below()).is(PoFluids.FLOWING_URINE.get());
    }
}
