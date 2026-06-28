package com.altnoir.poopsky;

import com.altnoir.poopsky.compat.PSMods;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class PTags {
    public interface Blocks {
        TagKey<Block> TOILET_BLOCKS = create("toilet_blocks");
        TagKey<Block> GOLDEN_TOILET_BLOCKS = create("golden_toilet_blocks");
        TagKey<Block> POOP_BLOCKS = create("poop_blocks");
        TagKey<Block> POOP_BUILDING_BLOCKS = create("poop_building_blocks");
        TagKey<Block> EMPTY_LOGS = create("empty_logs");

        TagKey<Block> POOP_BLOCK = create("poop_block");
        TagKey<Block> CHILI_POOP_BLOCK = create("chili_poop_block");
        TagKey<Block> RAW_SAPLING_POOP_BLOCK = create("raw_sapling_poop_block");
        TagKey<Block> RAW_SEA_POOP_BLOCK = create("raw_sea_poop_block");
        TagKey<Block> RAW_WITHER_POOP_BLOCK = create("raw_wither_poop_block");
        TagKey<Block> WATER_BLOCK = create("water_block");
        TagKey<Block> POOP_TNT_DESTROY = create("poop_tnt_destroy");
        TagKey<Block> POOP_TNT_REPLACEABLE = create("poop_tnt_replaceable");
        TagKey<Block> CONVERTABLE_TO_MOSS = create("convertable_to_moss");

        // Compat
        TagKey<Block> FAN_PROCESSING_CATALYSTS_DIGESTING = create("fan_processing_catalysts/digesting");

        private static TagKey<Block> create(String name) {
            return TagKey.create(Registries.BLOCK, PoopSky.loc(name));
        }
    }

    public interface Items {
        TagKey<Item> POOPS = create("poops");
        TagKey<Item> TOILET_BLOCKS = create("toilet_blocks");
        TagKey<Item> CAN_COMPOSTABLE = create("can_compooper");

        TagKey<Item> PASTA = tag("foods/pasta");
        TagKey<Item> SOUP = tag("foods/soup");

        TagKey<Item> UPRIGHT_ON_BELT = createCreate("upright_on_belt");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, PoopSky.loc(name));
        }

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Item> createCreate(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("create", name));
        }

    }

    public interface EntityTypes {
        TagKey<EntityType<?>> IGNORES_BLEEDING = create("ignore_bleeding");

        TagKey<EntityType<?>> RETAIN_IN_SUB_LEVEL = createSable("retain_in_sub_level");
        TagKey<EntityType<?>> DESTROY_WITH_SUB_LEVEL = createSable("destroy_with_sub_level");

        private static TagKey<EntityType<?>> create(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, PoopSky.loc(name));
        }

        private static TagKey<EntityType<?>> createSable(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, PSMods.SABLE.rl(name));
        }
    }

    public interface Fluids {
        TagKey<Fluid> FAN_PROCESSING_CATALYSTS_DIGESTING = create("fan_processing_catalysts/digesting");

        private static TagKey<Fluid> create(String name) {
            return TagKey.create(Registries.FLUID, PoopSky.loc(name));
        }
    }

    public interface DamageTypes {
        TagKey<DamageType> BYPASSES_BLEEDING = create("bypass_bleeding");

        private static TagKey<DamageType> create(String name) {
            return TagKey.create(Registries.DAMAGE_TYPE, PoopSky.loc(name));
        }
    }
}