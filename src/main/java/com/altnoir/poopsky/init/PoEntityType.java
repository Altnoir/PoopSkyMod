package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.*;
import com.altnoir.poopsky.content.entity.renderer.*;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.world.entity.MobCategory;

public class PoEntityType {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final EntityEntry<ToiletPlugEntity> TOILET_PLUG = REGISTRATE
            .entity("toilet_plug", ToiletPlugEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .fireImmune()
                    .clientTrackingRange(10)
                    .sized(0.75F, 0.35F))
            .renderer(() -> ToiletPlugRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<PoolimeEntity> POOLIME = REGISTRATE
            .entity("poolime", PoolimeEntity::new, MobCategory.MONSTER)
            .properties(properties -> properties
                    .sized(0.52F, 0.52F)
                    .eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F)
                    .clientTrackingRange(10))
            .renderer(() -> PoolimeRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<FlyEntity> FLY = REGISTRATE
            .entity("fly", FlyEntity::new, MobCategory.CREATURE)
            .properties(properties -> properties
                    .sized(0.5F, 0.6F)
                    .eyeHeight(0.3F)
                    .clientTrackingRange(8))
            .renderer(() -> FlyRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<BasiliskEntity> BASILISK = REGISTRATE
            .entity("basilisk", BasiliskEntity::new, MobCategory.MONSTER)
            .properties(properties -> properties
                    .sized(0.9F, 0.7F)
                    .eyeHeight(0.5F)
                    .clientTrackingRange(8))
            .renderer(() -> BasiliskRenderer::new)
            .lang("Basilisk")
            .register();

    public static final EntityEntry<ExplosiveChickenEntity> EXPLOSIVE_CHICKEN = REGISTRATE
            .entity("explosive_chicken", ExplosiveChickenEntity::new, MobCategory.CREATURE)
            .properties(properties -> properties
                    .sized(0.4F, 0.7F)
                    .eyeHeight(0.644F)
                    .clientTrackingRange(10))
            .renderer(() -> ChickenRenderer::new)
            .lang("Explosive Chicken")
            .register();

    public static final EntityEntry<ChairEntity> STOOL = REGISTRATE
            .entity("stool_entity", ChairEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.5F, 0.5F))
            .renderer(() -> ChairRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<FlushToiletEntity> FLUSH_TOILET = REGISTRATE
            .entity("flush_toilet_entity", FlushToiletEntity::new, MobCategory.MISC)
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

    public static final EntityEntry<PoopTntEntity> POOP_TNT = REGISTRATE
            .<PoopTntEntity>entity("poop_tnt", PoopTntEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.75F, 0.75F)
                    .eyeHeight(0.15F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .fireImmune())
            .renderer(() -> PoopTntRenderer::new)
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .register();

    public static final EntityEntry<PopTntMinecartEntity> POP_TNT_MINECART = REGISTRATE
            .entity("pop_tnt_minecart", PopTntMinecartEntity::new, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.98F, 0.7F)
                    .clientTrackingRange(8)
                    .updateInterval(3))
            .renderer(() -> TntMinecartRenderer::new)
            .lang("Minecart with POP")
            .register();

    public static final EntityEntry<PoBoatEntity> GINKGO_BOAT = REGISTRATE
            .<PoBoatEntity>entity("ginkgo_boat",
                    (type, level) -> new PoBoatEntity(PoItems.GINKGO_BOAT.get(), type, level), MobCategory.MISC)
            .properties(properties -> properties
                    .sized(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .clientTrackingRange(10))
            .renderer(() -> PoBoatRenderer.provider("ginkgo", false))
            .register();
    public static final EntityEntry<PoBoatEntity> PRIMO_BOAT = REGISTRATE
            .<PoBoatEntity>entity("primo_boat",
                    (type, level) -> new PoBoatEntity(PoItems.PRIMO_BOAT.get(), type, level), MobCategory.MISC)
            .properties(properties -> properties
                    .sized(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .clientTrackingRange(10))
            .renderer(() -> PoBoatRenderer.provider("primo", false))
            .register();

    public static final EntityEntry<PoChestBoatEntity> GINKGO_CHEST_BOAT = REGISTRATE
            .<PoChestBoatEntity>entity("ginkgo_chest_boat",
                    (type, level) -> new PoChestBoatEntity(PoItems.GINKGO_CHEST_BOAT.get(), type, level), MobCategory.MISC)
            .properties(properties -> properties
                    .sized(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .clientTrackingRange(10))
            .renderer(() -> PoBoatRenderer.provider("ginkgo", true))
            .lang("Ginkgo Boat with Chest")
            .register();
    public static final EntityEntry<PoChestBoatEntity> PRIMO_CHEST_BOAT = REGISTRATE
            .<PoChestBoatEntity>entity("primo_chest_boat",
                    (type, level) -> new PoChestBoatEntity(PoItems.PRIMO_CHEST_BOAT.get(), type, level), MobCategory.MISC)
            .properties(properties -> properties
                    .sized(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .clientTrackingRange(10))
            .renderer(() -> PoBoatRenderer.provider("primo", true))
            .lang("Primo Boat with Chest")
            .register();

    public static final EntityEntry<GachaponEntity> GACHAPON = REGISTRATE
            .entity("gachapon", GachaponEntity::create, MobCategory.MISC)
            .properties(properties -> properties
                    .sized(0.5F, 0.75F)
                    .eyeHeight(0.375F)
                    .clientTrackingRange(10))
            .renderer(() -> GachaponRenderer::new)
            .lang("Gachapon")
            .register();

    public static void register() {
    }
}
