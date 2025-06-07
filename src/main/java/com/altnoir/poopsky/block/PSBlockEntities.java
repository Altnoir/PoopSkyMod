package com.altnoir.poopsky.block;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.block.p.ToiletBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PSBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PoopSky.MOD_ID);

    public static final Supplier<BlockEntityType<ToiletBlockEntity>> TOILET_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "toilet_block_entity",
            () -> {
                var blocks = ToiletBlocks.BLOCKS.getEntries().stream()
                        .map(DeferredHolder::get)
                        .toArray(Block[]::new);
                return BlockEntityType.Builder.of(ToiletBlockEntity::new, blocks).build(null);
            }
    );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
