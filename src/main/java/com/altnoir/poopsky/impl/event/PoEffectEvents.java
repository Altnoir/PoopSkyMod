package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.effect.InfestationEffect;
import com.altnoir.poopsky.init.PoEffects;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PoopSky.MOD_ID)
public final class PoEffectEvents {
    @SubscribeEvent
    public static void hurt(LivingDamageEvent event) {
        var entity = event.getEntity();
        var effect = entity.getEffect(PoEffects.INFESTATION.get());
        if (effect != null && event.getAmount() > 0)
            ((InfestationEffect) effect.getEffect()).onMobHurt(entity, effect.getAmplifier(), event.getSource(), event.getAmount());
    }
}
