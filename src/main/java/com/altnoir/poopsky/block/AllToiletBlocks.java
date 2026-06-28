package com.altnoir.poopsky.block;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.p.GoldgenToiletBlock;
import com.altnoir.poopsky.block.p.ToiletBlock;
import com.altnoir.poopsky.block.p.ToiletLavaBlock;
import com.altnoir.poopsky.init.PItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
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
    private static final float TOILET_RESISTANCE = 100.0F;
    private static final int LAVA_LIGHT_LEVEL = 15;

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PoopSky.MOD_ID);

    public static final DeferredBlock<Block> OAK_TOILET = registerWoodToilet("oak_toilet", MapColor.WOOD, SoundType.WOOD);
    public static final DeferredBlock<Block> SPRUCE_TOILET = registerWoodToilet("spruce_toilet", MapColor.WOOD, SoundType.WOOD);
    public static final DeferredBlock<Block> BIRCH_TOILET = registerWoodToilet("birch_toilet", MapColor.COLOR_YELLOW, SoundType.WOOD);
    public static final DeferredBlock<Block> JUNGLE_TOILET = registerWoodToilet("jungle_toilet", MapColor.COLOR_BROWN, SoundType.WOOD);
    public static final DeferredBlock<Block> ACACIA_TOILET = registerWoodToilet("acacia_toilet", MapColor.COLOR_ORANGE, SoundType.WOOD);
    public static final DeferredBlock<Block> CHERRY_TOILET = registerWoodToilet("cherry_toilet", MapColor.TERRACOTTA_WHITE, SoundType.CHERRY_WOOD);
    public static final DeferredBlock<Block> DARK_OAK_TOILET = registerWoodToilet("dark_oak_toilet", MapColor.COLOR_BROWN, SoundType.WOOD);
    public static final DeferredBlock<Block> MANGROVE_TOILET = registerWoodToilet("mangrove_toilet", MapColor.COLOR_RED, SoundType.WOOD);
    public static final DeferredBlock<Block> BAMBOO_TOILET = registerWoodToilet("bamboo_toilet", MapColor.COLOR_YELLOW, SoundType.BAMBOO_WOOD);
    public static final DeferredBlock<Block> CRIMSON_TOILET = registerWoodToilet("crimson_toilet", MapColor.TERRACOTTA_RED, SoundType.NETHER_WOOD);
    public static final DeferredBlock<Block> WARPED_TOILET = registerWoodToilet("warped_toilet", MapColor.WATER, SoundType.NETHER_WOOD);

    public static final DeferredBlock<Block> STONE_TOILET = registerLavaToilet("stone_toilet", MapColor.STONE);
    public static final DeferredBlock<Block> COBBLESTONE_TOILET = registerLavaToilet("cobblestone_toilet", MapColor.STONE);
    public static final DeferredBlock<Block> MOSSY_COBBLESTONE_TOILET = registerLavaToilet("mossy_cobblestone_toilet", MapColor.STONE);
    public static final DeferredBlock<Block> SMOOTH_STONE_TOILET = registerLavaToilet("smooth_stone_toilet", MapColor.STONE);
    public static final DeferredBlock<Block> STONE_BRICK_TOILET = registerLavaToilet("stone_brick_toilet", MapColor.STONE);
    public static final DeferredBlock<Block> MOSSY_STONE_BRICK_TOILET = registerLavaToilet("mossy_stone_brick_toilet", MapColor.STONE);

    public static final DeferredBlock<Block> TILE_TOILET = registerColorToilet("tile_toilet", DyeColor.LIGHT_BLUE);

    public static final DeferredBlock<Block> WHITE_CONCRETE_TOILET = registerColorToilet("white_concrete_toilet", DyeColor.WHITE);
    public static final DeferredBlock<Block> LIGHT_GRAY_CONCRETE_TOILET = registerColorToilet("light_gray_concrete_toilet", DyeColor.LIGHT_GRAY);
    public static final DeferredBlock<Block> GRAY_CONCRETE_TOILET = registerColorToilet("gray_concrete_toilet", DyeColor.GRAY);
    public static final DeferredBlock<Block> BLACK_CONCRETE_TOILET = registerColorToilet("black_concrete_toilet", DyeColor.BLACK);
    public static final DeferredBlock<Block> BROWN_CONCRETE_TOILET = registerColorToilet("brown_concrete_toilet", DyeColor.BROWN);
    public static final DeferredBlock<Block> RED_CONCRETE_TOILET = registerColorToilet("red_concrete_toilet", DyeColor.RED);
    public static final DeferredBlock<Block> ORANGE_CONCRETE_TOILET = registerColorToilet("orange_concrete_toilet", DyeColor.ORANGE);
    public static final DeferredBlock<Block> YELLOW_CONCRETE_TOILET = registerColorToilet("yellow_concrete_toilet", DyeColor.YELLOW);
    public static final DeferredBlock<Block> LIME_CONCRETE_TOILET = registerColorToilet("lime_concrete_toilet", DyeColor.LIME);
    public static final DeferredBlock<Block> GREEN_CONCRETE_TOILET = registerColorToilet("green_concrete_toilet", DyeColor.GREEN);
    public static final DeferredBlock<Block> CYAN_CONCRETE_TOILET = registerColorToilet("cyan_concrete_toilet", DyeColor.CYAN);
    public static final DeferredBlock<Block> LIGHT_BLUE_CONCRETE_TOILET = registerColorToilet("light_blue_concrete_toilet", DyeColor.LIGHT_BLUE);
    public static final DeferredBlock<Block> BLUE_CONCRETE_TOILET = registerColorToilet("blue_concrete_toilet", DyeColor.BLUE);
    public static final DeferredBlock<Block> PURPLE_CONCRETE_TOILET = registerColorToilet("purple_concrete_toilet", DyeColor.PURPLE);
    public static final DeferredBlock<Block> MAGENTA_CONCRETE_TOILET = registerColorToilet("magenta_concrete_toilet", DyeColor.MAGENTA);
    public static final DeferredBlock<Block> PINK_CONCRETE_TOILET = registerColorToilet("pink_concrete_toilet", DyeColor.PINK);

    public static final DeferredBlock<Block> RAINBOW_TOILET = registerBlock(
            "rainbow_toilet",
            () -> new GoldgenToiletBlock(lavaToiletProperties(DyeColor.WHITE.getMapColor()))
    );

    private static DeferredBlock<Block> registerWoodToilet(String name, MapColor color, SoundType sound) {
        return registerBlock(name, () -> new ToiletBlock(woodToiletProperties(color, sound)));
    }

    private static DeferredBlock<Block> registerLavaToilet(String name, MapColor color) {
        return registerBlock(name, () -> new ToiletLavaBlock(lavaToiletProperties(color)));
    }

    private static DeferredBlock<Block> registerColorToilet(String name, DyeColor color) {
        return registerLavaToilet(name, color.getMapColor());
    }

    private static BlockBehaviour.Properties woodToiletProperties(MapColor color, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASS)
                .strength(WOOD_STRENGTH, TOILET_RESISTANCE)
                .sound(sound)
                .ignitedByLava();
    }

    private static BlockBehaviour.Properties lavaToiletProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .lightLevel(lavaLightLevel())
                .strength(STONE_STRENGTH, TOILET_RESISTANCE)
                .requiresCorrectToolForDrops()
                .ignitedByLava();
    }

    private static ToIntFunction<BlockState> lavaLightLevel() {
        return state -> state.getValue(ToiletLavaBlock.LAVA) ? LAVA_LIGHT_LEVEL : 0;
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> blockFactory) {
        DeferredBlock<T> block = BLOCKS.register(name, blockFactory);
        registerBlockItem(name, block);
        return block;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        PItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(88)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
