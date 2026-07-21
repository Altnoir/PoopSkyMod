package com.altnoir.poopsky.init;

import com.altnoir.poopsky.content.ToiletType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

public final class ToiletTypes {
    // ——— 木质厕所 ———
    public static final ToiletType OAK = ToiletType.register(Blocks.OAK_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.oak");
    public static final ToiletType SPRUCE = ToiletType.register(Blocks.SPRUCE_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.spruce");
    public static final ToiletType BIRCH = ToiletType.register(Blocks.BIRCH_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.birch");
    public static final ToiletType JUNGLE = ToiletType.register(Blocks.JUNGLE_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.jungle");
    public static final ToiletType ACACIA = ToiletType.register(Blocks.ACACIA_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.acacia");
    public static final ToiletType CHERRY = ToiletType.register(Blocks.CHERRY_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.cherry");
    public static final ToiletType DARK_OAK = ToiletType.register(Blocks.DARK_OAK_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.dark_oak");
    public static final ToiletType MANGROVE = ToiletType.register(Blocks.MANGROVE_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.mangrove");
    public static final ToiletType BAMBOO = ToiletType.register(Blocks.BAMBOO_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.bamboo");
    public static final ToiletType CRIMSON = ToiletType.register(Blocks.CRIMSON_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.crimson");
    public static final ToiletType WARPED = ToiletType.register(Blocks.WARPED_PLANKS, ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.warped");
    public static final ToiletType GINKGO = ToiletType.register(PoBlocks.GINKGO_PLANKS.get(), ToiletType.Category.WOOD).nameKey("block.poopsky.toilet.ginkgo");

    // ——— 石质厕所 ———
    public static final ToiletType TILE = ToiletType.register(PoBlocks.TILE_BLOCK.get(), ToiletType.Category.HARD).nameKey("block.poopsky.toilet.tile");
    public static final ToiletType WHITE_TILE = ToiletType.register(PoBlocks.WHITE_TILE_BLOCK.get(), ToiletType.Category.HARD).nameKey("block.poopsky.toilet.white_tile");
    public static final ToiletType STONE = ToiletType.register(Blocks.STONE, ToiletType.Category.HARD);
    public static final ToiletType COBBLESTONE = ToiletType.register(Blocks.COBBLESTONE, ToiletType.Category.HARD);
    public static final ToiletType MOSSY_COBBLESTONE = ToiletType.register(Blocks.MOSSY_COBBLESTONE, ToiletType.Category.HARD);
    public static final ToiletType SMOOTH_STONE = ToiletType.register(Blocks.SMOOTH_STONE, ToiletType.Category.HARD);
    public static final ToiletType STONE_BRICK = ToiletType.register(Blocks.STONE_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType CHISELED_STONE_BRICK = ToiletType.register(Blocks.CHISELED_STONE_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType MOSSY_STONE_BRICK = ToiletType.register(Blocks.MOSSY_STONE_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType GRANITE = ToiletType.register(Blocks.GRANITE, ToiletType.Category.HARD);
    public static final ToiletType POLISHED_GRANITE = ToiletType.register(Blocks.POLISHED_GRANITE, ToiletType.Category.HARD);
    public static final ToiletType DIORITE = ToiletType.register(Blocks.DIORITE, ToiletType.Category.HARD);
    public static final ToiletType POLISHED_DIORITE = ToiletType.register(Blocks.POLISHED_DIORITE, ToiletType.Category.HARD);
    public static final ToiletType ANDESITE = ToiletType.register(Blocks.ANDESITE, ToiletType.Category.HARD);
    public static final ToiletType POLISHED_ANDESITE = ToiletType.register(Blocks.POLISHED_ANDESITE, ToiletType.Category.HARD);
    public static final ToiletType CALCITE = ToiletType.register(Blocks.CALCITE, ToiletType.Category.HARD);
    public static final ToiletType DRIPSTONE_BLOCK = ToiletType.register(Blocks.DRIPSTONE_BLOCK, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.dripstone");
    public static final ToiletType DEEPSLATE = ToiletType.register(Blocks.DEEPSLATE, ToiletType.Category.HARD);
    public static final ToiletType COBBLED_DEEPSLATE = ToiletType.register(Blocks.COBBLED_DEEPSLATE, ToiletType.Category.HARD);
    public static final ToiletType POLISHED_DEEPSLATE = ToiletType.register(Blocks.POLISHED_DEEPSLATE, ToiletType.Category.HARD);
    public static final ToiletType DEEPSLATE_BRICK = ToiletType.register(Blocks.DEEPSLATE_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType DEEPSLATE_TILE = ToiletType.register(Blocks.DEEPSLATE_TILES, ToiletType.Category.HARD);
    public static final ToiletType TUFF = ToiletType.register(Blocks.TUFF, ToiletType.Category.HARD);
    public static final ToiletType POLISHED_TUFF = ToiletType.register(Blocks.POLISHED_TUFF, ToiletType.Category.HARD);
    public static final ToiletType TUFF_BRICK = ToiletType.register(Blocks.TUFF_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType BRICK = ToiletType.register(Blocks.BRICKS, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.brick");
    public static final ToiletType MUD_BRICK = ToiletType.register(Blocks.MUD_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType SANDSTONE = ToiletType.register(Blocks.SANDSTONE, ToiletType.Category.HARD);
    public static final ToiletType SMOOTH_SANDSTONE = ToiletType.register(Blocks.SMOOTH_SANDSTONE, ToiletType.Category.HARD).texture("sandstone_top");
    public static final ToiletType RED_SANDSTONE = ToiletType.register(Blocks.RED_SANDSTONE, ToiletType.Category.HARD);
    public static final ToiletType SMOOTH_RED_SANDSTONE = ToiletType.register(Blocks.SMOOTH_RED_SANDSTONE, ToiletType.Category.HARD).texture("red_sandstone_top");
    public static final ToiletType PRISMARINE = ToiletType.register(Blocks.PRISMARINE, ToiletType.Category.HARD);
    public static final ToiletType PRISMARINE_BRICK = ToiletType.register(Blocks.PRISMARINE_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType DARK_PRISMARINE = ToiletType.register(Blocks.DARK_PRISMARINE, ToiletType.Category.HARD);
    public static final ToiletType NETHERRACK = ToiletType.register(Blocks.NETHERRACK, ToiletType.Category.HARD);
    public static final ToiletType NETHER_BRICK = ToiletType.register(Blocks.NETHER_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType RED_NETHER_BRICK = ToiletType.register(Blocks.RED_NETHER_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType SMOOTH_BASALT = ToiletType.register(Blocks.SMOOTH_BASALT, ToiletType.Category.HARD);
    public static final ToiletType BLACKSTONE = ToiletType.register(Blocks.BLACKSTONE, ToiletType.Category.HARD);
    public static final ToiletType POLISHED_BLACKSTONE = ToiletType.register(Blocks.POLISHED_BLACKSTONE, ToiletType.Category.HARD);
    public static final ToiletType POLISHED_BLACKSTONE_BRICK = ToiletType.register(Blocks.POLISHED_BLACKSTONE_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType QUARTZ_BLOCK = ToiletType.register(Blocks.QUARTZ_BLOCK, ToiletType.Category.HARD).texture("quartz_block_side");
    public static final ToiletType SMOOTH_QUARTZ = ToiletType.register(Blocks.SMOOTH_QUARTZ, ToiletType.Category.HARD).texture("quartz_block_bottom").nameKey("block.poopsky.toilet.smooth_quartz");
    public static final ToiletType CHISELED_QUARTZ_BLOCK = ToiletType.register(Blocks.CHISELED_QUARTZ_BLOCK, ToiletType.Category.HARD);
    public static final ToiletType UARTZ_BRICKS = ToiletType.register(Blocks.QUARTZ_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType END_STONE = ToiletType.register(Blocks.END_STONE, ToiletType.Category.HARD);
    public static final ToiletType END_STONE_BRICK = ToiletType.register(Blocks.END_STONE_BRICKS, ToiletType.Category.HARD);
    public static final ToiletType PURPUR_BLOCK = ToiletType.register(Blocks.PURPUR_BLOCK, ToiletType.Category.HARD);
    // ——— 彩色石质厕所 ———
    public static final ToiletType TERRACOTTA = ToiletType.register(Blocks.TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType WHITE_TERRACOTTA = ToiletType.register(Blocks.WHITE_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType ORANGE_TERRACOTTA = ToiletType.register(Blocks.ORANGE_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType MAGENTA_TERRACOTTA = ToiletType.register(Blocks.MAGENTA_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType LIGHT_BLUE_TERRACOTTA = ToiletType.register(Blocks.LIGHT_BLUE_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType YELLOW_TERRACOTTA = ToiletType.register(Blocks.YELLOW_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType LIME_TERRACOTTA = ToiletType.register(Blocks.LIME_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType PINK_TERRACOTTA = ToiletType.register(Blocks.PINK_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType GRAY_TERRACOTTA = ToiletType.register(Blocks.GRAY_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType LIGHT_GRAY_TERRACOTTA = ToiletType.register(Blocks.LIGHT_GRAY_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType CYAN_TERRACOTTA = ToiletType.register(Blocks.CYAN_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType PURPLE_TERRACOTTA = ToiletType.register(Blocks.PURPLE_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType BLUE_TERRACOTTA = ToiletType.register(Blocks.BLUE_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType BROWN_TERRACOTTA = ToiletType.register(Blocks.BROWN_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType GREEN_TERRACOTTA = ToiletType.register(Blocks.GREEN_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType RED_TERRACOTTA = ToiletType.register(Blocks.RED_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType BLACK_TERRACOTTA = ToiletType.register(Blocks.BLACK_TERRACOTTA, ToiletType.Category.HARD);
    public static final ToiletType WHITE_CONCRETE = ToiletType.register(Blocks.WHITE_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType ORANGE_CONCRETE = ToiletType.register(Blocks.ORANGE_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType MAGENTA_CONCRETE = ToiletType.register(Blocks.MAGENTA_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType LIGHT_BLUE_CONCRETE = ToiletType.register(Blocks.LIGHT_BLUE_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType YELLOW_CONCRETE = ToiletType.register(Blocks.YELLOW_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType LIME_CONCRETE = ToiletType.register(Blocks.LIME_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType PINK_CONCRETE = ToiletType.register(Blocks.PINK_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType GRAY_CONCRETE = ToiletType.register(Blocks.GRAY_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType LIGHT_GRAY_CONCRETE = ToiletType.register(Blocks.LIGHT_GRAY_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType CYAN_CONCRETE = ToiletType.register(Blocks.CYAN_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType PURPLE_CONCRETE = ToiletType.register(Blocks.PURPLE_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType BLUE_CONCRETE = ToiletType.register(Blocks.BLUE_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType BROWN_CONCRETE = ToiletType.register(Blocks.BROWN_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType GREEN_CONCRETE = ToiletType.register(Blocks.GREEN_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType RED_CONCRETE = ToiletType.register(Blocks.RED_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType BLACK_CONCRETE = ToiletType.register(Blocks.BLACK_CONCRETE, ToiletType.Category.HARD);
    public static final ToiletType RAINBOW = ToiletType.register("rainbow", ToiletType.Category.HARD, Component.translatable("block.poopsky.rainbow_toilet")).texture("rainbow_concrete").golden().nameKey("block.poopsky.toilet.rainbow");
    // ——— 金属厕所 ———
    public static final ToiletType IRON = ToiletType.register(Blocks.IRON_BLOCK, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.iron");
    public static final ToiletType GOLD = ToiletType.register(Blocks.GOLD_BLOCK, ToiletType.Category.HARD).golden().nameKey("block.poopsky.toilet.gold");
    public static final ToiletType COPPER = ToiletType.register(Blocks.COPPER_BLOCK, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.copper");
    public static final ToiletType EXPOSED_COPPER = ToiletType.register(Blocks.EXPOSED_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.exposed_copper");
    public static final ToiletType WEATHERED_COPPER = ToiletType.register(Blocks.WEATHERED_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.weathered_copper");
    public static final ToiletType OXIDIZED_COPPER = ToiletType.register(Blocks.OXIDIZED_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.oxidized_copper");
    public static final ToiletType CHISELED_COPPER = ToiletType.register(Blocks.CHISELED_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.chiseled_copper");
    public static final ToiletType EXPOSED_CHISELED_COPPER = ToiletType.register(Blocks.EXPOSED_CHISELED_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.exposed_chiseled_copper");
    public static final ToiletType WEATHERED_CHISELED_COPPER = ToiletType.register(Blocks.WEATHERED_CHISELED_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.weathered_chiseled_copper");
    public static final ToiletType OXIDIZED_CHISELED_COPPER = ToiletType.register(Blocks.OXIDIZED_CHISELED_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.oxidized_chiseled_copper");
    public static final ToiletType CUT_COPPER = ToiletType.register(Blocks.CUT_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.cut_copper");
    public static final ToiletType EXPOSED_CUT_COPPER = ToiletType.register(Blocks.EXPOSED_CUT_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.exposed_cut_copper");
    public static final ToiletType WEATHERED_CUT_COPPER = ToiletType.register(Blocks.WEATHERED_CUT_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.weathered_cut_copper");
    public static final ToiletType OXIDIZED_CUT_COPPER = ToiletType.register(Blocks.OXIDIZED_CUT_COPPER, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.oxidized_cut_copper");
    public static final ToiletType AMETHYST = ToiletType.register(Blocks.AMETHYST_BLOCK, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.amethyst");
    public static final ToiletType LAPIS = ToiletType.register(Blocks.LAPIS_BLOCK, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.lapis_lazuli");
    public static final ToiletType REDSTONE = ToiletType.register(Blocks.REDSTONE_BLOCK, ToiletType.Category.HARD).redstone().nameKey("block.poopsky.toilet.redstone");
    public static final ToiletType DIAMOND = ToiletType.register(Blocks.DIAMOND_BLOCK, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.diamond");
    public static final ToiletType EMERALD = ToiletType.register(Blocks.EMERALD_BLOCK, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.emerald");
    public static final ToiletType NETHERITE = ToiletType.register(Blocks.NETHERITE_BLOCK, ToiletType.Category.HARD).nameKey("block.poopsky.toilet.netherite");
    public static final ToiletType OBSIDIAN = ToiletType.register(Blocks.OBSIDIAN, ToiletType.Category.HARD);
    public static final ToiletType CRYING_OBSIDIAN = ToiletType.register(Blocks.CRYING_OBSIDIAN, ToiletType.Category.HARD);

    private ToiletTypes() {
    }
    public static void init() {
    }
}
