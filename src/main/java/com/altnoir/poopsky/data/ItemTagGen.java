package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public final class ItemTagGen {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();
    private static RegistrateItemTagsProvider provider;

    private ItemTagGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, ItemTagGen::generate);
    }

    private static void generate(RegistrateItemTagsProvider provider) {
        ItemTagGen.provider = provider;
        tag(PoTags.Items.POOPS)
                .add(
                        PoItems.POOP.get(),
                        PoItems.CHILI_POOP.get(),
                        PoItems.GOLDEN_POOP.get()
                );
        copy(PoTags.Blocks.POOP_BLOCKS, PoTags.Items.POOP_BLOCKS);
        tag(PoTags.Items.SHITS)
                .add(
                        PoBlocks.SHIT.asItem(),
                        PoBlocks.CHILI_SHIT.asItem(),
                        PoBlocks.GOLDEN_SHIT.asItem()
                );
        copy(PoTags.Blocks.TOILET_BLOCKS, PoTags.Items.TOILET_BLOCKS);
        copy(PoTags.Blocks.FLUSH_TOILET_BLOCKS, PoTags.Items.FLUSH_TOILET_BLOCKS);
        copy(PoTags.Blocks.GINKGO_LOGS, PoTags.Items.GINKGO_LOGS);

        var tileBlocks = tag(PoTags.Items.TILE_BLOCKS);
        var tileStairs = tag(PoTags.Items.TILE_STAIRS);
        var tileSlabs = tag(PoTags.Items.TILE_SLABS);
        var tileVerticalSlabs = tag(PoTags.Items.TILE_VERTICAL_SLABS);
        var tileWalls = tag(PoTags.Items.TILE_WALLS);
        PoBlocks.COLORED_TILE_BLOCK_FAMILIES.forEach(family -> {
            tileBlocks.add(family.block().get().asItem());
            tileStairs.add(family.stairs().get().asItem());
            tileSlabs.add(family.slab().get().asItem());
            tileVerticalSlabs.add(family.verticalSlab().get().asItem());
            tileWalls.add(family.wall().get().asItem());
        });

        copy(PoTags.Blocks.ARCADES, PoTags.Items.ARCADES);
        tag(PoTags.Items.GAME_DISKS)
                .add(
                        PoItems.GAME_DISC_ROUNDWORM.get(),
                        PoItems.GAME_DISC_BLOCKTRIS.get(),
                        PoItems.GAME_DISC_PONG.get()
                );
        tag(PoTags.Items.FLUSH_TOILET_SAVE).addTag(PoTags.Items.POOPS).addTag(PoTags.Items.SHITS);
        copy(PoTags.Blocks.BREEDING_CHEST_ACCELERATOR, PoTags.Items.BREEDING_CHEST_ACCELERATOR);
        copy(PoTags.Blocks.BREEDING_CHEST_PARALLELISM, PoTags.Items.BREEDING_CHEST_PARALLELISM);
        tag(PoTags.Items.FLY_LIKE).addTag(PoTags.Items.FLUSH_TOILET_SAVE);
        tag(PoTags.Items.VILLAGER_LIKE).addTag(PoTags.Items.FLUSH_TOILET_SAVE);
        tag(PoTags.Items.REPAIRS_OMEN_ARMOR).add(PoItems.OMINOUS_FILTHY_INGOT.get());

        tag(PoTags.Items.CAN_COMPOSTABLE)
                .add(
                        PoItems.POOP.get(),
                        PoItems.POOP_BALL.get(),
                        PoBlocks.POOP_SAPLING.asItem(),
                        PoBlocks.POOP_LEAVES_IRON.asItem(),
                        PoBlocks.POOP_LEAVES_GOLD.asItem(),
                        PoBlocks.POOP_PIECE.asItem(),
                        PoBlocks.POOP_BLOCK.asItem(),
                        PoBlocks.POOLIME_MAGGOTS_BLOCK.asItem(),
                        PoBlocks.POOP_STAIRS.asItem(),
                        PoBlocks.POOP_SLAB.asItem(),
                        PoBlocks.POOP_VERTICAL_SLAB.asItem(),
                        PoBlocks.POOP_BUTTON.asItem(),
                        PoBlocks.POOP_PRESSURE_PLATE.asItem(),
                        PoBlocks.POOP_FENCE.asItem(),
                        PoBlocks.POOP_FENCE_GATE.asItem(),
                        PoBlocks.POOP_WALL.asItem(),
                        PoBlocks.POOP_DOOR.asItem(),
                        PoBlocks.POOP_TRAPDOOR.asItem(),
                        PoBlocks.STOOL.asItem(),
                        PoItems.MAGGOTS_SEEDS.get(),
                        PoItems.ROUNDWORM.get(),
                        PoItems.POOP_BREAD.get(),
                        PoItems.POOP_DUMPLINGS.get(),
                        PoItems.POOP_MOONCAKE.get(),
                        PoItems.CHILI_POOP_MOONCAKE.get(),
                        PoItems.GOLDEN_POOP_MOONCAKE.get(),
                        PoItems.POOP_VEGETABLE_STICKS.get(),
                        PoItems.POOBURGER_MEAT.get(),
                        PoItems.POOBURGER.get(),
                        PoItems.POOPSICLE.get(),
                        PoItems.POOP_PASTA.get(),
                        PoItems.POODDING.get(),
                        PoBlocks.POOP_CAKE.asItem()
                );

        // 原版Tags
        tag(Tags.Items.FOODS)
                .add(
                        PoItems.POOP.get(),
                        PoItems.CHILI_POOP.get(),
                        PoItems.GOLDEN_POOP.get(),
                        PoItems.SAPLING_POOP_BALL.get(),
                        PoItems.BAKED_MAGGOTS.get(),
                        PoItems.MAGGOTS_SEEDS.get(),
                        PoItems.ROUNDWORM.get(),
                        PoItems.FASTING_PILL.get(),
                        PoItems.POOP_BREAD.get(),
                        PoItems.POOP_DUMPLINGS.get(),
                        PoItems.POOP_MOONCAKE.get(),
                        PoItems.CHILI_POOP_MOONCAKE.get(),
                        PoItems.GOLDEN_POOP_MOONCAKE.get(),
                        PoItems.POOP_SOUP.get(),
                        PoItems.POOP_VEGETABLE_STICKS.get(),
                        PoItems.POOBURGER_MEAT.get(),
                        PoItems.POOBURGER.get(),
                        PoItems.POOPSICLE.get(),
                        PoItems.POOP_PASTA.get(),
                        PoItems.POODDING.get(),
                        PoItems.DRAGON_BREATH_CHILI.get(),
                        PoItems.KING_OF_DRAGON_FRUIT.get(),
                        PoItems.URINE_BOTTLE.get(),
                        PoBlocks.POOP_CAKE.get().asItem()
                );

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

        tag(ItemTags.STONE_TOOL_MATERIALS)
                .add(Items.MOSSY_COBBLESTONE);

        tag(ItemTags.HOES).add(PoItems.SPALL_HOE.get());
        tag(ItemTags.AXES).add(PoItems.SPALL_AXE.get());
        tag(ItemTags.PICKAXES).add(PoItems.SPALL_PICKAXE.get());
        tag(ItemTags.SHOVELS).add(PoItems.SPALL_SHOVEL.get());
        tag(ItemTags.SWORDS).add(
                PoItems.SPALL_SWORD.get(),
                PoItems.MILOS_SWORD.get(),
                PoItems.TOILET_PLUG.get(),
                PoItems.TOILET_PLUG_WAND.get()
        );
        tag(ItemTags.FOOT_ARMOR).add(PoItems.OMEN_BOOTS.get());
        tag(ItemTags.LEG_ARMOR).add(PoItems.OMEN_LEGGINGS.get());
        tag(ItemTags.CHEST_ARMOR).add(PoItems.OMEN_CHESTPLATE.get());
        tag(ItemTags.HEAD_ARMOR).add(PoItems.OMEN_HELMET.get());
        tag(ItemTags.EQUIPPABLE_ENCHANTABLE).addTag(PoTags.Items.SHITS);
        tag(ItemTags.VANISHING_ENCHANTABLE).addTag(PoTags.Items.SHITS);

        tag(Tags.Items.MUSIC_DISCS)
                .add(
                        PoItems.LAWRENCE_MUSIC_DISC.get(),
                        PoItems.LIGHT_DANCE_MUSIC_DISC.get(),
                        PoItems.MOON_BOWL_MUSIC_DISC.get(),
                        PoItems.THEME_MUSIC_DISC.get()
                );

        tag(PoTags.Items.POOP_MOONCAKES)
                .add(
                        PoItems.POOP_MOONCAKE.get(),
                        PoItems.CHILI_POOP_MOONCAKE.get(),
                        PoItems.GOLDEN_POOP_MOONCAKE.get()
                );
        tag(PoTags.Items.EGG)
                .add(
                        Items.EGG,
                        Items.TURTLE_EGG,
                        Items.SNIFFER_EGG
                );
        tag(PoTags.Items.PASTA)
                .add(
                        PoItems.ROUNDWORM.get(),
                        PoItems.POOP_PASTA.get()
                );
        tag(PoTags.Items.SOUP)
                .add(PoItems.POOP_SOUP.get());
        tag(PoTags.Items.UPRIGHT_ON_BELT).replace(false)
                .add(
                        PoItems.POOP_BREAD.get(),
                        PoItems.POOP_DUMPLINGS.get(),
                        PoItems.POOP_MOONCAKE.get(),
                        PoItems.CHILI_POOP_MOONCAKE.get(),
                        PoItems.GOLDEN_POOP_MOONCAKE.get(),
                        PoItems.POOP_SOUP.get(),
                        PoItems.POOP_VEGETABLE_STICKS.get(),
                        PoItems.POOBURGER_MEAT.get(),
                        PoItems.POOBURGER.get(),
                        PoItems.POOPSICLE.get(),
                        PoItems.POOP_PASTA.get(),
                        PoItems.POODDING.get(),
                        PoItems.DRAGON_BREATH_CHILI.get(),
                        PoItems.KING_OF_DRAGON_FRUIT.get(),
                        PoItems.FASTING_PILL.get(),
                        PoItems.URINE_BOTTLE.get(),
                        PoBlocks.POOP_CAKE.asItem()
                );

        //方块物品标签
        tag(ItemTags.DIRT)
                .add(PoBlocks.POOP_BLOCK.asItem())
                .add(PoBlocks.CHILI_POOP_BLOCK.asItem())
                .add(PoBlocks.GOLDEN_POOP_BLOCK.asItem());

        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(PoItems.MAGGOTS_SEEDS.get());
        tag(ItemTags.SAPLINGS)
                .add(PoBlocks.POOP_SAPLING.asItem())
                .add(PoBlocks.GINKGO_SAPLING.asItem());
        tag(ItemTags.LEAVES)
                .add(PoBlocks.POOP_LEAVES.asItem())
                .add(PoBlocks.POOP_LEAVES_IRON.asItem())
                .add(PoBlocks.POOP_LEAVES_GOLD.asItem())
                .add(PoBlocks.GINKGO_LEAVES.asItem());

        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(Tags.Blocks.STRIPPED_LOGS, Tags.Items.STRIPPED_LOGS);
        copy(Tags.Blocks.STRIPPED_WOODS, Tags.Items.STRIPPED_WOODS);
        tag(ItemTags.LOGS_THAT_BURN).addTag(PoTags.Items.GINKGO_LOGS);
        tag(ItemTags.PLANKS).add(PoBlocks.GINKGO_PLANKS.asItem());
        tag(ItemTags.BOATS).add(PoItems.GINKGO_BOAT.get());
        tag(ItemTags.CHEST_BOATS).add(PoItems.GINKGO_CHEST_BOAT.get());
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        var stairs = tag(ItemTags.STAIRS);
        var slabs = tag(ItemTags.SLABS);
        var walls = tag(ItemTags.WALLS);
        PoBlocks.WALL_TAG_FAMILIES.forEach(family -> {
            stairs.add(family.stairs().asItem());
            slabs.add(family.slab().asItem());
            walls.add(family.wall().asItem());
        });
        stairs.add(PoBlocks.GINKGO_STAIRS.asItem(), PoBlocks.PRIMO_STAIRS.asItem());
        slabs.add(PoBlocks.GINKGO_SLAB.asItem(), PoBlocks.PRIMO_SLAB.asItem());

        tag(ItemTags.BUTTONS)
                .add(
                        PoBlocks.POOP_BUTTON.asItem(),
                        PoBlocks.GINKGO_BUTTON.asItem(),
                        PoBlocks.PRIMO_BUTTON.asItem()
                );
        tag(ItemTags.FENCES)
                .add(
                        PoBlocks.POOP_FENCE.asItem(),
                        PoBlocks.GINKGO_FENCE.asItem(),
                        PoBlocks.PRIMO_FENCE.asItem()
                );
        tag(ItemTags.FENCE_GATES)
                .add(
                        PoBlocks.POOP_FENCE_GATE.asItem(),
                        PoBlocks.GINKGO_FENCE_GATE.asItem(),
                        PoBlocks.PRIMO_FENCE_GATE.asItem()
                );
        tag(ItemTags.DOORS)
                .add(
                        PoBlocks.POOP_DOOR.asItem(),
                        PoBlocks.GINKGO_DOOR.asItem(),
                        PoBlocks.PRIMO_DOOR.asItem()
                );
        tag(ItemTags.TRAPDOORS)
                .add(
                        PoBlocks.POOP_TRAPDOOR.asItem(),
                        PoBlocks.GINKGO_TRAPDOOR.asItem(),
                        PoBlocks.PRIMO_TRAPDOOR.asItem()
                );
    }

    private static TagAppender<Item, Item> tag(TagKey<Item> tag) {
        return provider.tag(tag);
    }

    private static void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
        provider.copy(blockTag, itemTag);
    }
}
