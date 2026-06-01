package com.altnoir.poopsky.tag;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class PSItemTags {
    public static final TagKey<Item> POOPS = create("poops");
    public static final TagKey<Item> TOILET_BLOCKS = create("toilet_blocks");

    public static final TagKey<Item> PASTA = tag("foods/pasta");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, PoopSky.loc(name));
    }
    private static TagKey<Item> tag(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
    }
}
