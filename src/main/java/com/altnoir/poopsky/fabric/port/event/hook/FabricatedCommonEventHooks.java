package com.altnoir.poopsky.fabric.port.event.hook;

import com.altnoir.poopsky.fabric.port.event.entity.MobEffectEvents;
import com.altnoir.poopsky.fabric.port.util.EffectApplicableResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class FabricatedCommonEventHooks {
    public static boolean canMobEffectBeApplied(LivingEntity entity, MobEffectInstance effect) {
        EffectApplicableResult result = MobEffectEvents.APPLICABLE.invoker().canApply(entity, effect);
        if (result == EffectApplicableResult.APPLY) {
            return true;
        }
        return result == EffectApplicableResult.DEFAULT && entity.canBeAffected(effect);
    }
}
