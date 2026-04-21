package com.altnoir.poopsky.tag;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PSItemTags {
    public static final TagKey<Item> POOPS = create("poops");
    public static final TagKey<Item> TOILET_BLOCKS = create("toilet_blocks");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, name));
    }
}
