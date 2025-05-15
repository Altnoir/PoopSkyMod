package com.altnoir.poopsky.block;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import net.minecraft.util.Rarity;

public class ToiletBlocks {
    public static final Block OAK_TOILET = registerToilet("oak_toilet", MapColor.OAK_TAN, BlockSoundGroup.WOOD);
    public static final Block SPRUCE_TOILET = registerToilet("spruce_toilet", MapColor.SPRUCE_BROWN, BlockSoundGroup.WOOD);
    public static final Block BIRCH_TOILET = registerToilet("birch_toilet", MapColor.PALE_YELLOW, BlockSoundGroup.WOOD);
    public static final Block JUNGLE_TOILET = registerToilet("jungle_toilet", MapColor.DIRT_BROWN, BlockSoundGroup.WOOD);
    public static final Block ACACIA_TOILET = registerToilet("acacia_toilet", MapColor.ORANGE, BlockSoundGroup.WOOD);
    public static final Block CHERRY_TOILET = registerToilet("cherry_toilet", MapColor.TERRACOTTA_WHITE, BlockSoundGroup.CHERRY_WOOD);
    public static final Block DARK_OAK_TOILET = registerToilet("dark_oak_toilet", MapColor.BROWN, BlockSoundGroup.WOOD);
    public static final Block MANGROVE_TOILET = registerToilet("mangrove_toilet", MapColor.RED, BlockSoundGroup.WOOD);
    public static final Block BAMBOO_TOILET = registerToilet("bamboo_toilet", MapColor.YELLOW, BlockSoundGroup.BAMBOO_WOOD);
    public static final Block CRIMSON_TOILET = registerToilet("crimson_toilet", MapColor.DULL_PINK, BlockSoundGroup.NETHER_WOOD);
    public static final Block WARPED_TOILET = registerToilet("warped_toilet", MapColor.DARK_AQUA, BlockSoundGroup.NETHER_WOOD);

    public static final Block STONE_TOILET = registerToilet("stone_toilet", MapColor.STONE_GRAY);
    public static final Block COBBLESTONE_TOILET = registerToilet("cobblestone_toilet", MapColor.STONE_GRAY);
    public static final Block MOSSY_COBBLESTONE_TOILET = registerToilet("mossy_cobblestone_toilet", MapColor.STONE_GRAY);
    public static final Block SMOOTH_STONE_TOILET = registerToilet("smooth_stone_toilet", MapColor.STONE_GRAY);
    public static final Block STONE_BRICK_TOILET = registerToilet("stone_brick_toilet", MapColor.STONE_GRAY);
    public static final Block MOSSY_STONE_BRICK_TOILET = registerToilet("mossy_stone_brick_toilet", MapColor.STONE_GRAY);

    public static final Block WHITE_CONCRETE_TOILET = registerToilet("white_concrete_toilet", DyeColor.WHITE);
    public static final Block LIGHT_GRAY_CONCRETE_TOILET = registerToilet("light_gray_concrete_toilet", DyeColor.LIGHT_GRAY);
    public static final Block GRAY_CONCRETE_TOILET = registerToilet("gray_concrete_toilet", DyeColor.GRAY);
    public static final Block BLACK_CONCRETE_TOILET = registerToilet("black_concrete_toilet", DyeColor.BLACK);
    public static final Block BROWN_CONCRETE_TOILET = registerToilet("brown_concrete_toilet", DyeColor.BROWN);
    public static final Block RED_CONCRETE_TOILET = registerToilet("red_concrete_toilet", DyeColor.RED);
    public static final Block ORANGE_CONCRETE_TOILET = registerToilet("orange_concrete_toilet", DyeColor.ORANGE);
    public static final Block YELLOW_CONCRETE_TOILET = registerToilet("yellow_concrete_toilet", DyeColor.YELLOW);
    public static final Block LIME_CONCRETE_TOILET = registerToilet("lime_concrete_toilet", DyeColor.LIME);
    public static final Block GREEN_CONCRETE_TOILET = registerToilet("green_concrete_toilet", DyeColor.GREEN);
    public static final Block CYAN_CONCRETE_TOILET = registerToilet("cyan_concrete_toilet", DyeColor.CYAN);
    public static final Block LIGHT_BLUE_CONCRETE_TOILET = registerToilet("light_blue_concrete_toilet", DyeColor.LIGHT_BLUE);
    public static final Block BLUE_CONCRETE_TOILET = registerToilet("blue_concrete_toilet", DyeColor.BLUE);
    public static final Block PURPLE_CONCRETE_TOILET = registerToilet("purple_concrete_toilet", DyeColor.PURPLE);
    public static final Block MAGENTA_CONCRETE_TOILET = registerToilet("magenta_concrete_toilet", DyeColor.MAGENTA);
    public static final Block PINK_CONCRETE_TOILET = registerToilet("pink_concrete_toilet", DyeColor.PINK);
    public static final Block RAINBOW_TOILET = registerToilet("rainbow_toilet", DyeColor.WHITE);

    private static Block registerToilet(String name, MapColor color, BlockSoundGroup soundGroup) {
        return registerBlock(name, new Toilet(AbstractBlock.Settings.create()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 100.0F)
                .sounds(soundGroup)
                .burnable()
        ));
    }
    private static Block registerToilet(String name, MapColor color) {
        return registerBlock(name, new ToiletLava(AbstractBlock.Settings.create()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(2.0F,100.0F)
                .requiresTool()
        ));
    }
    private static Block registerToilet(String name, DyeColor color) {
        return registerBlock(name, new ToiletLava(AbstractBlock.Settings.create()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(2.0F,100.0F)
                .requiresTool()
        ));
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(PoopSky.MOD_ID, name), block);
    }
    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(PoopSky.MOD_ID, name),
                new BlockItem(block, new BlockItem.Settings().rarity(Rarity.RARE)));
    }
    public static void registerToiletBlocks() {
        PoopSky.LOGGER.info("Registering Toilet Blocks for " + PoopSky.MOD_ID);
    }
}
