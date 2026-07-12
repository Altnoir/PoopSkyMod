package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.*;
import com.altnoir.poopsky.content.entity.renderer.*;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.entity.MobCategory;

public class PoEntityType {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final EntityEntry<ToiletPlugEntity> TOILET_PLUG = REGISTRATE.entity("toilet_plug", ToiletPlugEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .fireImmune()
                    .clientTrackingRange(10)
                    .sized(0.75F, 0.35F))
            .renderer(() -> ToiletPlugRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<PoolimeEntity> POOLIME = REGISTRATE.entity("poolime", PoolimeEntity::new, MobCategory.MONSTER)
            .properties(properties -> properties
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F)
                    .clientTrackingRange(10))
            .renderer(() -> PoolimeRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<FlyEntity> FLY = REGISTRATE.entity("fly", FlyEntity::new, MobCategory.CREATURE)
            .properties(properties -> properties
                    .sized(0.5F, 0.6F)
                    .eyeHeight(0.3F)
                    .clientTrackingRange(8))
            .renderer(() -> FlyRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<ChairEntity> STOOL = REGISTRATE.entity("stool_entity", ChairEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.5F, 0.5F))
            .renderer(() -> ChairRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<ToiletEntity> TOILET = REGISTRATE.entity("toilet_entity", ToiletEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.75F, 1.0F))
            .renderer(() -> ToiletRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<PoopTntEntity> POOP_TNT = REGISTRATE.<PoopTntEntity>entity("poop_tnt", PoopTntEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.75F, 0.75F)
                    .eyeHeight(0.15F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .fireImmune())
            .renderer(() -> PoopTntRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static void register() {
    }
}