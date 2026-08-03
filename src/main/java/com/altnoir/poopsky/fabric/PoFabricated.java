package com.altnoir.poopsky.fabric;

import com.altnoir.poopsky.content.block.entity.BreedingChestBlockEntity;
import com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity;
import com.altnoir.poopsky.content.block.entity.SieveBlockEntity;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.entity.p.PoolimeEntity;
import com.altnoir.poopsky.impl.network.PoNetworking;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public class PoFabricated {

    public static void init() {
        FabricatedToolActions.register();
        registerSpawnPlacements();
        registerLookups();
    }

    public static void clientInit() {
        PoNetworking.registerClientReceivers();
    }

    public static void registerSpawnPlacements() {
        SpawnPlacements.register(PoEntityType.POOLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PoolimeEntity::checkPoolimeSpawnRules);
        SpawnPlacements.register(PoEntityType.FLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FlyEntity::checkFlySpawnRules);
    }

    public static void registerLookups() {
        LookupRegisteries.SIEVE.registerForBlockEntities((blockEntity, direction) -> {
            if (direction == null || direction == Direction.DOWN) {
                return ((SieveBlockEntity) blockEntity).getBottomHandler();
            }
            return ((SieveBlockEntity) blockEntity).getTopSideHandler();
        }, PoBlockEntityType.SIEVE_BLOCK_ENTITY.get());
        LookupRegisteries.TOILET.registerForBlockEntities((blockEntity, direction) ->
                ((ToiletBlockEntity) blockEntity).fluidTank, PoBlockEntityType.TOILET_BLOCK_ENTITY.get());
        LookupRegisteries.FLY_BARREL.registerForBlockEntities((blockEntity, direction) -> {
            if (direction == null || direction == Direction.DOWN) {
                return ((FlyBarrelBlockEntity) blockEntity).getBottomHandler();
            }
            return ((FlyBarrelBlockEntity) blockEntity).getTopSideHandler();
        }, PoBlockEntityType.FLY_BARREL.get());
        LookupRegisteries.BREEDING_CHEST.registerForBlockEntities((blockEntity, direction) -> {
            if (direction == null || direction == Direction.DOWN) {
                return ((BreedingChestBlockEntity) blockEntity).getBottomHandler();
            }
            return ((BreedingChestBlockEntity) blockEntity).getTopSideHandler();
        }, PoBlockEntityType.BREEDING_CHEST.get());
    }


}
