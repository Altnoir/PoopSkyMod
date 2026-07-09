package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PItems;
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
        tag(PTags.Items.POOPS)
                .add(PItems.POOP.get())
                .add(PItems.CHILI_POOP.get())
                .add(PItems.GOLDEN_POOP.get());
        tag(PTags.Items.TOILET_BLOCKS)
                .add(PBlocks.WOODEN_TOILET.get().asItem())
                .add(PBlocks.HARD_TOILET.get().asItem());
        tag(PTags.Items.CAN_COMPOSTABLE)
                .add(PItems.POOP.get())
                .add(PItems.POOP_BALL.get())
                .add(PBlocks.POOP_SAPLING.asItem())
                .add(PBlocks.POOP_LEAVES.asItem())
                .add(PBlocks.POOP_LEAVES.asItem())
                .add(PBlocks.POOP_PIECE.asItem())
                .add(PBlocks.POOP_BLOCK.asItem())
                .add(PBlocks.POOLIME_MAGGOTS_BLOCK.asItem())
                .add(PBlocks.POOP_STAIRS.asItem())
                .add(PBlocks.POOP_SLAB.asItem())
                .add(PBlocks.POOP_VERTICAL_SLAB.asItem())
                .add(PBlocks.POOP_BUTTON.asItem())
                .add(PBlocks.POOP_PRESSURE_PLATE.asItem())
                .add(PBlocks.POOP_FENCE.asItem())
                .add(PBlocks.POOP_FENCE_GATE.asItem())
                .add(PBlocks.POOP_WALL.asItem())
                .add(PBlocks.POOP_DOOR.asItem())
                .add(PBlocks.POOP_TRAPDOOR.asItem())
                .add(PBlocks.STOOL.asItem())
                .add(PItems.MAGGOTS_SEEDS.get())
                .add(PItems.ROUNDWORM.get())
                .add(PItems.POOP_BREAD.get())
                .add(PItems.POOP_DUMPLINGS.get())
                .add(PItems.POOP_MOONCAKE.get())
                .add(PItems.CHILI_POOP_MOONCAKE.get())
                .add(PItems.GOLDEN_POOP_MOONCAKE.get())
                .add(PItems.POOP_VEGETABLE_STICKS.get())
                .add(PItems.POOBURGER_MEAT.get())
                .add(PItems.POOBURGER.get())
                .add(PItems.POOP_PASTA.get())
                .add(PItems.POODDING.get())
                .add(PBlocks.POOP_CAKE.asItem());

        // 原版Tags
        tag(Tags.Items.FOODS)
                .addTag(PTags.Items.POOPS)
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
                .addTag(PTags.Items.POOPS)
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
        tag(ItemTags.WOLF_FOOD)
                .addTag(PTags.Items.POOPS);
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

        tag(Tags.Items.GUNPOWDERS)
                .add(PItems.KING_OF_DRAGON_FRUIT.get());

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(PItems.OMEN_HELMET.get())
                .add(PItems.OMEN_CHESTPLATE.get())
                .add(PItems.OMEN_LEGGINGS.get())
                .add(PItems.OMEN_BOOTS.get());

        tag(ItemTags.SWORDS).add(
                PItems.MILOS_SWORD.get(),
                PItems.TOILET_PLUG.get(),
                PItems.TOILET_PLUG_WAND.get()
        );
        tag(ItemTags.FOOT_ARMOR).add(PItems.OMEN_BOOTS.get());
        tag(ItemTags.LEG_ARMOR).add(PItems.OMEN_LEGGINGS.get());
        tag(ItemTags.CHEST_ARMOR).add(PItems.OMEN_CHESTPLATE.get());
        tag(ItemTags.HEAD_ARMOR).add(PItems.OMEN_HELMET.get());

        tag(Tags.Items.MUSIC_DISCS)
                .add(PItems.LAWRENCE_MUSIC_DISC.get())
                .add(PItems.LIGHT_DANCE_MUSIC_DISC.get())
                .add(PItems.MOON_BOWL_MUSIC_DISC.get());

        tag(PTags.Items.PASTA)
                .add(PItems.ROUNDWORM.get())
                .add(PItems.POOP_PASTA.get());
        tag(PTags.Items.SOUP)
                .add(PItems.POOP_SOUP.get());
        tag(PTags.Items.UPRIGHT_ON_BELT).replace(false)
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
}
