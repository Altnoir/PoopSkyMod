package com.altnoir.poopsky.entity;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.p.ChairEntity;
import com.altnoir.poopsky.entity.p.FlyEntity;
import com.altnoir.poopsky.entity.p.PoolimeEntity;
import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PSEntityType {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, PoopSky.MOD_ID);

    public static Supplier<EntityType<ToiletPlugEntity>> TOILET_PLUG = ENTITY_TYPES.register("toilet_plug", () ->
            EntityType.Builder.of(ToiletPlugEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .sized(0.75F, 0.35F)
                    .build("toilet_plug"));

    public static Supplier<EntityType<PoolimeEntity>> POOLIME = ENTITY_TYPES.register("poolime", () ->
            EntityType.Builder.of(PoolimeEntity::new, MobCategory.MONSTER)
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F)
                    .clientTrackingRange(10)
                    .build("pooplime"));

    public static Supplier<EntityType<FlyEntity>> FLY = ENTITY_TYPES.register("fly", () ->
            EntityType.Builder.of(FlyEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 0.6F)
                    .eyeHeight(0.3F)
                    .clientTrackingRange(8)
                    .build("fly"));

    public static Supplier<EntityType<ChairEntity>> STOOL = ENTITY_TYPES.register("stool_entity", () ->
            EntityType.Builder.of(ChairEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("stool_entity"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}