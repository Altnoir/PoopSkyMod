package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PSItemTagProvider extends ItemTagsProvider {


    public PSItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Tags.Items.FOODS)
                .add(PSItems.POOP.get())
                .add(PSItems.CHILI_POOP.get())
                .add(PSItems.SAPING_BALL.get())
                .add(PSItems.BAKED_MAGGOTS.get())
                .add(PSItems.MAGGOTS_SEEDS.get())
                .add(PSItems.POOP_BREAD.get())
                .add(PSItems.POOP_DUMPLINGS.get())
                .add(PSItems.POOP_SOUP.get())
                .add(PSItems.POOP_VEGETABLE_STICKS.get())
                .add(PSItems.POOBURGER_MEAT.get())
                .add(PSItems.POOBURGER.get())
                .add(PSItems.POOP_PASTA.get())
                .add(PSItems.POODDING.get())
                .add(PSItems.DRAGON_BREATH_CHILI.get())
                .add(PSBlocks.POOP_CAKE.get().asItem());

        tag(Tags.Items.FOODS_FOOD_POISONING)
                .add(PSItems.POOP.get())
                .add(PSItems.POOP_BREAD.get())
                .add(PSItems.POOP_DUMPLINGS.get())
                .add(PSItems.POOP_SOUP.get())
                .add(PSItems.POOP_VEGETABLE_STICKS.get())
                .add(PSItems.POOBURGER_MEAT.get())
                .add(PSItems.POOBURGER.get())
                .add(PSItems.POOP_PASTA.get())
                .add(PSItems.POODDING.get())
                .add(PSBlocks.POOP_CAKE.asItem());
        tag(ItemTags.MEAT)
                .add(PSItems.MAGGOTS_SEEDS.get())
                .add(PSItems.BAKED_MAGGOTS.get())
                .add(PSItems.POOBURGER_MEAT.get());
        tag(Tags.Items.FOODS_RAW_MEAT)
                .add(PSItems.MAGGOTS_SEEDS.get());
        tag(Tags.Items.FOODS_COOKED_MEAT)
                .add(PSItems.BAKED_MAGGOTS.get());


        tag(Tags.Items.MUSIC_DISCS)
                .add(PSItems.LAWRENCE_MUSIC_DISC.get())
                .add(PSItems.LIGHT_DANCE_MUSIC_DISC.get())
                .add(PSItems.MOON_BOWL_MUSIC_DISC.get());

        //方块物品标签
        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(PSItems.MAGGOTS_SEEDS.get());
        tag(ItemTags.SAPLINGS)
                .add(PSBlocks.POOP_SAPLING.asItem());
        tag(ItemTags.LEAVES)
                .add(PSBlocks.POOP_LEAVES.asItem())
                .add(PSBlocks.POOP_LEAVES_IRON.asItem())
                .add(PSBlocks.POOP_LEAVES_GOLD.asItem());
        tag(ItemTags.LOGS)
                .add(PSBlocks.POOP_LOG.asItem())
                .add(PSBlocks.POOP_EMPTY_LOG.asItem())
                .add(PSBlocks.STRIPPED_POOP_LOG.asItem())
                .add(PSBlocks.STRIPPED_POOP_EMPTY_LOG.asItem());
        tag(ItemTags.STAIRS)
                .add(PSBlocks.POOP_STAIRS.asItem())
                .add(PSBlocks.CHILI_POOP_STAIRS.asItem())
                .add(PSBlocks.POOP_BRICK_STAIRS.asItem())
                .add(PSBlocks.MOSSY_POOP_BRICK_STAIRS.asItem())
                .add(PSBlocks.DRIED_POOP_BLOCK_STAIRS.asItem())
                .add(PSBlocks.SMOOTH_POOP_BLOCK_STAIRS.asItem())
                .add(PSBlocks.CUT_POOP_BLOCK_STAIRS.asItem());
        tag(ItemTags.SLABS)
                .add(PSBlocks.POOP_SLAB.asItem())
                .add(PSBlocks.CHILI_POOP_SLAB.asItem())
                .add(PSBlocks.POOP_BRICK_SLAB.asItem())
                .add(PSBlocks.MOSSY_POOP_BRICK_SLAB.asItem())
                .add(PSBlocks.DRIED_POOP_BLOCK_SLAB.asItem())
                .add(PSBlocks.SMOOTH_POOP_BLOCK_SLAB.asItem())
                .add(PSBlocks.CUT_POOP_BLOCK_SLAB.asItem());
        tag(ItemTags.WALLS)
                .add(PSBlocks.POOP_WALL.asItem())
                .add(PSBlocks.CHILI_POOP_BLOCK.asItem())
                .add(PSBlocks.POOP_BRICK_WALL.asItem())
                .add(PSBlocks.MOSSY_POOP_BRICK_WALL.asItem())
                .add(PSBlocks.DRIED_POOP_BLOCK_WALL.asItem())
                .add(PSBlocks.SMOOTH_POOP_BLOCK_WALL.asItem())
                .add(PSBlocks.CUT_POOP_BLOCK_WALL.asItem());
        tag(ItemTags.BUTTONS).add(PSBlocks.POOP_BUTTON.asItem());
        tag(ItemTags.FENCES).add(PSBlocks.POOP_FENCE.asItem());
        tag(ItemTags.FENCE_GATES).add(PSBlocks.POOP_FENCE_GATE.asItem());
        tag(ItemTags.DOORS).add(PSBlocks.POOP_DOOR.asItem());
        tag(ItemTags.TRAPDOORS).add(PSBlocks.POOP_TRAPDOOR.asItem());
    }
}
