package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.entity.p.PoolimeEntity;
import com.altnoir.poopsky.impl.DataGenerators;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.impl.network.PoNetworking;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public class PoModEvents {
    public static void registerMod(IEventBus modEventBus) {
        modEventBus.addListener(DataGenerators::gatherData);
        modEventBus.addListener(PoNetworking::registerNetworking);
        modEventBus.addListener(PoModEvents::registerAttributes);
        modEventBus.addListener(PoModEvents::registerSpawnPlacements);
        modEventBus.addListener(PoModEvents::registerCapabilities);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(PoEntityType.POOLIME.get(), PoolimeEntity.createAttributes().build());
        event.put(PoEntityType.FLY.get(), FlyEntity.createAttributes().build());
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(PoEntityType.POOLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PoolimeEntity::checkPoolimeSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PoEntityType.FLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FlyEntity::checkFlySpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                PoBlockEntityType.SIEVE_BLOCK_ENTITY.get(),
                (blockEntity, direction) -> {
                    if (direction == null || direction == Direction.DOWN) {
                        return blockEntity.getBottomHandler();
                    }
                    return blockEntity.getTopSideHandler();
                }
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                PoBlockEntityType.TOILET_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.fluidTank
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                PoBlockEntityType.FLY_BARREL.get(),
                (blockEntity, direction) -> {
                    if (direction == null || direction == Direction.DOWN) {
                        return blockEntity.getBottomHandler();
                    }
                    return blockEntity.getTopSideHandler();
                }
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                PoBlockEntityType.BREEDING_CHEST.get(),
                (blockEntity, direction) -> {
                    if (direction == null || direction == Direction.DOWN) {
                        return blockEntity.getBottomHandler();
                    }
                    return blockEntity.getTopSideHandler();
                }
        );
    }
}