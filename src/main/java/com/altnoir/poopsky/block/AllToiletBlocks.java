package com.altnoir.poopsky.block;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.block.p.GoldgenBaseToiletBlock;
import com.altnoir.poopsky.block.p.LavaToiletBlock;
import com.altnoir.poopsky.block.p.ToiletBlock;
import com.altnoir.poopsky.init.PItems;
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
    private static final float WOOD_STRENGTH = 2.0F;
    private static final float STONE_STRENGTH = 4.0F;
    private static final float METAL_STRENGTH = 10.0F;
    private static final float TOILET_RESISTANCE = 100.0F;
    private static final int LAVA_LIGHT_LEVEL = 15;

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PoopSky.MOD_ID);

    public static final DeferredBlock<Block> WOOD_TOILET = registerBlock(
            "wood_toilet",
            () -> new ToiletBlock(woodToiletProperties(MapColor.WOOD, SoundType.WOOD))
    );

    public static final DeferredBlock<Block> STONE_TOILET = registerBlock(
            "stone_toilet",
            () -> new LavaToiletBlock(LavaToiletType.COBBLESTONE, lavaToiletProperties(MapColor.STONE, STONE_STRENGTH))
    );

    public static final DeferredBlock<Block> METAL_TOILET = registerBlock(
            "metal_toilet",
            () -> new LavaToiletBlock(LavaToiletType.IRON, lavaToiletProperties(MapColor.METAL, METAL_STRENGTH))
    );

    public static final DeferredBlock<Block> RAINBOW_TOILET = registerBlock(
            "rainbow_toilet",
            () -> new GoldgenBaseToiletBlock(lavaToiletProperties(MapColor.COLOR_LIGHT_GRAY, STONE_STRENGTH))
    );

    private static BlockBehaviour.Properties woodToiletProperties(MapColor color, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASS)
                .strength(WOOD_STRENGTH, TOILET_RESISTANCE)
                .sound(sound)
                .ignitedByLava();
    }

    private static BlockBehaviour.Properties lavaToiletProperties(MapColor color, float strength) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .lightLevel(lavaLightLevel())
                .strength(strength, TOILET_RESISTANCE)
                .requiresCorrectToolForDrops()
                .ignitedByLava();
    }

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
        BLOCKS.register(eventBus);
    }
}