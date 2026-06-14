package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.AllToiletBlocks;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.tag.PSItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
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
        tag(PSItemTags.POOPS)
                .add(PSItems.POOP.get())
                .add(PSItems.CHILI_POOP.get())
                .add(PSItems.GOLDEN_POOP.get());
        tag(PSItemTags.TOILET_BLOCKS)
                .add(WOODEN_TOILETS)
                .add(HARD_TOILETS);

        // 原版Tags
        tag(Tags.Items.FOODS)
                .add(PSItems.POOP.get())
                .add(PSItems.CHILI_POOP.get())
                .add(PSItems.GOLDEN_POOP.get())
                .add(PSItems.SAPLING_POOP_BALL.get())
                .add(PSItems.BAKED_MAGGOTS.get())
                .add(PSItems.MAGGOTS_SEEDS.get())
                .add(PSItems.ROUNDWORM.get())
                .add(PSItems.POOP_BREAD.get())
                .add(PSItems.POOP_DUMPLINGS.get())
                .add(PSItems.POOP_MOONCAKE.get())
                .add(PSItems.CHILI_POOP_MOONCAKE.get())
                .add(PSItems.GOLDEN_POOP_MOONCAKE.get())
                .add(PSItems.POOP_SOUP.get())
                .add(PSItems.POOP_VEGETABLE_STICKS.get())
                .add(PSItems.POOBURGER_MEAT.get())
                .add(PSItems.POOBURGER.get())
                .add(PSItems.POOP_PASTA.get())
                .add(PSItems.POODDING.get())
                .add(PSItems.DRAGON_BREATH_CHILI.get())
                .add(PSItems.KING_OF_DRAGON_FRUIT.get())
                .add(PSItems.URINE_BOTTLE.get())
                .add(PSBlocks.POOP_CAKE.get().asItem());

        tag(Tags.Items.FOODS_FOOD_POISONING)
                .add(PSItems.POOP.get())
                .add(PSItems.CHILI_POOP.get())
                .add(PSItems.GOLDEN_POOP.get())
                .add(PSItems.SAPLING_POOP_BALL.get())
                .add(PSItems.POOP_BREAD.get())
                .add(PSItems.POOP_DUMPLINGS.get())
                .add(PSItems.POOP_MOONCAKE.get())
                .add(PSItems.CHILI_POOP_MOONCAKE.get())
                .add(PSItems.GOLDEN_POOP_MOONCAKE.get())
                .add(PSItems.POOP_SOUP.get())
                .add(PSItems.POOP_VEGETABLE_STICKS.get())
                .add(PSItems.POOBURGER_MEAT.get())
                .add(PSItems.POOBURGER.get())
                .add(PSItems.POOP_PASTA.get())
                .add(PSItems.POODDING.get())
                .add(PSItems.URINE_BOTTLE.get())
                .add(PSBlocks.POOP_CAKE.asItem());
        tag(ItemTags.MEAT)
                .add(PSItems.MAGGOTS_SEEDS.get())
                .add(PSItems.ROUNDWORM.get())
                .add(PSItems.BAKED_MAGGOTS.get())
                .add(PSItems.POOBURGER_MEAT.get());
        tag(Tags.Items.FOODS_RAW_MEAT)
                .add(PSItems.MAGGOTS_SEEDS.get())
                .add(PSItems.ROUNDWORM.get());
        tag(Tags.Items.FOODS_COOKED_MEAT)
                .add(PSItems.BAKED_MAGGOTS.get());
        tag(PSItemTags.PASTA)
                .add(PSItems.ROUNDWORM.get())
                .add(PSItems.POOP_PASTA.get());

        tag(Tags.Items.GUNPOWDERS)
                .add(PSItems.KING_OF_DRAGON_FRUIT.get());

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(PSItems.OMEN_HELMET.get())
                .add(PSItems.OMEN_CHESTPLATE.get())
                .add(PSItems.OMEN_LEGGINGS.get())
                .add(PSItems.OMEN_BOOTS.get());

        tag(ItemTags.SWORDS).add(PSItems.MILOS_SWORD.get());

        tag(Tags.Items.MUSIC_DISCS)
                .add(PSItems.LAWRENCE_MUSIC_DISC.get())
                .add(PSItems.LIGHT_DANCE_MUSIC_DISC.get())
                .add(PSItems.MOON_BOWL_MUSIC_DISC.get());


        //方块物品标签
        tag(ItemTags.DIRT)
                .add(PSBlocks.POOP_BLOCK.asItem())
                .add(PSBlocks.CHILI_POOP_BLOCK.asItem())
                .add(PSBlocks.GOLDEN_POOP_BLOCK.asItem());

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
                .add(PSBlocks.GOLDEN_POOP_STAIRS.asItem())
                .add(PSBlocks.POOP_BRICK_STAIRS.asItem())
                .add(PSBlocks.MOSSY_POOP_BRICK_STAIRS.asItem())
                .add(PSBlocks.DRIED_POOP_BLOCK_STAIRS.asItem())
                .add(PSBlocks.SMOOTH_POOP_BLOCK_STAIRS.asItem())
                .add(PSBlocks.CUT_POOP_BLOCK_STAIRS.asItem());
        tag(ItemTags.SLABS)
                .add(PSBlocks.POOP_SLAB.asItem())
                .add(PSBlocks.CHILI_POOP_SLAB.asItem())
                .add(PSBlocks.GOLDEN_POOP_SLAB.asItem())
                .add(PSBlocks.POOP_BRICK_SLAB.asItem())
                .add(PSBlocks.MOSSY_POOP_BRICK_SLAB.asItem())
                .add(PSBlocks.DRIED_POOP_BLOCK_SLAB.asItem())
                .add(PSBlocks.SMOOTH_POOP_BLOCK_SLAB.asItem())
                .add(PSBlocks.CUT_POOP_BLOCK_SLAB.asItem());
        tag(ItemTags.WALLS)
                .add(PSBlocks.POOP_WALL.asItem())
                .add(PSBlocks.CHILI_POOP_BLOCK.asItem())
                .add(PSBlocks.GOLDEN_POOP_BLOCK.asItem())
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

    public static final Item[] WOODEN_TOILETS = {
            AllToiletBlocks.OAK_TOILET.get().asItem(),
            AllToiletBlocks.SPRUCE_TOILET.get().asItem(),
            AllToiletBlocks.BIRCH_TOILET.get().asItem(),
            AllToiletBlocks.JUNGLE_TOILET.get().asItem(),
            AllToiletBlocks.ACACIA_TOILET.get().asItem(),
            AllToiletBlocks.CHERRY_TOILET.get().asItem(),
            AllToiletBlocks.DARK_OAK_TOILET.get().asItem(),
            AllToiletBlocks.MANGROVE_TOILET.get().asItem(),
            AllToiletBlocks.BAMBOO_TOILET.get().asItem()
    };

    public static final Item[] HARD_TOILETS = {
            //石制
            AllToiletBlocks.STONE_TOILET.get().asItem(),
            AllToiletBlocks.COBBLESTONE_TOILET.get().asItem(),
            AllToiletBlocks.MOSSY_COBBLESTONE_TOILET.get().asItem(),
            AllToiletBlocks.SMOOTH_STONE_TOILET.get().asItem(),
            AllToiletBlocks.STONE_BRICK_TOILET.get().asItem(),
            AllToiletBlocks.MOSSY_STONE_BRICK_TOILET.get().asItem(),
            AllToiletBlocks.TILE_TOILET.get().asItem(),
            //混凝土
            AllToiletBlocks.WHITE_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.ORANGE_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.MAGENTA_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.YELLOW_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.LIME_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.PINK_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.GRAY_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.CYAN_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.PURPLE_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.BLUE_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.BROWN_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.GREEN_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.RED_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.BLACK_CONCRETE_TOILET.get().asItem(),
            AllToiletBlocks.RAINBOW_TOILET.get().asItem()
    };
}
