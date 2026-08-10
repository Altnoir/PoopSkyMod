package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;

import com.altnoir.poopsky.content.entity.p.*;
import com.altnoir.poopsky.content.entity.renderer.*;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
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

    public static final EntityEntry<FlushToiletEntity> FLUSH_TOILET = REGISTRATE.entity("flush_toilet_entity", FlushToiletEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.5F, 0.5F))
            .renderer(() -> FlushToiletRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<FlushToiletCartEntity> FLUSH_TOILET_CART = REGISTRATE
            .entity("flush_toilet_cart", FlushToiletCartEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.75F, 0.5F)
                    .eyeHeight(0.375F)
                    .clientTrackingRange(10))
            .renderer(() -> FlushToiletCartRenderer::new)
            .lang("Flush Toilet Cart")
            .register();
    public static final EntityEntry<FlushToiletCartEntity> GOLDEN_FLUSH_TOILET_CART = REGISTRATE
            .entity("golden_flush_toilet_cart", FlushToiletCartEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.75F, 0.5F)
                    .eyeHeight(0.375F)
                    .clientTrackingRange(10))
            .renderer(() -> FlushToiletCartRenderer::new)
            .lang("Golden Flush Toilet Cart")
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

    public static final EntityEntry<GachaBallEntity> GACHA_BALL = REGISTRATE
            .<GachaBallEntity>entity("gacha_ball", GachaBallEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10))
            .renderer(() -> ThrownItemRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<GinkgoBoatEntity> GINKGO_BOAT = REGISTRATE
            .entity("ginkgo_boat", GinkgoBoatEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .clientTrackingRange(10))
            .renderer(() -> GinkgoBoatRenderer.provider(false))
            .lang("Ginkgo Boat")
            .register();

    public static final EntityEntry<GinkgoChestBoatEntity> GINKGO_CHEST_BOAT = REGISTRATE
            .entity("ginkgo_chest_boat", GinkgoChestBoatEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .clientTrackingRange(10))
            .renderer(() -> GinkgoBoatRenderer.provider(true))
            .lang("Ginkgo Boat with Chest")
            .register();

    public static void register() {
    }
}
