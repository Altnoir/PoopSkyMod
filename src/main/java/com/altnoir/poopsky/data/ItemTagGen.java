package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.fabric.FabricatedTags;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

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
                .add(PoItems.POOP.get())
                .add(PoItems.CHILI_POOP.get())
                .add(PoItems.GOLDEN_POOP.get())
                .add(PoBlocks.SHIT.asItem())
                .add(PoBlocks.CHILI_SHIT.asItem())
                .add(PoBlocks.GOLDEN_SHIT.asItem());
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

        tag(PoTags.Items.CAN_COMPOSTABLE)
                .add(PoItems.POOP.get())
                .add(PoItems.POOP_BALL.get())
                .add(PoBlocks.POOP_SAPLING.asItem())
                .add(PoBlocks.POOP_LEAVES_IRON.asItem())
                .add(PoBlocks.POOP_LEAVES_GOLD.asItem())
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
        tag(FabricatedTags.Items.FOODS)
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
                        PoItems.POOP_PASTA.get(),
                        PoItems.POODDING.get(),
                        PoItems.DRAGON_BREATH_CHILI.get(),
                        PoItems.KING_OF_DRAGON_FRUIT.get(),
                        PoItems.URINE_BOTTLE.get(),
                        PoBlocks.POOP_CAKE.get().asItem()
                );

        tag(FabricatedTags.Items.FOODS_FOOD_POISONING)
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
        tag(FabricatedTags.Items.FOODS_RAW_MEAT)
                .add(PoItems.MAGGOTS_SEEDS.get())
                .add(PoItems.ROUNDWORM.get());
        tag(FabricatedTags.Items.FOODS_COOKED_MEAT)
                .add(PoItems.BAKED_MAGGOTS.get());

        tag(FabricatedTags.Items.GUNPOWDERS)
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
        tag(ItemTags.EQUIPPABLE_ENCHANTABLE).add(
                PoBlocks.SHIT.asItem(),
                PoBlocks.CHILI_SHIT.asItem(),
                PoBlocks.GOLDEN_SHIT.asItem()
        );
        tag(ItemTags.VANISHING_ENCHANTABLE).add(
                PoBlocks.SHIT.asItem(),
                PoBlocks.CHILI_SHIT.asItem(),
                PoBlocks.GOLDEN_SHIT.asItem()
        );

        tag(FabricatedTags.Items.MUSIC_DISCS)
                .add(PoItems.LAWRENCE_MUSIC_DISC.get())
                .add(PoItems.LIGHT_DANCE_MUSIC_DISC.get())
                .add(PoItems.MOON_BOWL_MUSIC_DISC.get());

        tag(PoTags.Items.PASTA)
                .add(PoItems.ROUNDWORM.get())
                .add(PoItems.POOP_PASTA.get());
        tag(PoTags.Items.SOUP)
                .add(PoItems.POOP_SOUP.get());
        tag(PoTags.Items.UPRIGHT_ON_BELT)
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
                .add(PoItems.FASTING_PILL.get())
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
                .add(PoBlocks.POOP_SAPLING.asItem())
                .add(PoBlocks.GINKGO_SAPLING.asItem());
        tag(ItemTags.LEAVES)
                .add(PoBlocks.POOP_LEAVES.asItem())
                .add(PoBlocks.POOP_LEAVES_IRON.asItem())
                .add(PoBlocks.POOP_LEAVES_GOLD.asItem())
                .add(PoBlocks.GINKGO_LEAVES.asItem());

        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(FabricatedTags.Blocks.STRIPPED_LOGS, FabricatedTags.Items.STRIPPED_LOGS);
        copy(FabricatedTags.Blocks.STRIPPED_WOODS, FabricatedTags.Items.STRIPPED_WOODS);
        tag(ItemTags.LOGS_THAT_BURN).addTag(PoTags.Items.GINKGO_LOGS);
        tag(ItemTags.PLANKS).add(PoBlocks.GINKGO_PLANKS.asItem());
        tag(ItemTags.BOATS).add(PoItems.GINKGO_BOAT.get());
        tag(ItemTags.CHEST_BOATS).add(PoItems.GINKGO_CHEST_BOAT.get());
        tag(ItemTags.WOODEN_STAIRS).add(PoBlocks.GINKGO_STAIRS.asItem());
        tag(ItemTags.WOODEN_SLABS).add(PoBlocks.GINKGO_SLAB.asItem());
        tag(ItemTags.WOODEN_BUTTONS).add(PoBlocks.GINKGO_BUTTON.asItem());
        tag(ItemTags.WOODEN_PRESSURE_PLATES).add(PoBlocks.GINKGO_PRESSURE_PLATE.asItem());
        tag(ItemTags.WOODEN_FENCES).add(PoBlocks.GINKGO_FENCE.asItem());
        tag(ItemTags.WOODEN_DOORS).add(PoBlocks.GINKGO_DOOR.asItem());
        tag(ItemTags.WOODEN_TRAPDOORS).add(PoBlocks.GINKGO_TRAPDOOR.asItem());
        var stairs = tag(ItemTags.STAIRS);
        var slabs = tag(ItemTags.SLABS);
        var walls = tag(ItemTags.WALLS);
        PoBlocks.WALL_TAG_FAMILIES.forEach(family -> {
            stairs.add(family.stairs().asItem());
            slabs.add(family.slab().asItem());
            walls.add(family.wall().asItem());
        });
        stairs.add(PoBlocks.GINKGO_STAIRS.asItem());
        slabs.add(PoBlocks.GINKGO_SLAB.asItem());

        tag(ItemTags.BUTTONS)
                .add(PoBlocks.POOP_BUTTON.asItem())
                .add(PoBlocks.GINKGO_BUTTON.asItem());
        tag(ItemTags.FENCES)
                .add(PoBlocks.POOP_FENCE.asItem())
                .add(PoBlocks.GINKGO_FENCE.asItem());
        tag(ItemTags.FENCE_GATES)
                .add(PoBlocks.POOP_FENCE_GATE.asItem())
                .add(PoBlocks.GINKGO_FENCE_GATE.asItem());
        tag(ItemTags.DOORS)
                .add(PoBlocks.POOP_DOOR.asItem())
                .add(PoBlocks.GINKGO_DOOR.asItem());
        tag(ItemTags.TRAPDOORS)
                .add(PoBlocks.POOP_TRAPDOOR.asItem())
                .add(PoBlocks.GINKGO_TRAPDOOR.asItem());
    }

    private static IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag(TagKey<Item> tag) {
        return provider.addTag(tag);
    }

    private static void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
        provider.copy(blockTag, itemTag);
    }
}
