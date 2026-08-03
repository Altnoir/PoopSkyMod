package com.altnoir.poopsky.fabric;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.fabric.impl.tag.convention.v2.TagRegistration;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class FabricatedTags {
    public static class Blocks {
        public static final TagKey<Block> VILLAGER_JOB_SITES = ConventionalBlockTags.VILLAGER_JOB_SITES;
        public static final TagKey<Block> STRIPPED_LOGS = ConventionalBlockTags.STRIPPED_LOGS;
        public static final TagKey<Block> STRIPPED_WOODS = ConventionalBlockTags.STRIPPED_WOODS;
    }

    public static class Items {
        public static final TagKey<Item> BARRELS = ConventionalItemTags.BARRELS;
        public static final TagKey<Item> BUCKETS_EMPTY = ConventionalItemTags.EMPTY_BUCKETS;

        public static final TagKey<Item> FOODS = ConventionalItemTags.FOODS;
        public static final TagKey<Item> FOODS_RAW_MEAT = ConventionalItemTags.RAW_MEAT_FOODS;
        public static final TagKey<Item> FOODS_COOKED_MEAT = ConventionalItemTags.COOKED_MEAT_FOODS;
        public static final TagKey<Item> FOODS_FOOD_POISONING = ConventionalItemTags.FOOD_POISONING_FOODS;

        public static final TagKey<Item> GUNPOWDERS = ConventionalItemTags.GUNPOWDERS;

        public static final TagKey<Item> MUSIC_DISCS = ConventionalItemTags.MUSIC_DISCS;
        public static final TagKey<Item> STRIPPED_LOGS = ConventionalItemTags.STRIPPED_LOGS;
        public static final TagKey<Item> STRIPPED_WOODS = ConventionalItemTags.STRIPPED_WOODS;


        public static TagKey<Item> create(ResourceLocation resourceLocation) {
            return TagKey.create(Registries.ITEM, resourceLocation);
        }
    }
}
