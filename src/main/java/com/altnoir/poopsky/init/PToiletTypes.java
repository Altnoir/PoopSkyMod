package com.altnoir.poopsky.init;

import net.minecraft.world.level.block.Blocks;

public final class PToiletTypes {

    // ——— 木质马桶 ———
    public static final ToiletType OAK = ToiletType.register(Blocks.OAK_PLANKS, ToiletType.Category.WOOD);
    public static final ToiletType SPRUCE = ToiletType.register(Blocks.SPRUCE_PLANKS, ToiletType.Category.WOOD);
    public static final ToiletType BIRCH = ToiletType.register(Blocks.BIRCH_PLANKS, ToiletType.Category.WOOD);
    public static final ToiletType JUNGLE = ToiletType.register(Blocks.JUNGLE_PLANKS, ToiletType.Category.WOOD);
    public static final ToiletType ACACIA = ToiletType.register(Blocks.ACACIA_PLANKS, ToiletType.Category.WOOD);
    public static final ToiletType CHERRY = ToiletType.register(Blocks.CHERRY_PLANKS, ToiletType.Category.WOOD);
    public static final ToiletType DARK_OAK = ToiletType.register(Blocks.DARK_OAK_PLANKS, ToiletType.Category.WOOD);
    public static final ToiletType MANGROVE = ToiletType.register(Blocks.MANGROVE_PLANKS, ToiletType.Category.WOOD);
    public static final ToiletType BAMBOO = ToiletType.register(Blocks.BAMBOO_PLANKS, ToiletType.Category.WOOD);
    public static final ToiletType CRIMSON = ToiletType.register(Blocks.CRIMSON_PLANKS, ToiletType.Category.WOOD);
    public static final ToiletType WARPED = ToiletType.register(Blocks.WARPED_PLANKS, ToiletType.Category.WOOD);

    // ——— 石质马桶 ———
    public static final ToiletType STONE = ToiletType.register(Blocks.STONE, ToiletType.Category.STONE);
    public static final ToiletType COBBLESTONE = ToiletType.register(Blocks.COBBLESTONE, ToiletType.Category.STONE);
    public static final ToiletType MOSSY_COBBLESTONE = ToiletType.register(Blocks.MOSSY_COBBLESTONE, ToiletType.Category.STONE);
    public static final ToiletType SMOOTH_STONE = ToiletType.register(Blocks.SMOOTH_STONE, ToiletType.Category.STONE);
    public static final ToiletType STONE_BRICK = ToiletType.register(Blocks.STONE_BRICKS, ToiletType.Category.STONE);
    public static final ToiletType MOSSY_STONE_BRICK = ToiletType.register(Blocks.MOSSY_STONE_BRICKS, ToiletType.Category.STONE);
    public static final ToiletType TILE = ToiletType.register(PBlocks.TILE_BLOCK.get(), ToiletType.Category.STONE);
    public static final ToiletType WHITE_CONCRETE = ToiletType.register(Blocks.WHITE_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType ORANGE_CONCRETE = ToiletType.register(Blocks.ORANGE_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType MAGENTA_CONCRETE = ToiletType.register(Blocks.MAGENTA_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType LIGHT_BLUE_CONCRETE = ToiletType.register(Blocks.LIGHT_BLUE_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType YELLOW_CONCRETE = ToiletType.register(Blocks.YELLOW_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType LIME_CONCRETE = ToiletType.register(Blocks.LIME_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType PINK_CONCRETE = ToiletType.register(Blocks.PINK_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType GRAY_CONCRETE = ToiletType.register(Blocks.GRAY_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType LIGHT_GRAY_CONCRETE = ToiletType.register(Blocks.LIGHT_GRAY_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType CYAN_CONCRETE = ToiletType.register(Blocks.CYAN_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType PURPLE_CONCRETE = ToiletType.register(Blocks.PURPLE_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType BLUE_CONCRETE = ToiletType.register(Blocks.BLUE_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType BROWN_CONCRETE = ToiletType.register(Blocks.BROWN_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType GREEN_CONCRETE = ToiletType.register(Blocks.GREEN_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType RED_CONCRETE = ToiletType.register(Blocks.RED_CONCRETE, ToiletType.Category.STONE);
    public static final ToiletType BLACK_CONCRETE = ToiletType.register(Blocks.BLACK_CONCRETE, ToiletType.Category.STONE);

    // ——— 金属马桶 ———
    public static final ToiletType IRON = ToiletType.register(Blocks.IRON_BLOCK, ToiletType.Category.METAL);
    public static final ToiletType GOLD = ToiletType.register(Blocks.GOLD_BLOCK, ToiletType.Category.METAL);
    public static final ToiletType COPPER = ToiletType.register(Blocks.COPPER_BLOCK, ToiletType.Category.METAL);
    public static final ToiletType LAPIS = ToiletType.register(Blocks.LAPIS_BLOCK, ToiletType.Category.METAL);
    public static final ToiletType REDSTONE = ToiletType.register(Blocks.REDSTONE_BLOCK, ToiletType.Category.METAL);
    public static final ToiletType QUARTZ = ToiletType.register(Blocks.QUARTZ_BLOCK, ToiletType.Category.METAL);
    public static final ToiletType DIAMOND = ToiletType.register(Blocks.DIAMOND_BLOCK, ToiletType.Category.METAL);
    public static final ToiletType EMERALD = ToiletType.register(Blocks.EMERALD_BLOCK, ToiletType.Category.METAL);
    public static final ToiletType NETHERITE = ToiletType.register(Blocks.NETHERITE_BLOCK, ToiletType.Category.METAL);

    private PToiletTypes() {}
}