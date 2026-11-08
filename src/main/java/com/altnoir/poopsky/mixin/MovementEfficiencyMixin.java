package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MovementEfficiencyMixin {
    @Inject(method = "getBlockSpeedFactor", at = @At("RETURN"), cancellable = true)
    private void poopsky$movementEfficiency(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof LivingEntity living && living.getAttributes().hasAttribute(PoAttributes.MOVEMENT_EFFICIENCY.get())) {
            cir.setReturnValue(Mth.lerp((float) living.getAttributeValue(PoAttributes.MOVEMENT_EFFICIENCY.get()), cir.getReturnValue(), 1));
        }
    }
}
