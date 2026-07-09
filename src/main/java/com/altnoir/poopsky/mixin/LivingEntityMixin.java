package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.content.item.p.TimeBellItem;
import com.altnoir.poopsky.init.PEffects;
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
        if (poopSky$hasBleeding(self) && !damageSource.is(PTags.DamageTypes.BYPASSES_BLEEDING)) {
            float amplifier = (self.getEffect(PEffects.BLEEDING).getAmplifier() + 1) * 0.1F;
            finalAmount = finalAmount * (1 + amplifier);
        }
        if (damageSource.getEntity() instanceof LivingEntity attacker && poopSky$hasCurse(attacker)) {
            finalAmount = finalAmount * 2.0F;
        }
        return finalAmount;
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
    private boolean poopSky$hasCurse(LivingEntity self) {
        return self.hasEffect(PEffects.SEEDBED_CURSE);
    }

    @Unique
    private boolean poopSky$hasBleeding(LivingEntity self) {
        return self.hasEffect(PEffects.BLEEDING);
    }
}