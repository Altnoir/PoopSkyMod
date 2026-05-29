package com.altnoir.poopsky.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.PSEntityType;
import com.altnoir.poopsky.entity.model.FlyModel;
import com.altnoir.poopsky.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.entity.p.FlyEntity;
import com.altnoir.poopsky.entity.p.PoolimeEntity;
import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class PSModEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ToiletPlugModel.LAYER_LOCATION, ToiletPlugModel::createBodyLayer);
        event.registerLayerDefinition(FlyModel.LAYER_LOCATION, FlyModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(PSEntityType.POOLIME.get(), PoolimeEntity.createAttributes().build());
        event.put(PSEntityType.FLY.get(), FlyEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(PSEntityType.POOLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PoolimeEntity::checkPooplimeSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PSEntityType.FLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FlyEntity::checkFlySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void onEntityDismount(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityBeingMounted() instanceof ToiletPlugEntity &&
                event.getEntity() instanceof Player player && player.isShiftKeyDown()) {
            event.setCanceled(true);
        }
    }
}
