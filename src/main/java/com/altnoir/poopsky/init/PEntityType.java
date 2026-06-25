package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.p.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PEntityType {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, PoopSky.MOD_ID);

    public static final Supplier<EntityType<ToiletPlugEntity>> TOILET_PLUG = ENTITY_TYPES.register("toilet_plug", () ->
            EntityType.Builder.of(ToiletPlugEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .sized(0.75F, 0.35F)
                    .build("toilet_plug"));

    public static final Supplier<EntityType<PoolimeEntity>> POOLIME = ENTITY_TYPES.register("poolime", () ->
            EntityType.Builder.of(PoolimeEntity::new, MobCategory.MONSTER)
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F)
                    .clientTrackingRange(10)
                    .build("pooplime"));

    public static final Supplier<EntityType<FlyEntity>> FLY = ENTITY_TYPES.register("fly", () ->
            EntityType.Builder.of(FlyEntity::new, MobCategory.CREATURE)
                    .sized(0.7F, 0.6F)
                    .eyeHeight(0.3F)
                    .clientTrackingRange(8)
                    .build("fly"));

    public static final Supplier<EntityType<ChairEntity>> STOOL = ENTITY_TYPES.register("stool_entity", () ->
            EntityType.Builder.of(ChairEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("stool_entity"));

    public static final Supplier<EntityType<ToiletEntity>> TOILET = ENTITY_TYPES.register("toilet_entity", () ->
            EntityType.Builder.of(ToiletEntity::new, MobCategory.MISC)
                    .sized(0.75F, 1.0F)
                    .build("toilet_entity"));

    public static final DeferredHolder<EntityType<?>, EntityType<PoopTntEntity>> POOP_TNT = ENTITY_TYPES.register("poop_tnt", () ->
            EntityType.Builder.<PoopTntEntity>of(PoopTntEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .eyeHeight(0.15F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .fireImmune()
                    .build("poop_tnt"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
