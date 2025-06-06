package com.altnoir.poopsky.entity;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PSEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, PoopSky.MOD_ID);

    public static Supplier<EntityType<ToiletPlugEntity>> TOILET_PLUG = ENTITY_TYPES.register("toilet_plug", () ->
            EntityType.Builder.of(ToiletPlugEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .sized(0.75F, 0.35F).build("toilet_plug"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
