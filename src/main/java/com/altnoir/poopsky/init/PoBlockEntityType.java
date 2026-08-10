package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.*;
import com.altnoir.poopsky.content.block.renderer.GachaMachineBlockEntityRenderer;
import com.altnoir.poopsky.content.block.renderer.MaggotsChunkLoaderBlockEntityRenderer;
import com.altnoir.poopsky.content.block.renderer.SieveBlockEntityRenderer;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class PoBlockEntityType {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final BlockEntityEntry<ToiletBlockEntity> TOILET_BLOCK_ENTITY = REGISTRATE
            .<ToiletBlockEntity>blockEntity("toilet_block_entity", (type, pos, state) -> new ToiletBlockEntity(pos, state))
            .validBlocks(PoBlocks.WOODEN_TOILET, PoBlocks.HARD_TOILET)
            .register();

    public static final BlockEntityEntry<SieveBlockEntity> SIEVE_BLOCK_ENTITY = REGISTRATE
            .<SieveBlockEntity>blockEntity("sieve_block_entity", (type, pos, state) -> new SieveBlockEntity(pos, state))
            .validBlock(PoBlocks.SIEVE)
            .renderer(() -> SieveBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<PlacerBlockEntity> PLACER_BLOCK_ENTITY = REGISTRATE
            .<PlacerBlockEntity>blockEntity("placer_entity", (type, pos, state) -> new PlacerBlockEntity(pos, state))
            .validBlock(PoBlocks.PLACER)
            .register();

    public static final BlockEntityEntry<MaggotsChunkLoaderBlockEntity> MAGGOTS_CHUNK_LOADER = REGISTRATE
            .<MaggotsChunkLoaderBlockEntity>blockEntity("maggots_chunk_loader", (type, pos, state) -> new MaggotsChunkLoaderBlockEntity(pos, state))
            .validBlock(PoBlocks.MAGGOTS_CHUNK_LOADER)
            .renderer(() -> MaggotsChunkLoaderBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<FlyBarrelBlockEntity> FLY_BARREL = REGISTRATE
            .<FlyBarrelBlockEntity>blockEntity("fly_barrel", (type, pos, state) -> new FlyBarrelBlockEntity(pos, state))
            .validBlock(PoBlocks.FLY_BARREL)
            .register();

    public static final BlockEntityEntry<BreedingChestBlockEntity> BREEDING_CHEST = REGISTRATE
            .<BreedingChestBlockEntity>blockEntity("breeding_chest", (type, pos, state) -> new BreedingChestBlockEntity(pos, state))
            .validBlock(PoBlocks.BREEDING_CHEST)
            .register();

    public static final BlockEntityEntry<FlushToiletBlockEntity> FLUSH_TOILET = REGISTRATE
            .<FlushToiletBlockEntity>blockEntity("flush_toilet", (type, pos, state) -> new FlushToiletBlockEntity(pos, state))
            .validBlocks(PoBlocks.FLUSH_TOILET, PoBlocks.GOLDEN_FLUSH_TOILET)
            .register();

    public static final BlockEntityEntry<GachaMachineBlockEntity> GACHA_MACHINE = REGISTRATE
            .<GachaMachineBlockEntity>blockEntity("gacha_machine", (type, pos, state) -> new GachaMachineBlockEntity(pos, state))
            .validBlock(PoBlocks.GACHA_MACHINE)
            .renderer(() -> GachaMachineBlockEntityRenderer::new)
            .register();

    public static void register() {
    }
}
