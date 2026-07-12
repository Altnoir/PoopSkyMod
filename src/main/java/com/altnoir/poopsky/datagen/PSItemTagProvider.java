package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
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
        tag(PoTags.Items.POOPS)
                .add(PoItems.POOP.get())
                .add(PoItems.CHILI_POOP.get())
                .add(PoItems.GOLDEN_POOP.get());
        tag(PoTags.Items.TOILET_BLOCKS)
                .add(PoBlocks.WOODEN_TOILET.get().asItem())
                .add(PoBlocks.HARD_TOILET.get().asItem());
        tag(PoTags.Items.CAN_COMPOSTABLE)
                .add(PoItems.POOP.get())
                .add(PoItems.POOP_BALL.get())
                .add(PoBlocks.POOP_SAPLING.asItem())
                .add(PoBlocks.POOP_LEAVES.asItem())
                .add(PoBlocks.POOP_LEAVES.asItem())
                .add(PoBlocks.POOP_PIECE.asItem())
                .add(PoBlocks.POOP_BLOCK.asItem())
                .add(PoBlocks.POOLIME_MAGGOTS_BLOCK.asItem())
                .add(PoBlocks.POOP_STAIRS.asItem())
                .add(PoBlocks.POOP_SLAB.asItem())
                .add(PoBlocks.POOP_VERTICAL_SLAB.asItem())
                .add(PoBlocks.POOP_BUTTON.asItem())
                .add(PoBlocks.POOP_PRESSURE_PLATE.asItem())
                .add(PoBlocks.POOP_FENCE.asItem())
                .add(PoBlocks.POOP_FENCE_GATE.asItem())
                .add(PoBlocks.POOP_WALL.asItem())
                .add(PoBlocks.POOP_DOOR.asItem())
                .add(PoBlocks.POOP_TRAPDOOR.asItem())
                .add(PoBlocks.STOOL.asItem())
                .add(PoItems.MAGGOTS_SEEDS.get())
                .add(PoItems.ROUNDWORM.get())
                .add(PoItems.POOP_BREAD.get())
                .add(PoItems.POOP_DUMPLINGS.get())
                .add(PoItems.POOP_MOONCAKE.get())
                .add(PoItems.CHILI_POOP_MOONCAKE.get())
                .add(PoItems.GOLDEN_POOP_MOONCAKE.get())
                .add(PoItems.POOP_VEGETABLE_STICKS.get())
                .add(PoItems.POOBURGER_MEAT.get())
                .add(PoItems.POOBURGER.get())
                .add(PoItems.POOP_PASTA.get())
                .add(PoItems.POODDING.get())
                .add(PoBlocks.POOP_CAKE.asItem());

        // 原版Tags
        tag(Tags.Items.FOODS)
                .addTag(PoTags.Items.POOPS)
                .add(PoItems.SAPLING_POOP_BALL.get())
                .add(PoItems.BAKED_MAGGOTS.get())
                .add(PoItems.MAGGOTS_SEEDS.get())
                .add(PoItems.ROUNDWORM.get())
                .add(PoItems.POOP_BREAD.get())
                .add(PoItems.POOP_DUMPLINGS.get())
                .add(PoItems.POOP_MOONCAKE.get())
                .add(PoItems.CHILI_POOP_MOONCAKE.get())
                .add(PoItems.GOLDEN_POOP_MOONCAKE.get())
                .add(PoItems.POOP_SOUP.get())
                .add(PoItems.POOP_VEGETABLE_STICKS.get())
                .add(PoItems.POOBURGER_MEAT.get())
                .add(PoItems.POOBURGER.get())
                .add(PoItems.POOP_PASTA.get())
                .add(PoItems.POODDING.get())
                .add(PoItems.DRAGON_BREATH_CHILI.get())
                .add(PoItems.KING_OF_DRAGON_FRUIT.get())
                .add(PoItems.URINE_BOTTLE.get())
                .add(PoBlocks.POOP_CAKE.get().asItem());

        tag(Tags.Items.FOODS_FOOD_POISONING)
                .addTag(PoTags.Items.POOPS)
                .add(PoItems.SAPLING_POOP_BALL.get())
                .add(PoItems.POOP_BREAD.get())
                .add(PoItems.POOP_DUMPLINGS.get())
                .add(PoItems.POOP_MOONCAKE.get())
                .add(PoItems.CHILI_POOP_MOONCAKE.get())
                .add(PoItems.GOLDEN_POOP_MOONCAKE.get())
                .add(PoItems.POOP_SOUP.get())
                .add(PoItems.POOP_VEGETABLE_STICKS.get())
                .add(PoItems.POOBURGER_MEAT.get())
                .add(PoItems.POOBURGER.get())
                .add(PoItems.POOP_PASTA.get())
                .add(PoItems.POODDING.get())
                .add(PoItems.URINE_BOTTLE.get())
                .add(PoBlocks.POOP_CAKE.asItem());
        tag(ItemTags.WOLF_FOOD)
                .addTag(PoTags.Items.POOPS);
        tag(ItemTags.MEAT)
                .add(PoItems.MAGGOTS_SEEDS.get())
                .add(PoItems.ROUNDWORM.get())
                .add(PoItems.BAKED_MAGGOTS.get())
                .add(PoItems.POOBURGER_MEAT.get());
        tag(Tags.Items.FOODS_RAW_MEAT)
                .add(PoItems.MAGGOTS_SEEDS.get())
                .add(PoItems.ROUNDWORM.get());
        tag(Tags.Items.FOODS_COOKED_MEAT)
                .add(PoItems.BAKED_MAGGOTS.get());

        tag(Tags.Items.GUNPOWDERS)
                .add(PoItems.KING_OF_DRAGON_FRUIT.get());

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(PoItems.OMEN_HELMET.get())
                .add(PoItems.OMEN_CHESTPLATE.get())
                .add(PoItems.OMEN_LEGGINGS.get())
                .add(PoItems.OMEN_BOOTS.get());

        tag(ItemTags.SWORDS).add(
                PoItems.MILOS_SWORD.get(),
                PoItems.TOILET_PLUG.get(),
                PoItems.TOILET_PLUG_WAND.get()
        );
        tag(ItemTags.FOOT_ARMOR).add(PoItems.OMEN_BOOTS.get());
        tag(ItemTags.LEG_ARMOR).add(PoItems.OMEN_LEGGINGS.get());
        tag(ItemTags.CHEST_ARMOR).add(PoItems.OMEN_CHESTPLATE.get());
        tag(ItemTags.HEAD_ARMOR).add(PoItems.OMEN_HELMET.get());

        tag(Tags.Items.MUSIC_DISCS)
                .add(PoItems.LAWRENCE_MUSIC_DISC.get())
                .add(PoItems.LIGHT_DANCE_MUSIC_DISC.get())
                .add(PoItems.MOON_BOWL_MUSIC_DISC.get());

        tag(PoTags.Items.PASTA)
                .add(PoItems.ROUNDWORM.get())
                .add(PoItems.POOP_PASTA.get());
        tag(PoTags.Items.SOUP)
                .add(PoItems.POOP_SOUP.get());
        tag(PoTags.Items.UPRIGHT_ON_BELT).replace(false)
                .add(PoItems.POOP_BREAD.get())
                .add(PoItems.POOP_DUMPLINGS.get())
                .add(PoItems.POOP_MOONCAKE.get())
                .add(PoItems.CHILI_POOP_MOONCAKE.get())
                .add(PoItems.GOLDEN_POOP_MOONCAKE.get())
                .add(PoItems.POOP_SOUP.get())
                .add(PoItems.POOP_VEGETABLE_STICKS.get())
                .add(PoItems.POOBURGER_MEAT.get())
                .add(PoItems.POOBURGER.get())
                .add(PoItems.POOP_PASTA.get())
                .add(PoItems.POODDING.get())
                .add(PoItems.DRAGON_BREATH_CHILI.get())
                .add(PoItems.KING_OF_DRAGON_FRUIT.get())
                .add(PoItems.URINE_BOTTLE.get())
                .add(PoBlocks.POOP_CAKE.get().asItem());

        //方块物品标签
        tag(ItemTags.DIRT)
                .add(PoBlocks.POOP_BLOCK.asItem())
                .add(PoBlocks.CHILI_POOP_BLOCK.asItem())
                .add(PoBlocks.GOLDEN_POOP_BLOCK.asItem());

        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(PoItems.MAGGOTS_SEEDS.get());
        tag(ItemTags.SAPLINGS)
                .add(PoBlocks.POOP_SAPLING.asItem());
        tag(ItemTags.LEAVES)
                .add(PoBlocks.POOP_LEAVES.asItem())
                .add(PoBlocks.POOP_LEAVES_IRON.asItem())
                .add(PoBlocks.POOP_LEAVES_GOLD.asItem());
        tag(ItemTags.LOGS)
                .add(PoBlocks.POOP_LOG.asItem())
                .add(PoBlocks.POOP_EMPTY_LOG.asItem())
                .add(PoBlocks.STRIPPED_POOP_LOG.asItem())
                .add(PoBlocks.STRIPPED_POOP_EMPTY_LOG.asItem());
        tag(ItemTags.STAIRS)
                .add(PoBlocks.POOP_STAIRS.asItem())
                .add(PoBlocks.CHILI_POOP_STAIRS.asItem())
                .add(PoBlocks.GOLDEN_POOP_STAIRS.asItem())
                .add(PoBlocks.POOP_BRICK_STAIRS.asItem())
                .add(PoBlocks.MOSSY_POOP_BRICK_STAIRS.asItem())
                .add(PoBlocks.DRIED_POOP_BLOCK_STAIRS.asItem())
                .add(PoBlocks.SMOOTH_POOP_BLOCK_STAIRS.asItem())
                .add(PoBlocks.CUT_POOP_BLOCK_STAIRS.asItem());
        tag(ItemTags.SLABS)
                .add(PoBlocks.POOP_SLAB.asItem())
                .add(PoBlocks.CHILI_POOP_SLAB.asItem())
                .add(PoBlocks.GOLDEN_POOP_SLAB.asItem())
                .add(PoBlocks.POOP_BRICK_SLAB.asItem())
                .add(PoBlocks.MOSSY_POOP_BRICK_SLAB.asItem())
                .add(PoBlocks.DRIED_POOP_BLOCK_SLAB.asItem())
                .add(PoBlocks.SMOOTH_POOP_BLOCK_SLAB.asItem())
                .add(PoBlocks.CUT_POOP_BLOCK_SLAB.asItem());
        tag(ItemTags.WALLS)
                .add(PoBlocks.POOP_WALL.asItem())
                .add(PoBlocks.CHILI_POOP_BLOCK.asItem())
                .add(PoBlocks.GOLDEN_POOP_BLOCK.asItem())
                .add(PoBlocks.POOP_BRICK_WALL.asItem())
                .add(PoBlocks.MOSSY_POOP_BRICK_WALL.asItem())
                .add(PoBlocks.DRIED_POOP_BLOCK_WALL.asItem())
                .add(PoBlocks.SMOOTH_POOP_BLOCK_WALL.asItem())
                .add(PoBlocks.CUT_POOP_BLOCK_WALL.asItem());
        tag(ItemTags.BUTTONS).add(PoBlocks.POOP_BUTTON.asItem());
        tag(ItemTags.FENCES).add(PoBlocks.POOP_FENCE.asItem());
        tag(ItemTags.FENCE_GATES).add(PoBlocks.POOP_FENCE_GATE.asItem());
        tag(ItemTags.DOORS).add(PoBlocks.POOP_DOOR.asItem());
        tag(ItemTags.TRAPDOORS).add(PoBlocks.POOP_TRAPDOOR.asItem());
    }
}
