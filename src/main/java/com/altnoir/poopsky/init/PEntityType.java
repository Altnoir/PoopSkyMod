package com.altnoir.poopsky.init;

import com.altnoir.poopsky.content.entity.p.*;
import com.altnoir.poopsky.content.entity.renderer.*;
import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.stream.Stream;

public class PEntityType {
    public static final EntityEntry<ToiletPlugEntity> TOILET_PLUG = PRegistries.REGISTRATE
            .entity("toilet_plug", ToiletPlugEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .fireImmune()
                    .clientTrackingRange(10)
                    .sized(0.75F, 0.35F))
            .renderer(() -> ToiletPlugRenderer::new)
            .register();

    public static final EntityEntry<PoolimeEntity> POOLIME = PRegistries.REGISTRATE
            .entity("poolime", PoolimeEntity::new, MobCategory.MONSTER)
            .properties(properties -> properties
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F)
                    .clientTrackingRange(10))
            .renderer(() -> PoolimeRenderer::new)
            .register();

    public static final EntityEntry<FlyEntity> FLY = PRegistries.REGISTRATE
            .entity("fly", FlyEntity::new, MobCategory.CREATURE)
            .properties(properties -> properties
                    .sized(0.5F, 0.6F)
                    .eyeHeight(0.3F)
                    .clientTrackingRange(8))
            .renderer(() -> FlyRenderer::new)
            .register();

    public static final EntityEntry<ChairEntity> STOOL = PRegistries.REGISTRATE
            .entity("stool_entity", ChairEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.5F, 0.5F))
            .renderer(() -> ChairRenderer::new)
            .register();

    public static final EntityEntry<ToiletEntity> TOILET = PRegistries.REGISTRATE
            .entity("toilet_entity", ToiletEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.75F, 1.0F))
            .renderer(() -> ToiletRenderer::new)
            .register();

    public static final EntityEntry<PoopTntEntity> POOP_TNT = PRegistries.REGISTRATE
            .<PoopTntEntity>entity("poop_tnt", PoopTntEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.75F, 0.75F)
                    .eyeHeight(0.15F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .fireImmune())
            .renderer(() -> PoopTntRenderer::new)
            .register();

    public static Stream<EntityType<?>> getAllEntityTypes() {
        return PRegistries.REGISTRATE.getAll(Registries.ENTITY_TYPE)
                .stream()
                .map(DeferredHolder::get);
    }

    public static void register(IEventBus eventBus) {
    }
}