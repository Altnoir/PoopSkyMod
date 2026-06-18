package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.tag.PDamageTypeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyArg(
            method = "actuallyHurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"),
            index = 1
    )
    private float poopsky$applyBleedingDamage(DamageSource damageSource, float amount) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(PEffects.BLEEDING) && !damageSource.is(PDamageTypeTags.BYPASSES_BLEEDING)) {
            float amplifier = (self.getEffect(PEffects.BLEEDING).getAmplifier() + 1) * 0.1F;
            return amount * (1 + amplifier);
        }
        return amount;
    }
}
