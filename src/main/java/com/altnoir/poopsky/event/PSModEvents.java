package com.altnoir.poopsky.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PBlockEntityType;
import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.entity.p.FlyEntity;
import com.altnoir.poopsky.entity.p.PoolimeEntity;
import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class PSModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(PEntityType.POOLIME.get(), PoolimeEntity.createAttributes().build());
        event.put(PEntityType.FLY.get(), FlyEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(PEntityType.POOLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PoolimeEntity::checkPooplimeSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PEntityType.FLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FlyEntity::checkFlySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void onEntityDismount(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityBeingMounted() instanceof ToiletPlugEntity &&
                event.getEntity() instanceof Player player && player.isShiftKeyDown()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                PBlockEntityType.SIEVE_BLOCK_ENTITY.get(),
                (blockEntity, direction) -> {
                    if (direction == null || direction == Direction.DOWN) {
                        return blockEntity.getBottomHandler();
                    }
                    return blockEntity.getTopSideHandler();
                }
        );
    }
}
