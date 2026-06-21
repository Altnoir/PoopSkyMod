package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.AllToiletBlocks;
import com.altnoir.poopsky.block.PBlocks;
import com.altnoir.poopsky.item.PItems;
import com.altnoir.poopsky.PTags;
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
        tag(PTags.Items.POOPS)
                .add(PItems.POOP.get())
                .add(PItems.CHILI_POOP.get())
                .add(PItems.GOLDEN_POOP.get());
        tag(PTags.Items.TOILET_BLOCKS)
                .add(WOODEN_TOILETS)
                .add(HARD_TOILETS);

        // 原版Tags
        tag(Tags.Items.FOODS)
                .add(PItems.POOP.get())
                .add(PItems.CHILI_POOP.get())
                .add(PItems.GOLDEN_POOP.get())
                .add(PItems.SAPLING_POOP_BALL.get())
                .add(PItems.BAKED_MAGGOTS.get())
                .add(PItems.MAGGOTS_SEEDS.get())
                .add(PItems.ROUNDWORM.get())
                .add(PItems.POOP_BREAD.get())
                .add(PItems.POOP_DUMPLINGS.get())
                .add(PItems.POOP_MOONCAKE.get())
                .add(PItems.CHILI_POOP_MOONCAKE.get())
                .add(PItems.GOLDEN_POOP_MOONCAKE.get())
                .add(PItems.POOP_SOUP.get())
                .add(PItems.POOP_VEGETABLE_STICKS.get())
                .add(PItems.POOBURGER_MEAT.get())
                .add(PItems.POOBURGER.get())
                .add(PItems.POOP_PASTA.get())
                .add(PItems.POODDING.get())
                .add(PItems.DRAGON_BREATH_CHILI.get())
                .add(PItems.KING_OF_DRAGON_FRUIT.get())
                .add(PItems.URINE_BOTTLE.get())
                .add(PBlocks.POOP_CAKE.get().asItem());

        tag(Tags.Items.FOODS_FOOD_POISONING)
                .add(PItems.POOP.get())
                .add(PItems.CHILI_POOP.get())
                .add(PItems.GOLDEN_POOP.get())
                .add(PItems.SAPLING_POOP_BALL.get())
                .add(PItems.POOP_BREAD.get())
                .add(PItems.POOP_DUMPLINGS.get())
                .add(PItems.POOP_MOONCAKE.get())
                .add(PItems.CHILI_POOP_MOONCAKE.get())
                .add(PItems.GOLDEN_POOP_MOONCAKE.get())
                .add(PItems.POOP_SOUP.get())
                .add(PItems.POOP_VEGETABLE_STICKS.get())
                .add(PItems.POOBURGER_MEAT.get())
                .add(PItems.POOBURGER.get())
                .add(PItems.POOP_PASTA.get())
                .add(PItems.POODDING.get())
                .add(PItems.URINE_BOTTLE.get())
                .add(PBlocks.POOP_CAKE.asItem());
        tag(ItemTags.MEAT)
                .add(PItems.MAGGOTS_SEEDS.get())
                .add(PItems.ROUNDWORM.get())
                .add(PItems.BAKED_MAGGOTS.get())
                .add(PItems.POOBURGER_MEAT.get());
        tag(Tags.Items.FOODS_RAW_MEAT)
                .add(PItems.MAGGOTS_SEEDS.get())
                .add(PItems.ROUNDWORM.get());
        tag(Tags.Items.FOODS_COOKED_MEAT)
                .add(PItems.BAKED_MAGGOTS.get());
        tag(PTags.Items.PASTA)
                .add(PItems.ROUNDWORM.get())
                .add(PItems.POOP_PASTA.get());

        tag(Tags.Items.GUNPOWDERS)
                .add(PItems.KING_OF_DRAGON_FRUIT.get());

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(PItems.OMEN_HELMET.get())
                .add(PItems.OMEN_CHESTPLATE.get())
                .add(PItems.OMEN_LEGGINGS.get())
                .add(PItems.OMEN_BOOTS.get());

        tag(ItemTags.SWORDS).add(PItems.MILOS_SWORD.get());
        tag(ItemTags.FOOT_ARMOR).add(PItems.OMEN_BOOTS.get());
        tag(ItemTags.LEG_ARMOR).add(PItems.OMEN_LEGGINGS.get());
        tag(ItemTags.CHEST_ARMOR).add(PItems.OMEN_CHESTPLATE.get());
        tag(ItemTags.HEAD_ARMOR).add(PItems.OMEN_HELMET.get());

        tag(Tags.Items.MUSIC_DISCS)
                .add(PItems.LAWRENCE_MUSIC_DISC.get())
                .add(PItems.LIGHT_DANCE_MUSIC_DISC.get())
                .add(PItems.MOON_BOWL_MUSIC_DISC.get());


        //方块物品标签
        tag(ItemTags.DIRT)
                .add(PBlocks.POOP_BLOCK.asItem())
                .add(PBlocks.CHILI_POOP_BLOCK.asItem())
                .add(PBlocks.GOLDEN_POOP_BLOCK.asItem());

        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(PItems.MAGGOTS_SEEDS.get());
        tag(ItemTags.SAPLINGS)
                .add(PBlocks.POOP_SAPLING.asItem());
        tag(ItemTags.LEAVES)
                .add(PBlocks.POOP_LEAVES.asItem())
                .add(PBlocks.POOP_LEAVES_IRON.asItem())
                .add(PBlocks.POOP_LEAVES_GOLD.asItem());
        tag(ItemTags.LOGS)
                .add(PBlocks.POOP_LOG.asItem())
                .add(PBlocks.POOP_EMPTY_LOG.asItem())
                .add(PBlocks.STRIPPED_POOP_LOG.asItem())
                .add(PBlocks.STRIPPED_POOP_EMPTY_LOG.asItem());
        tag(ItemTags.STAIRS)
                .add(PBlocks.POOP_STAIRS.asItem())
                .add(PBlocks.CHILI_POOP_STAIRS.asItem())
                .add(PBlocks.GOLDEN_POOP_STAIRS.asItem())
                .add(PBlocks.POOP_BRICK_STAIRS.asItem())
                .add(PBlocks.MOSSY_POOP_BRICK_STAIRS.asItem())
                .add(PBlocks.DRIED_POOP_BLOCK_STAIRS.asItem())
                .add(PBlocks.SMOOTH_POOP_BLOCK_STAIRS.asItem())
                .add(PBlocks.CUT_POOP_BLOCK_STAIRS.asItem());
        tag(ItemTags.SLABS)
                .add(PBlocks.POOP_SLAB.asItem())
                .add(PBlocks.CHILI_POOP_SLAB.asItem())
                .add(PBlocks.GOLDEN_POOP_SLAB.asItem())
                .add(PBlocks.POOP_BRICK_SLAB.asItem())
                .add(PBlocks.MOSSY_POOP_BRICK_SLAB.asItem())
                .add(PBlocks.DRIED_POOP_BLOCK_SLAB.asItem())
                .add(PBlocks.SMOOTH_POOP_BLOCK_SLAB.asItem())
                .add(PBlocks.CUT_POOP_BLOCK_SLAB.asItem());
        tag(ItemTags.WALLS)
                .add(PBlocks.POOP_WALL.asItem())
                .add(PBlocks.CHILI_POOP_BLOCK.asItem())
                .add(PBlocks.GOLDEN_POOP_BLOCK.asItem())
                .add(PBlocks.POOP_BRICK_WALL.asItem())
                .add(PBlocks.MOSSY_POOP_BRICK_WALL.asItem())
                .add(PBlocks.DRIED_POOP_BLOCK_WALL.asItem())
                .add(PBlocks.SMOOTH_POOP_BLOCK_WALL.asItem())
                .add(PBlocks.CUT_POOP_BLOCK_WALL.asItem());
        tag(ItemTags.BUTTONS).add(PBlocks.POOP_BUTTON.asItem());
        tag(ItemTags.FENCES).add(PBlocks.POOP_FENCE.asItem());
        tag(ItemTags.FENCE_GATES).add(PBlocks.POOP_FENCE_GATE.asItem());
        tag(ItemTags.DOORS).add(PBlocks.POOP_DOOR.asItem());
        tag(ItemTags.TRAPDOORS).add(PBlocks.POOP_TRAPDOOR.asItem());
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
