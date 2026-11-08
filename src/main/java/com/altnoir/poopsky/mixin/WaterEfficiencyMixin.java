package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoAttributes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LivingEntity.class)
public abstract class WaterEfficiencyMixin {
    // 1.20 divides the local by 3; 1.21 uses a 0..1 attribute directly.
    // Change the float local rather than the integer enchantment result to preserve fractions.
    @ModifyVariable(method = "travel", at = @At(value = "STORE", ordinal = 0), index = 11,
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDepthStrider(Lnet/minecraft/world/entity/LivingEntity;)I")))
    private float poopsky$waterEfficiency(float depthStrider) {
        LivingEntity entity = (LivingEntity) (Object) this;
        double efficiency = entity.getAttributes().hasAttribute(PoAttributes.WATER_MOVEMENT_EFFICIENCY.get()) ? entity.getAttributeValue(PoAttributes.WATER_MOVEMENT_EFFICIENCY.get()) : 0;
        return (float) Math.min(3, depthStrider + efficiency * 3);
    }
}
