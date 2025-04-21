package com.altnoir.poopsky.tag;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class PSBlockTags {
    public static final TagKey<Block> TOILET_BLOCKS = of("toilet_blocks");
    public static final TagKey<Block> POOP_BLOCKS = of("poop_blocks");
    private static TagKey<Block> of(String id) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(PoopSky.MOD_ID, id));
    }
}
