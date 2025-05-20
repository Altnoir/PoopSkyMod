package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.tag.PSItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class PSItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public PSItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ConventionalItemTags.FOOD_POISONING_FOODS)
                .add(PSItems.POOP)
                .add(PSItems.POOP_BREAD)
                .add(PSItems.POOP_SOUP)
                .add(PSBlocks.POOP_CAKE.asItem());
        getOrCreateTagBuilder(ConventionalItemTags.RAW_MEAT_FOODS)
                .add(PSItems.MAGGOTS_SEEDS);
        getOrCreateTagBuilder(ConventionalItemTags.COOKED_MEAT_FOODS)
                .add(PSItems.BAKED_MAGGOTS);
        getOrCreateTagBuilder(ConventionalItemTags.FOODS)
                .add(PSItems.DRAGON_BREATH_CHILI);

        getOrCreateTagBuilder(ConventionalItemTags.MUSIC_DISCS)
                .add(PSItems.LAWRENCE_MUSIC_DISC);

        getOrCreateTagBuilder(PSItemTags.COMPOOPER_SAPLINGS)
                .add(Items.OAK_SAPLING)
                .add(Items.SPRUCE_SAPLING)
                .add(Items.BIRCH_SAPLING)
                .add(Items.JUNGLE_SAPLING)
                .add(Items.ACACIA_SAPLING)
                .add(Items.DARK_OAK_SAPLING)
                .add(Items.MANGROVE_PROPAGULE)
                .add(Items.CHERRY_SAPLING);

        //方块基础标签
        getOrCreateTagBuilder(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(PSItems.MAGGOTS_SEEDS);
        getOrCreateTagBuilder(ItemTags.SAPLINGS)
                .add(PSBlocks.POOP_SAPLING.asItem());
        getOrCreateTagBuilder(ItemTags.LEAVES)
                .add(PSBlocks.POOP_LEAVES.asItem())
                .add(PSBlocks.POOP_LEAVES_IRON.asItem())
                .add(PSBlocks.POOP_LEAVES_GOLD.asItem());
        getOrCreateTagBuilder(ItemTags.LOGS)
                .add(PSBlocks.POOP_LOG.asItem())
                .add(PSBlocks.POOP_EMPTY_LOG.asItem())
                .add(PSBlocks.STRIPPED_POOP_LOG.asItem())
                .add(PSBlocks.STRIPPED_POOP_EMPTY_LOG.asItem());
        getOrCreateTagBuilder(ItemTags.STAIRS)
                .add(PSBlocks.POOP_STAIRS.asItem())
                .add(PSBlocks.POOP_BRICK_STAIRS.asItem())
                .add(PSBlocks.MOSSY_POOP_BRICK_STAIRS.asItem())
                .add(PSBlocks.DRIED_POOP_BLOCK_STAIRS.asItem())
                .add(PSBlocks.SMOOTH_POOP_BLOCK_STAIRS.asItem())
                .add(PSBlocks.CUT_POOP_BLOCK_STAIRS.asItem());
        getOrCreateTagBuilder(ItemTags.SLABS)
                .add(PSBlocks.POOP_SLAB.asItem())
                .add(PSBlocks.POOP_BRICK_SLAB.asItem())
                .add(PSBlocks.MOSSY_POOP_BRICK_SLAB.asItem())
                .add(PSBlocks.DRIED_POOP_BLOCK_SLAB.asItem())
                .add(PSBlocks.SMOOTH_POOP_BLOCK_SLAB.asItem())
                .add(PSBlocks.CUT_POOP_BLOCK_SLAB.asItem());
        getOrCreateTagBuilder(ItemTags.WALLS)
                .add(PSBlocks.POOP_WALL.asItem())
                .add(PSBlocks.POOP_BRICK_WALL.asItem())
                .add(PSBlocks.MOSSY_POOP_BRICK_WALL.asItem())
                .add(PSBlocks.DRIED_POOP_BLOCK_WALL.asItem())
                .add(PSBlocks.SMOOTH_POOP_BLOCK_WALL.asItem())
                .add(PSBlocks.CUT_POOP_BLOCK_WALL.asItem());
        getOrCreateTagBuilder(ItemTags.BUTTONS).add(PSBlocks.POOP_BUTTON.asItem());
        getOrCreateTagBuilder(ItemTags.FENCES).add(PSBlocks.POOP_FENCE.asItem());
        getOrCreateTagBuilder(ItemTags.FENCE_GATES).add(PSBlocks.POOP_FENCE_GATE.asItem());
        getOrCreateTagBuilder(ItemTags.DOORS).add(PSBlocks.POOP_DOOR.asItem());
        getOrCreateTagBuilder(ItemTags.TRAPDOORS).add(PSBlocks.POOP_TRAPDOOR.asItem());
    }
}
