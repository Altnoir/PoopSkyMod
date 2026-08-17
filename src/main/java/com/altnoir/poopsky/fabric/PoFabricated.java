package com.altnoir.poopsky.fabric;

import com.altnoir.poopsky.content.block.entity.*;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.entity.p.PoolimeEntity;
import com.altnoir.poopsky.fabric.port.fluidhandler.FluidHandlerStorage;
import com.altnoir.poopsky.fabric.port.itemhandler.IItemHandlerModifiable;
import com.altnoir.poopsky.fabric.port.itemhandler.ItemHandlerWorldlyContainer;
import com.altnoir.poopsky.impl.network.PoNetworking;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoEntityType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.block.entity.BlockEntity;
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

        ItemStorage.SIDED.registerForBlockEntities((blockEntity, direction) ->
                InventoryStorage.of(worldlyContainer(
                        blockEntity,
                        ((SieveBlockEntity) blockEntity).getItemHandler(),
                        new int[]{SieveBlockEntity.INPUT_SLOT},
                        new int[]{SieveBlockEntity.INPUT_SLOT}
                ), direction), PoBlockEntityType.SIEVE_BLOCK_ENTITY.get());
        ItemStorage.SIDED.registerForBlockEntities((blockEntity, direction) ->
                InventoryStorage.of(worldlyContainer(
                        blockEntity,
                        ((IItemHandlerModifiable) ((FlyBarrelBlockEntity) blockEntity).getItemHandler()),
                        new int[]{FlyBarrelBlockEntity.SLOT_INPUT},
                        new int[]{FlyBarrelBlockEntity.SLOT_OUTPUT_1, FlyBarrelBlockEntity.SLOT_OUTPUT_2, FlyBarrelBlockEntity.SLOT_OUTPUT_3, FlyBarrelBlockEntity.SLOT_OUTPUT_4}
                ), direction), PoBlockEntityType.FLY_BARREL.get());
        ItemStorage.SIDED.registerForBlockEntities((blockEntity, direction) ->
                InventoryStorage.of(worldlyContainer(
                        blockEntity,
                        ((IItemHandlerModifiable) ((BreedingChestBlockEntity) blockEntity).getItemHandler()),
                        new int[]{BreedingChestBlockEntity.SLOT_FECES, BreedingChestBlockEntity.SLOT_FLY_1, BreedingChestBlockEntity.SLOT_FLY_2},
                        new int[]{BreedingChestBlockEntity.SLOT_OUTPUT_1, BreedingChestBlockEntity.SLOT_OUTPUT_2, BreedingChestBlockEntity.SLOT_OUTPUT_3}
                ), direction), PoBlockEntityType.BREEDING_CHEST.get());
        ItemStorage.SIDED.registerForBlockEntities((blockEntity, direction) ->
                InventoryStorage.of(worldlyContainer(
                        blockEntity,
                        ((FlushToiletBlockEntity) blockEntity).getItemHandler(),
                        new int[]{},
                        new int[]{0}
                ), direction), PoBlockEntityType.FLUSH_TOILET.get());

        FluidStorage.SIDED.registerForBlockEntities((blockEntity, direction) ->
                new FluidHandlerStorage(((ToiletBlockEntity) blockEntity).fluidTank, blockEntity::setChanged),
                PoBlockEntityType.TOILET_BLOCK_ENTITY.get());
    }

    private static ItemHandlerWorldlyContainer worldlyContainer(
            BlockEntity blockEntity,
            IItemHandlerModifiable itemHandler,
            int[] inputSlots,
            int[] outputSlots) {
        return new ItemHandlerWorldlyContainer(blockEntity, itemHandler, inputSlots, outputSlots);
    }
}
