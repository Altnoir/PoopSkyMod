package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = PoopSky.MOD_ID)
public final class PoScaleEvents {
    private static final Map<LivingEntity, Float> LAST_SCALE = new WeakHashMap<>();
    public static float scale(LivingEntity entity) {
        if (!PoAttributes.SCALE.isPresent() || entity.getAttributes() == null || !entity.getAttributes().hasAttribute(PoAttributes.SCALE.get())) return 1;
        return (float) entity.getAttributeValue(PoAttributes.SCALE.get());
    }
    @SubscribeEvent public static void size(EntityEvent.Size event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            float scale = scale(entity);
            if (scale != 1) { event.setNewSize(event.getNewSize().scale(scale)); event.setNewEyeHeight(event.getNewEyeHeight() * scale); }
        }
    }
    @SubscribeEvent public static void tick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        float scale = scale(entity);
        Float old;
        synchronized (LAST_SCALE) { old = LAST_SCALE.put(entity, scale); }
        if ((old == null && scale != 1) || (old != null && old != scale)) entity.refreshDimensions();
    }
}
