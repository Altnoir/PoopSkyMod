package com.altnoir.poopsky.tag;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class PBlockTags {
    public static final TagKey<Block> TOILET_BLOCKS = create("toilet_blocks");
    public static final TagKey<Block> GOLDEN_TOILET_BLOCKS = create("golden_toilet_blocks");
    public static final TagKey<Block> POOP_BLOCKS = create("poop_blocks");
    public static final TagKey<Block> POOP_BUILDING_BLOCKS = create("poop_building_blocks");
    public static final TagKey<Block> EMPTY_LOGS = create("empty_logs");

    public static final TagKey<Block> POOP_BLOCK = create("poop_block");
    public static final TagKey<Block> CHILI_POOP_BLOCK = create("chili_poop_block");
    public static final TagKey<Block> RAW_SAPLING_POOP_BLOCK = create("raw_saping_poop_block");
    public static final TagKey<Block> RAW_SEA_POOP_BLOCK = create("raw_sea_poop_block");
    public static final TagKey<Block> RAW_WITHER_POOP_BLOCK = create("raw_wither_poop_block");
    public static final TagKey<Block> WATER_BLOCK = create("water_block");
    public static final TagKey<Block> POOP_TNT_DESTROY = create("poop_tnt_destroy");
    public static final TagKey<Block> POOP_TNT_REPLACEABLE = create("poop_tnt_replaceable");
    public static final TagKey<Block> CONVERTABLE_TO_MOSS = create("convertable_to_moss");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, PoopSky.loc(name));
    }
}
