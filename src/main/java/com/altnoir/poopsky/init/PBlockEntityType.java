package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.common.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PBlockEntityType {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PoopSky.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ToiletBlockEntity>> TOILET_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("toilet_block_entity",
            () -> {
                var blocks = PBlocks.BLOCKS.getEntries().stream()
                        .filter(b -> b.get() instanceof AbstractToiletBlock)
                        .map(DeferredHolder::get)
                        .toArray(Block[]::new);
                return BlockEntityType.Builder.of(ToiletBlockEntity::new, blocks).build(null);
            }
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SieveBlockEntity>> SIEVE_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("sieve_block_entity", () ->
                    BlockEntityType.Builder.of(SieveBlockEntity::new, PBlocks.SIEVE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlacerBlockEntity>> PLACER_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("placer_entity", () ->
                    BlockEntityType.Builder.of(PlacerBlockEntity::new, PBlocks.PLACER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FlyBarrelBlockEntity>> FLY_BARREL =
            BLOCK_ENTITY_TYPES.register("fly_barrel", () ->
                    BlockEntityType.Builder.of(FlyBarrelBlockEntity::new, PBlocks.FLY_BARREL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BreedingBoxBlockEntity>> BREEDING_BOX =
            BLOCK_ENTITY_TYPES.register("breeding_box", () ->
                    BlockEntityType.Builder.of(BreedingBoxBlockEntity::new, PBlocks.BREEDING_BOX.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}