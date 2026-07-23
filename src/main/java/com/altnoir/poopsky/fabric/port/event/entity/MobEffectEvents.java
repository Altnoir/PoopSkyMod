package com.altnoir.poopsky.fabric.port.event.entity;

import com.altnoir.poopsky.fabric.port.util.EffectApplicableResult;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class MobEffectEvents {

    public static final Event<Applicable> APPLICABLE = EventFactory.createArrayBacked(Applicable.class, listeners -> (living, effectInstance) -> {
        for (Applicable event : listeners) {
            EffectApplicableResult result = event.canApply(living, effectInstance);

            if (result == EffectApplicableResult.APPLY) {
                return result;
            }
        }
        return EffectApplicableResult.DEFAULT;
    });

    @FunctionalInterface
    public interface Applicable {
        EffectApplicableResult canApply(LivingEntity living, MobEffectInstance effectInstance);
    }
}
