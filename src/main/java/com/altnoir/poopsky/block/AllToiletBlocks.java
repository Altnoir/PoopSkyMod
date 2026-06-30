package com.altnoir.poopsky.block;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.block.p.LavaToiletBlock;
import com.altnoir.poopsky.block.p.ToiletBlock;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PItems;
import com.altnoir.poopsky.init.PToiletTypes;
import com.altnoir.poopsky.item.p.ToiletBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public final class AllToiletBlocks {
    private static final float WOODEN_STRENGTH = 4.0F;
    private static final float HARD_STRENGTH = 10.0F;
    private static final float TOILET_RESISTANCE = 1200.0F;
    private static final int LAVA_LIGHT_LEVEL = 15;

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PoopSky.MOD_ID);



    private static ToIntFunction<BlockState> lavaLightLevel() {
        return state -> state.getValue(BaseToiletLavaBlock.LAVA) ? LAVA_LIGHT_LEVEL : 0;
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> blockFactory) {
        DeferredBlock<T> block = BLOCKS.register(name, blockFactory);
        registerBlockItem(name, block);
        return block;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        PItems.ITEMS.register(name, () -> new ToiletBlockItem(block.get(), new Item.Properties().stacksTo(88)));
    }

    public static void register(IEventBus eventBus) {
        PToiletTypes.bootstrap();
        BLOCKS.register(eventBus);
    }
}
