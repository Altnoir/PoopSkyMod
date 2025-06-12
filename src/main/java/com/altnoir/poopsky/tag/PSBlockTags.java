package com.altnoir.poopsky.tag;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class PSBlockTags {
    public static final TagKey<Block> TOILET_BLOCKS = create("toilet_blocks");
    public static final TagKey<Block> POOP_BLOCKS = create("poop_blocks");
    public static final TagKey<Block> CONVERTABLE_TO_MOSS = create("convertable_to_moss");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, name));
    }
}
