package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.common.item.p.TimeBellItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void poopsky$ignoreProjectiles(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (poopSky$hasOmener(self) && source.is(DamageTypeTags.IS_PROJECTILE)) {
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
        if (self.hasEffect(PEffects.BLEEDING) && !damageSource.is(PTags.DamageTypes.BYPASSES_BLEEDING)) {
            float amplifier = (self.getEffect(PEffects.BLEEDING).getAmplifier() + 1) * 0.1F;
            return amount * (1 + amplifier);
        }
        if (poopSky$hasOmener(self)) {
            return amount * 2.0F;
        }
        return amount;
    }

    @Inject(method = "hurt", at = @At("TAIL"))
    private void poopsky$removeInvulnerabilityDuringTimeFreeze(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            Entity self = (Entity) (Object) this;
            if (!self.level().isClientSide && TimeBellItem.isTimeBellFreeze()) {
                self.invulnerableTime = 0;
            }
        }
    }

    @Unique
    private boolean poopSky$hasOmener(LivingEntity self) {
        return self.hasEffect(PEffects.OMENER) && self.getEffect(PEffects.OMENER).getAmplifier() >= 1;
    }
}