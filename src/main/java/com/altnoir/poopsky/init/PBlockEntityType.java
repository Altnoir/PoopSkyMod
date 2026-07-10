package com.altnoir.poopsky.init;

import com.altnoir.poopsky.content.block.entity.*;
import com.altnoir.poopsky.content.block.renderer.SieveBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.neoforged.bus.api.IEventBus;

public class PBlockEntityType {
    public static final BlockEntityEntry<ToiletBlockEntity> TOILET_BLOCK_ENTITY = PRegistries.REGISTRATE
            .<ToiletBlockEntity>blockEntity("toilet_block_entity", (type, pos, state) -> new ToiletBlockEntity(pos, state))
            .validBlocks(PBlocks.WOODEN_TOILET, PBlocks.HARD_TOILET)
            .register();

    public static final BlockEntityEntry<SieveBlockEntity> SIEVE_BLOCK_ENTITY = PRegistries.REGISTRATE
            .<SieveBlockEntity>blockEntity("sieve_block_entity", (type, pos, state) -> new SieveBlockEntity(pos, state))
            .validBlock(PBlocks.SIEVE)
            .renderer(() -> SieveBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<PlacerBlockEntity> PLACER_BLOCK_ENTITY = PRegistries.REGISTRATE
            .<PlacerBlockEntity>blockEntity("placer_entity", (type, pos, state) -> new PlacerBlockEntity(pos, state))
            .validBlock(PBlocks.PLACER)
            .register();

    public static final BlockEntityEntry<FlyBarrelBlockEntity> FLY_BARREL = PRegistries.REGISTRATE
            .<FlyBarrelBlockEntity>blockEntity("fly_barrel", (type, pos, state) -> new FlyBarrelBlockEntity(pos, state))
            .validBlock(PBlocks.FLY_BARREL)
            .register();

    public static final BlockEntityEntry<BreedingChestBlockEntity> BREEDING_CHEST = PRegistries.REGISTRATE
            .<BreedingChestBlockEntity>blockEntity("breeding_chest", (type, pos, state) -> new BreedingChestBlockEntity(pos, state))
            .validBlock(PBlocks.BREEDING_CHEST)
            .register();

    public static void register(IEventBus eventBus) {
    }
}
