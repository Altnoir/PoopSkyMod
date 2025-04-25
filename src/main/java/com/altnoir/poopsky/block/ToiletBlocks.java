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
import net.minecraft.util.Rarity;

public class ToiletBlocks {
    public static final Block OAK_TOILET = registerWoodToilet("oak_toilet", MapColor.OAK_TAN, BlockSoundGroup.WOOD);
    public static final Block SPRUCE_TOILET = registerWoodToilet("spruce_toilet", MapColor.SPRUCE_BROWN, BlockSoundGroup.WOOD);
    public static final Block BIRCH_TOILET = registerWoodToilet("birch_toilet", MapColor.PALE_YELLOW, BlockSoundGroup.WOOD);
    public static final Block JUNGLE_TOILET = registerWoodToilet("jungle_toilet", MapColor.DIRT_BROWN, BlockSoundGroup.WOOD);
    public static final Block ACACIA_TOILET = registerWoodToilet("acacia_toilet", MapColor.ORANGE, BlockSoundGroup.WOOD);
    public static final Block CHERRY_TOILET = registerWoodToilet("cherry_toilet", MapColor.TERRACOTTA_WHITE, BlockSoundGroup.CHERRY_WOOD);
    public static final Block DARK_OAK_TOILET = registerWoodToilet("dark_oak_toilet", MapColor.BROWN, BlockSoundGroup.WOOD);
    public static final Block MANGROVE_TOILET = registerWoodToilet("mangrove_toilet", MapColor.RED, BlockSoundGroup.WOOD);
    public static final Block BAMBOO_TOILET = registerWoodToilet("bamboo_toilet", MapColor.YELLOW, BlockSoundGroup.BAMBOO_WOOD);
    public static final Block CRIMSON_TOILET = registerWoodToilet("crimson_toilet", MapColor.DULL_PINK, BlockSoundGroup.NETHER_WOOD);
    public static final Block WARPED_TOILET = registerWoodToilet("warped_toilet", MapColor.DARK_AQUA, BlockSoundGroup.NETHER_WOOD);

    public static final Block WHITE_CONCRETE_TOILET = registerConcreteToilet("white_concrete_toilet", DyeColor.WHITE);
    public static final Block ORANGE_CONCRETE_TOILET = registerConcreteToilet("orange_concrete_toilet", DyeColor.ORANGE);
    public static final Block MAGENTA_CONCRETE_TOILET = registerConcreteToilet("magenta_concrete_toilet", DyeColor.MAGENTA);
    public static final Block LIGHT_BLUE_CONCRETE_TOILET = registerConcreteToilet("light_blue_concrete_toilet", DyeColor.LIGHT_BLUE);
    public static final Block YELLOW_CONCRETE_TOILET = registerConcreteToilet("yellow_concrete_toilet", DyeColor.YELLOW);
    public static final Block LIME_CONCRETE_TOILET = registerConcreteToilet("lime_concrete_toilet", DyeColor.LIME);
    public static final Block PINK_CONCRETE_TOILET = registerConcreteToilet("pink_concrete_toilet", DyeColor.PINK);
    public static final Block GRAY_CONCRETE_TOILET = registerConcreteToilet("gray_concrete_toilet", DyeColor.GRAY);
    public static final Block LIGHT_GRAY_CONCRETE_TOILET = registerConcreteToilet("light_gray_concrete_toilet", DyeColor.LIGHT_GRAY);
    public static final Block CYAN_CONCRETE_TOILET = registerConcreteToilet("cyan_concrete_toilet", DyeColor.CYAN);
    public static final Block PURPLE_CONCRETE_TOILET = registerConcreteToilet("purple_concrete_toilet", DyeColor.PURPLE);
    public static final Block BLUE_CONCRETE_TOILET = registerConcreteToilet("blue_concrete_toilet", DyeColor.BLUE);
    public static final Block BROWN_CONCRETE_TOILET = registerConcreteToilet("brown_concrete_toilet", DyeColor.BROWN);
    public static final Block GREEN_CONCRETE_TOILET = registerConcreteToilet("green_concrete_toilet", DyeColor.GREEN);
    public static final Block RED_CONCRETE_TOILET = registerConcreteToilet("red_concrete_toilet", DyeColor.RED);
    public static final Block BLACK_CONCRETE_TOILET = registerConcreteToilet("black_concrete_toilet", DyeColor.BLACK);
    public static final Block RAINBOW_TOILET = registerConcreteToilet("rainbow_toilet", DyeColor.WHITE);

    private static Block registerWoodToilet(String name, MapColor color, BlockSoundGroup soundGroup) {
        return registerBlock(name, new Toilet(AbstractBlock.Settings.create()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 100.0F)
                .sounds(soundGroup)
                .burnable()
        ));
    }
    private static Block registerConcreteToilet(String name, DyeColor color) {
        return registerBlock(name, new Toilet(AbstractBlock.Settings.create()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(1.8F,100.0F)
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
}
