package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.ToiletType;
import com.altnoir.poopsky.compat.PSMods;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PItems;
import com.altnoir.poopsky.recipe.*;
import com.simibubi.create.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PSRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public PSRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        List<ItemLike> POOP_LIST = List.of(PBlocks.POOP_BLOCK);
        List<ItemLike> POOP_BRICK_LIST = List.of(PBlocks.POOP_BRICKS);
        List<ItemLike> SMOOTH_POOP_LIST = List.of(PBlocks.DRIED_POOP_BLOCK);
        List<ItemLike> TILE_BLOCK_LIST = List.of(PBlocks.POOLIME_BLOCK);
        List<ItemLike> MAGGOTS_LIST = List.of(PItems.MAGGOTS_SEEDS);
        List<ItemLike> ROUNDWORM_LIST = List.of(PItems.ROUNDWORM);

        shapeless1x1Recipe(recipeOutput, Blocks.CRIMSON_NYLIUM, Blocks.CRIMSON_FUNGUS, Blocks.NETHERRACK);
        shapeless1x1Recipe(recipeOutput, Blocks.WARPED_NYLIUM, Blocks.WARPED_FUNGUS, Blocks.NETHERRACK);
        shapeless1x1Recipe(recipeOutput, Blocks.SLIME_BLOCK, Items.LIME_DYE, PBlocks.POOLIME_BLOCK);

        oreSmelting(recipeOutput, POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PBlocks.DRIED_POOP_BLOCK, 0.1F, 200, "dried_poop_block");
        oreBlasting(recipeOutput, POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PBlocks.DRIED_POOP_BLOCK, 0.1F, 100, "dried_poop_block");

        oreSmelting(recipeOutput, POOP_BRICK_LIST, RecipeCategory.BUILDING_BLOCKS, PBlocks.CRACKED_POOP_BRICKS, 0.1F, 200, "cracked_poop_bricks");
        oreBlasting(recipeOutput, POOP_BRICK_LIST, RecipeCategory.BUILDING_BLOCKS, PBlocks.CRACKED_POOP_BRICKS, 0.1F, 100, "cracked_poop_bricks");

        oreSmelting(recipeOutput, SMOOTH_POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PBlocks.SMOOTH_POOP_BLOCK, 0.1F, 200, "smooth_poop_block");
        oreBlasting(recipeOutput, SMOOTH_POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PBlocks.SMOOTH_POOP_BLOCK, 0.1F, 100, "smooth_poop_block");

        oreSmelting(recipeOutput, TILE_BLOCK_LIST, RecipeCategory.BUILDING_BLOCKS, PBlocks.TILE_BLOCK, 0.1F, 200, "tile_block");
        oreBlasting(recipeOutput, TILE_BLOCK_LIST, RecipeCategory.BUILDING_BLOCKS, PBlocks.TILE_BLOCK, 0.1F, 100, "tile_block");

        oreCooking(recipeOutput, List.of(PItems.POOP.get()), RecipeCategory.MISC, Items.COCOA_BEANS, 0.35F, 600, "cocoa_beans");
        oreSmelting(recipeOutput, ROUNDWORM_LIST, RecipeCategory.MISC, Items.STRING, 0.35F, 200, "roundworm");
        oreCooking(recipeOutput, ROUNDWORM_LIST, RecipeCategory.MISC, Items.STRING, 0.35F, 200, "roundworm");
        // 食物
        oreSmelting(recipeOutput, MAGGOTS_LIST, RecipeCategory.BUILDING_BLOCKS, PItems.BAKED_MAGGOTS, 0.35F, 200, "maggots_seeds");
        oreCooking(recipeOutput, RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, MAGGOTS_LIST, RecipeCategory.BUILDING_BLOCKS, PItems.BAKED_MAGGOTS, 0.35F, 100, "maggots_seeds", "_from_smoking");
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PItems.POOP_BREAD)
                .pattern("PMP")
                .define('P', PItems.POOP)
                .define('M', PItems.MAGGOTS_SEEDS)
                .unlockedBy(getItemName(PItems.MAGGOTS_SEEDS), has(PItems.MAGGOTS_SEEDS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PItems.POOP_MOONCAKE, 2)
                .pattern("WPW")
                .define('W', Items.WHEAT)
                .define('P', PItems.POOP)
                .unlockedBy(getItemName(Items.WHEAT), has(Items.WHEAT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PItems.CHILI_POOP_MOONCAKE, 2)
                .pattern("WPW")
                .define('W', Items.WHEAT)
                .define('P', PItems.CHILI_POOP)
                .unlockedBy(getItemName(Items.WHEAT), has(Items.WHEAT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PItems.GOLDEN_POOP_MOONCAKE, 2)
                .pattern("WPW")
                .define('W', Items.WHEAT)
                .define('P', PItems.GOLDEN_POOP)
                .unlockedBy(getItemName(Items.WHEAT), has(Items.WHEAT))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PItems.POOP_DUMPLINGS)
                .requires(PItems.POOP_BALL.get())
                .requires(ItemTags.LEAVES)
                .unlockedBy(getItemName(PItems.POOP_BALL.get()), has(PItems.POOP_BALL.get()))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PItems.POOP_SOUP)
                .requires(Items.BOWL)
                .requires(PItems.POOP)
                .requires(PItems.MAGGOTS_SEEDS)
                .requires(PItems.URINE_BOTTLE)
                .unlockedBy(getItemName(PItems.MAGGOTS_SEEDS), has(PItems.MAGGOTS_SEEDS))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PItems.POOBURGER_MEAT.get(), 3)
                .requires(PItems.POOP, 3)
                .requires(Items.EGG)
                .unlockedBy(getItemName(PItems.POOP), has(PItems.POOP))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PItems.POOBURGER.get())
                .pattern("P")
                .pattern("M")
                .pattern("P")
                .define('P', Items.BREAD)
                .define('M', PItems.POOBURGER_MEAT)
                .unlockedBy(getItemName(PItems.POOBURGER_MEAT), has(PItems.POOBURGER_MEAT))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PItems.POODDING.get(), 2)
                .requires(PItems.POOP_BALL)
                .requires(Items.EGG).requires(Items.SUGAR)
                .unlockedBy(getItemName(PItems.POOP_BALL), has(PItems.POOP_BALL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PBlocks.POOP_CAKE.get())
                .pattern("MMM")
                .pattern("SES")
                .pattern("PPP")
                .define('M', PItems.MAGGOTS_SEEDS)
                .define('S', Items.SUGAR).define('E', Items.EGG)
                .define('P', PItems.POOP)
                .unlockedBy(getItemName(PItems.MAGGOTS_SEEDS), has(PItems.MAGGOTS_SEEDS))
                .save(recipeOutput);

        // 杂项
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PItems.TOILET_PLUG_WAND)
                .requires(PItems.TOILET_PLUG.get())
                .requires(PItems.POOP.get())
                .requires(Items.ENDER_EYE)
                .unlockedBy(getItemName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PItems.TIME_BELL)
                .requires(Items.BELL)
                .requires(PItems.POOP.get())
                .requires(Items.DRAGON_EGG)
                .unlockedBy(getItemName(Items.DRAGON_EGG), has(Items.DRAGON_EGG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.COBWEB)
                .pattern("S S")
                .pattern(" P ")
                .pattern("S S")
                .define('P', PItems.POOP_BALL)
                .define('S', PItems.MAGGOTS_SEEDS)
                .unlockedBy(getItemName(PItems.MAGGOTS_SEEDS), has(PItems.MAGGOTS_SEEDS))
                .save(recipeOutput, getConversionRecipeName(PItems.MAGGOTS_SEEDS));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PItems.WITHER_POOP_BALL.get(), 8)
                .pattern("PPP")
                .pattern("PSP")
                .pattern("PPP")
                .define('P', PItems.POOP_BALL)
                .define('S', Items.WITHER_ROSE)
                .unlockedBy(getItemName(Items.WITHER_ROSE), has(Items.WITHER_ROSE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PItems.GOLDEN_POOP.get())
                .pattern("PPP")
                .pattern("PSP")
                .pattern("PPP")
                .define('P', Items.GOLD_NUGGET)
                .define('S', PItems.POOP)
                .unlockedBy(getItemName(Items.GOLD_NUGGET), has(Items.GOLD_NUGGET))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PItems.SEEDBED_CURSE.get())
                .requires(Items.ROTTEN_FLESH, 4)
                .requires(PItems.POOP_BALL)
                .requires(PItems.POOP, 4)
                .unlockedBy(getItemName(Items.ROTTEN_FLESH), has(Items.ROTTEN_FLESH))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PItems.OMINOUS_FILTHY_INGOT.get())
                .requires(PItems.SEEDBED_CURSE, 4)
                .requires(Items.IRON_INGOT, 4)
                .unlockedBy(getItemName(PItems.SEEDBED_CURSE), has(PItems.SEEDBED_CURSE))
                .save(recipeOutput);
        copySmithingTemplate(recipeOutput, PItems.OMEN_UPGRADE_SMITHING_TEMPLATE, PBlocks.POOP_BLOCK, PItems.SEEDBED_CURSE);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PItems.MILOS_SWORD)
                .pattern("BOB")
                .pattern(" O ")
                .pattern(" P ")
                .define('B', Items.BONE)
                .define('O', PItems.OMINOUS_FILTHY_INGOT)
                .define('P', PItems.TOILET_PLUG)
                .unlockedBy(getItemName(PItems.OMINOUS_FILTHY_INGOT), has(PItems.OMINOUS_FILTHY_INGOT))
                .save(recipeOutput);
        // 盔甲
        omenSmithing(recipeOutput, Items.GOLDEN_CHESTPLATE, RecipeCategory.COMBAT, PItems.OMEN_CHESTPLATE.get());
        omenSmithing(recipeOutput, Items.GOLDEN_LEGGINGS, RecipeCategory.COMBAT, PItems.OMEN_LEGGINGS.get());
        omenSmithing(recipeOutput, Items.GOLDEN_HELMET, RecipeCategory.COMBAT, PItems.OMEN_HELMET.get());
        omenSmithing(recipeOutput, Items.GOLDEN_BOOTS, RecipeCategory.COMBAT, PItems.OMEN_BOOTS.get());

        // 建筑
        offer2x2CompactingRecipe(recipeOutput, PBlocks.POOP_BLOCK, PItems.POOP);
        stairsRecipe(recipeOutput, PBlocks.POOP_STAIRS, PBlocks.POOP_BLOCK);
        slabRecipe(recipeOutput, PBlocks.POOP_SLAB, PBlocks.POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PBlocks.POOP_VERTICAL_SLAB, PBlocks.POOP_BLOCK);
        wallRecipe(recipeOutput, PBlocks.POOP_WALL, PBlocks.POOP_BLOCK);

        offer2x2CompactingRecipe(recipeOutput, PBlocks.CHILI_POOP_BLOCK, PItems.CHILI_POOP);
        stairsRecipe(recipeOutput, PBlocks.CHILI_POOP_STAIRS, PBlocks.CHILI_POOP_BLOCK);
        slabRecipe(recipeOutput, PBlocks.CHILI_POOP_SLAB, PBlocks.CHILI_POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PBlocks.CHILI_POOP_VERTICAL_SLAB, PBlocks.CHILI_POOP_BLOCK);
        wallRecipe(recipeOutput, PBlocks.CHILI_POOP_WALL, PBlocks.CHILI_POOP_BLOCK);

        offer2x2CompactingRecipe(recipeOutput, PBlocks.GOLDEN_POOP_BLOCK, PItems.GOLDEN_POOP);
        stairsRecipe(recipeOutput, PBlocks.GOLDEN_POOP_STAIRS, PBlocks.GOLDEN_POOP_BLOCK);
        slabRecipe(recipeOutput, PBlocks.GOLDEN_POOP_SLAB, PBlocks.GOLDEN_POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PBlocks.GOLDEN_POOP_VERTICAL_SLAB, PBlocks.GOLDEN_POOP_BLOCK);
        wallRecipe(recipeOutput, PBlocks.GOLDEN_POOP_WALL, PBlocks.GOLDEN_POOP_BLOCK);

        stairsRecipe(recipeOutput, PBlocks.DRIED_POOP_BLOCK_STAIRS, PBlocks.DRIED_POOP_BLOCK);
        slabRecipe(recipeOutput, PBlocks.DRIED_POOP_BLOCK_SLAB, PBlocks.DRIED_POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB, PBlocks.DRIED_POOP_BLOCK);
        wallRecipe(recipeOutput, PBlocks.DRIED_POOP_BLOCK_WALL, PBlocks.DRIED_POOP_BLOCK);

        stairsRecipe(recipeOutput, PBlocks.SMOOTH_POOP_BLOCK_STAIRS, PBlocks.SMOOTH_POOP_BLOCK);
        slabRecipe(recipeOutput, PBlocks.SMOOTH_POOP_BLOCK_SLAB, PBlocks.SMOOTH_POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB, PBlocks.SMOOTH_POOP_BLOCK);
        wallRecipe(recipeOutput, PBlocks.SMOOTH_POOP_BLOCK_WALL, PBlocks.SMOOTH_POOP_BLOCK);

        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CUT_POOP_BLOCK, PBlocks.DRIED_POOP_BLOCK, 4);
        stairsRecipe(recipeOutput, PBlocks.CUT_POOP_BLOCK_STAIRS, PBlocks.CUT_POOP_BLOCK);
        slabRecipe(recipeOutput, PBlocks.CUT_POOP_BLOCK_SLAB, PBlocks.CUT_POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PBlocks.CUT_POOP_BLOCK);
        wallRecipe(recipeOutput, PBlocks.CUT_POOP_BLOCK_WALL, PBlocks.CUT_POOP_BLOCK);

        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BRICKS, PBlocks.POOP_BLOCK, 4);
        stairsRecipe(recipeOutput, PBlocks.POOP_BRICK_STAIRS, PBlocks.POOP_BRICKS);
        slabRecipe(recipeOutput, PBlocks.POOP_BRICK_SLAB, PBlocks.POOP_BRICKS);
        verticalSlabRecipe(recipeOutput, PBlocks.POOP_BRICK_VERTICAL_SLAB, PBlocks.POOP_BRICKS);
        wallRecipe(recipeOutput, PBlocks.POOP_BRICK_WALL, PBlocks.POOP_BRICKS);

        create1x2ShapelessFrom(recipeOutput, PBlocks.MOSSY_POOP_BRICKS, PBlocks.POOP_BRICKS, Blocks.MOSS_BLOCK);
        create1x2ShapelessFrom(recipeOutput, PBlocks.MOSSY_POOP_BRICKS, PBlocks.POOP_BRICKS, Blocks.VINE);
        stairsRecipe(recipeOutput, PBlocks.MOSSY_POOP_BRICK_STAIRS, PBlocks.MOSSY_POOP_BRICKS);
        slabRecipe(recipeOutput, PBlocks.MOSSY_POOP_BRICK_SLAB, PBlocks.MOSSY_POOP_BRICKS);
        verticalSlabRecipe(recipeOutput, PBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB, PBlocks.MOSSY_POOP_BRICKS);
        wallRecipe(recipeOutput, PBlocks.MOSSY_POOP_BRICK_WALL, PBlocks.MOSSY_POOP_BRICKS);

        stairsRecipe(recipeOutput, PBlocks.TILE_BLOCK_STAIRS, PBlocks.TILE_BLOCK);
        slabRecipe(recipeOutput, PBlocks.TILE_BLOCK_SLAB, PBlocks.TILE_BLOCK);
        verticalSlabRecipe(recipeOutput, PBlocks.TILE_BLOCK_VERTICAL_SLAB, PBlocks.TILE_BLOCK);
        wallRecipe(recipeOutput, PBlocks.TILE_BLOCK_WALL, PBlocks.TILE_BLOCK);

        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PItems.POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PBlocks.RAW_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PItems.SAPLING_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PBlocks.RAW_SAPLING_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PItems.SEA_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PBlocks.RAW_SEA_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PItems.WITHER_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PBlocks.RAW_WITHER_POOP_BLOCK);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BLOCK)
                .pattern("P")
                .pattern("P")
                .define('P', PBlocks.POOP_SLAB)
                .unlockedBy(getItemName(PBlocks.POOP_BLOCK), has(PBlocks.POOP_BLOCK))
                .save(recipeOutput, getConversionRecipeName(PBlocks.POOP_BLOCK) + "_from_slab");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BLOCK)
                .pattern("PP")
                .define('P', PBlocks.POOP_VERTICAL_SLAB)
                .unlockedBy(getItemName(PBlocks.POOP_BLOCK), has(PBlocks.POOP_BLOCK))
                .save(recipeOutput, getConversionRecipeName(PBlocks.POOP_BLOCK) + "_from_vertical_slab");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BUTTON)
                .requires(PItems.POOP.get())
                .unlockedBy(getItemName(PItems.POOP), has(PItems.POOP))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_PRESSURE_PLATE)
                .pattern("PP")
                .define('P', PItems.POOP)
                .unlockedBy(getItemName(PItems.POOP), has(PItems.POOP))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_FENCE, 3)
                .pattern("BPB")
                .pattern("BPB")
                .define('B', PBlocks.POOP_BLOCK)
                .define('P', PItems.POOP)
                .unlockedBy(getItemName(PBlocks.POOP_BLOCK), has(PBlocks.POOP_BLOCK))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_FENCE_GATE)
                .pattern("PBP")
                .pattern("PBP")
                .define('B', PBlocks.POOP_BLOCK)
                .define('P', PItems.POOP)
                .unlockedBy(getItemName(PBlocks.POOP_BLOCK), has(PBlocks.POOP_BLOCK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_DOOR, 3)
                .pattern("PP")
                .pattern("PP")
                .pattern("PP")
                .define('P', PBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PBlocks.POOP_BLOCK), has(PBlocks.POOP_BLOCK))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_TRAPDOOR, 2)
                .pattern("PP")
                .pattern("PP")
                .define('P', PBlocks.POOP_SLAB)
                .unlockedBy(getItemName(PBlocks.POOP_BLOCK), has(PBlocks.POOP_BLOCK))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BLOCK, 4)
                .requires(PBlocks.POOP_EMPTY_LOG)
                .unlockedBy(getItemName(PBlocks.POOP_LOG), has(PBlocks.POOP_LOG))
                .save(recipeOutput, getConversionRecipeName(PBlocks.POOP_BLOCK, PBlocks.POOP_EMPTY_LOG));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BLOCK, 4)
                .requires(PBlocks.STRIPPED_POOP_EMPTY_LOG)
                .unlockedBy(getItemName(PBlocks.POOP_LOG), has(PBlocks.POOP_LOG))
                .save(recipeOutput, getConversionRecipeName(PBlocks.POOP_BLOCK, PBlocks.STRIPPED_POOP_EMPTY_LOG));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_PIECE, 3)
                .pattern("PP")
                .define('P', PBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PBlocks.POOP_BLOCK), has(PBlocks.POOP_BLOCK))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PItems.LAWRENCE_MUSIC_DISC)
                .requires(Tags.Items.MUSIC_DISCS)
                .requires(PItems.POOP)
                .unlockedBy(getItemName(PItems.POOP), has(PItems.POOP))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PBlocks.COMPOOPER)
                .pattern("S S")
                .pattern("S S")
                .pattern("SSS")
                .define('S', Blocks.MOSSY_COBBLESTONE_SLAB)
                .unlockedBy(getItemName(Blocks.MOSSY_COBBLESTONE_SLAB), has(Blocks.MOSSY_COBBLESTONE_SLAB))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PBlocks.PLACER)
                .pattern("SSS")
                .pattern("SPS")
                .pattern("SAS")
                .define('S', Blocks.MOSSY_COBBLESTONE)
                .define('A', Items.REDSTONE)
                .define('P', PItems.TOILET_PLUG)
                .unlockedBy(getItemName(PItems.TOILET_PLUG), has(PItems.TOILET_PLUG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PBlocks.SIEVE)
                .pattern("SAS")
                .pattern("S S")
                .pattern("S S")
                .define('S', Blocks.MOSSY_COBBLESTONE_WALL)
                .define('A', Items.STRING)
                .unlockedBy(getItemName(Blocks.MOSSY_COBBLESTONE_WALL), has(Blocks.MOSSY_COBBLESTONE_WALL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PBlocks.POOP_TNT)
                .pattern("SAS")
                .pattern("ASA")
                .pattern("SAS")
                .define('S', Tags.Items.GUNPOWDERS)
                .define('A', PBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PItems.KING_OF_DRAGON_FRUIT), has(PItems.KING_OF_DRAGON_FRUIT))
                .save(recipeOutput);

        offer2x2CompactingRecipe(recipeOutput, PBlocks.POOLIME_BLOCK.get(), PItems.POOP_BALL.get());
        offerCompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOLIME_POOP_BLOCK.get(), PBlocks.POOP_BLOCK.get());

        // 原版物品配方
        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, Blocks.CRAFTING_TABLE, PItems.SPALL, 1);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER)
                .requires(PItems.KING_OF_DRAGON_FRUIT)
                .unlockedBy(getItemName(PItems.KING_OF_DRAGON_FRUIT), has(PItems.KING_OF_DRAGON_FRUIT))
                .save(recipeOutput, getConversionRecipeName(Items.GUNPOWDER) + "_from_" + getItemName(PItems.KING_OF_DRAGON_FRUIT));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.COARSE_DIRT, 4)
                .pattern("PG")
                .pattern("GP")
                .define('G', Blocks.GRAVEL)
                .define('P', PBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PBlocks.POOP_BLOCK), has(PBlocks.POOP_BLOCK))
                .save(recipeOutput, getConversionRecipeName(Blocks.COARSE_DIRT) + "_from_poop_block");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.POINTED_DRIPSTONE)
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .define('S', PItems.SPALL)
                .unlockedBy(getItemName(PItems.SPALL), has(PItems.SPALL))
                .save(recipeOutput, getConversionRecipeName(Blocks.POINTED_DRIPSTONE) + "_from_spall");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.FLINT)
                .pattern("S")
                .pattern("S")
                .define('S', PItems.SPALL)
                .unlockedBy(getItemName(PItems.SPALL), has(PItems.SPALL))
                .save(recipeOutput, getConversionRecipeName(Items.FLINT) + "_from_spall");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.GRAVEL)
                .pattern("FF")
                .pattern("FF")
                .define('F', Items.FLINT)
                .unlockedBy(getItemName(Items.FLINT), has(Items.FLINT))
                .save(recipeOutput, getConversionRecipeName(Blocks.GRAVEL) + "_from_flint_x4");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH, 4)
                .pattern("P")
                .pattern("S")
                .define('P', PItems.POOP_BALL)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PItems.POOP_BALL), has(PItems.POOP_BALL))
                .save(recipeOutput, getConversionRecipeName(Items.TORCH) + "_from_poop_ball");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.TUBE_CORAL_BLOCK)
                .requires(PBlocks.POOP_BLOCK)
                .requires(Blocks.TUBE_CORAL).requires(Blocks.TUBE_CORAL_FAN)
                .unlockedBy(getItemName(Blocks.TUBE_CORAL), has(Blocks.TUBE_CORAL))
                .save(recipeOutput, getConversionRecipeName(Blocks.TUBE_CORAL_BLOCK));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.BRAIN_CORAL_BLOCK)
                .requires(PBlocks.POOP_BLOCK)
                .requires(Blocks.BRAIN_CORAL).requires(Blocks.BRAIN_CORAL_FAN)
                .unlockedBy(getItemName(Blocks.BRAIN_CORAL), has(Blocks.BRAIN_CORAL))
                .save(recipeOutput, getConversionRecipeName(Blocks.BRAIN_CORAL_BLOCK));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.BUBBLE_CORAL_BLOCK)
                .requires(PBlocks.POOP_BLOCK)
                .requires(Blocks.BUBBLE_CORAL).requires(Blocks.BUBBLE_CORAL_FAN)
                .unlockedBy(getItemName(Blocks.BUBBLE_CORAL), has(Blocks.BUBBLE_CORAL))
                .save(recipeOutput, getConversionRecipeName(Blocks.BUBBLE_CORAL_BLOCK));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.FIRE_CORAL_BLOCK)
                .requires(PBlocks.POOP_BLOCK)
                .requires(Blocks.FIRE_CORAL).requires(Blocks.FIRE_CORAL_FAN)
                .unlockedBy(getItemName(Blocks.FIRE_CORAL), has(Blocks.FIRE_CORAL))
                .save(recipeOutput, getConversionRecipeName(Blocks.FIRE_CORAL_BLOCK));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.HORN_CORAL_BLOCK)
                .requires(PBlocks.POOP_BLOCK)
                .requires(Blocks.HORN_CORAL).requires(Blocks.HORN_CORAL_FAN)
                .unlockedBy(getItemName(Blocks.HORN_CORAL), has(Blocks.HORN_CORAL))
                .save(recipeOutput, getConversionRecipeName(Blocks.HORN_CORAL_BLOCK));

        offerCompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE, PItems.SPALL);
        create1x2ShapelessFrom(recipeOutput, Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.MOSS_BLOCK);

        create1x2ShapelessFrom(recipeOutput, Blocks.DIORITE, Blocks.COBBLESTONE, Blocks.CLAY, 2);
        create1x2ShapelessFrom(recipeOutput, Blocks.GRANITE, Blocks.COBBLESTONE, Blocks.DRIPSTONE_BLOCK, 2);
        create1x2ShapelessFrom(recipeOutput, Blocks.DIRT, Blocks.MUD, PItems.POOP.get());
        //create1x2ShapelessFrom(recipeOutput, Blocks.TUFF, Blocks.ANDESITE, PSItems.SPALL);
        //create1x2ShapelessFrom(recipeOutput, Blocks.CALCITE, Blocks.DIORITE, PSItems.SPALL);

        // 切石配方
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.STOOL, PBlocks.DRIED_POOP_BLOCK, 2);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_PIECE, PBlocks.POOP_BLOCK, 8);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_STAIRS, PBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_SLAB, PBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_VERTICAL_SLAB, PBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_FENCE, PBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_FENCE_GATE, PBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_WALL, PBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_DOOR, PBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_TRAPDOOR, PBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_PRESSURE_PLATE, PBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BUTTON, PBlocks.POOP_BLOCK, 4);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CHILI_POOP_STAIRS, PBlocks.CHILI_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CHILI_POOP_SLAB, PBlocks.CHILI_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CHILI_POOP_VERTICAL_SLAB, PBlocks.CHILI_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CHILI_POOP_WALL, PBlocks.CHILI_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.GOLDEN_POOP_STAIRS, PBlocks.GOLDEN_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.GOLDEN_POOP_SLAB, PBlocks.GOLDEN_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.GOLDEN_POOP_VERTICAL_SLAB, PBlocks.GOLDEN_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.GOLDEN_POOP_WALL, PBlocks.GOLDEN_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BRICKS, PBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BRICK_STAIRS, PBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BRICK_SLAB, PBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BRICK_VERTICAL_SLAB, PBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BRICK_WALL, PBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BRICK_STAIRS, PBlocks.POOP_BRICKS);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BRICK_SLAB, PBlocks.POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BRICK_VERTICAL_SLAB, PBlocks.POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BRICK_WALL, PBlocks.POOP_BRICKS);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.MOSSY_POOP_BRICK_STAIRS, PBlocks.MOSSY_POOP_BRICKS);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.MOSSY_POOP_BRICK_SLAB, PBlocks.MOSSY_POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB, PBlocks.MOSSY_POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.MOSSY_POOP_BRICK_WALL, PBlocks.MOSSY_POOP_BRICKS);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.DRIED_POOP_BLOCK_STAIRS, PBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.DRIED_POOP_BLOCK_SLAB, PBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB, PBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.DRIED_POOP_BLOCK_WALL, PBlocks.DRIED_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CUT_POOP_BLOCK, PBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CUT_POOP_BLOCK_STAIRS, PBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CUT_POOP_BLOCK_SLAB, PBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CUT_POOP_BLOCK_WALL, PBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CUT_POOP_BLOCK_STAIRS, PBlocks.CUT_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CUT_POOP_BLOCK_SLAB, PBlocks.CUT_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PBlocks.CUT_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.CUT_POOP_BLOCK_WALL, PBlocks.CUT_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.SMOOTH_POOP_BLOCK_STAIRS, PBlocks.SMOOTH_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.SMOOTH_POOP_BLOCK_SLAB, PBlocks.SMOOTH_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB, PBlocks.SMOOTH_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.SMOOTH_POOP_BLOCK_WALL, PBlocks.SMOOTH_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.STRIPPED_POOP_LOG, PBlocks.POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_EMPTY_LOG, PBlocks.POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.STRIPPED_POOP_EMPTY_LOG, PBlocks.STRIPPED_POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.STRIPPED_POOP_EMPTY_LOG, PBlocks.POOP_EMPTY_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BLOCK, PBlocks.POOP_EMPTY_LOG, 4);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.POOP_BLOCK, PBlocks.STRIPPED_POOP_EMPTY_LOG, 4);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.TILE_BLOCK_STAIRS, PBlocks.TILE_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.TILE_BLOCK_SLAB, PBlocks.TILE_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.TILE_BLOCK_VERTICAL_SLAB, PBlocks.TILE_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PBlocks.TILE_BLOCK_WALL, PBlocks.TILE_BLOCK);

        // 厕所配方
        for (var entry : ToiletType.getByCategory(ToiletType.Category.WOOD).entrySet()) {
            toiletRecipes(recipeOutput, PBlocks.WOODEN_TOILET, entry.getValue().sourceBlock(), entry.getValue());
        }
        for (var entry : ToiletType.getByCategory(ToiletType.Category.HARD).entrySet()) {
            if (entry.getValue().sourceBlock() != null) {
                toiletRecipes(recipeOutput, PBlocks.HARD_TOILET, entry.getValue().sourceBlock(), entry.getValue());
            }
        }
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PBlocks.HARD_TOILET)
                .requires(Blocks.RED_CONCRETE)
                .requires(Blocks.GREEN_CONCRETE)
                .requires(Blocks.BLUE_CONCRETE)
                .requires(PItems.POOP.get())
                .unlockedBy(getItemName(PItems.POOP), has(PItems.POOP.get()))
                .save(recipeOutput, PoopSky.loc("hard_toilet_from_rainbow"));

        buildSieveRecipes(recipeOutput);
        buildpopExplosionRecipes(recipeOutput);
        buildAnalPressingRecipes(recipeOutput);
        buildBreedingBoxRecipes(recipeOutput);
        buildFlyNestRecipes(recipeOutput);
    }

    private void buildpopExplosionRecipes(RecipeOutput recipeOutput) {
        record PopExplosionEntry(ItemLike input, ItemLike output, int radius) {
            static PopExplosionEntry of(ItemLike input, ItemLike output) {
                return new PopExplosionEntry(input, output, 0);
            }

            static PopExplosionEntry of(ItemLike input, ItemLike output, int radius) {
                return new PopExplosionEntry(input, output, radius);
            }
        }
        List<PopExplosionEntry> recipes = List.of(
                PopExplosionEntry.of(Blocks.COBBLESTONE, Blocks.GRAVEL),
                PopExplosionEntry.of(Blocks.GRAVEL, Blocks.SAND),
                PopExplosionEntry.of(Blocks.COAL_BLOCK, Items.DIAMOND, 6)
        );

        for (PopExplosionEntry entry : recipes) {
            var builder = POPExplosionRecipeBuilder.transform(entry.input(), entry.output());
            if (entry.radius() > 0) {
                builder.withRadius(entry.radius());
            }
            builder.unlockedBy(getItemName(entry.input()), has(entry.input()))
                    .save(recipeOutput, getModConversionRecipeName(entry.input(), entry.output()));
        }
    }

    private void buildAnalPressingRecipes(RecipeOutput recipeOutput) {
        record AnalPressingEntry(Block input, Block output, Block replaceTarget) {
            static AnalPressingEntry of(Block input, Block output) {
                return new AnalPressingEntry(input, output, null);
            }

            static AnalPressingEntry ofDeepslate(Block input, Block output) {
                return new AnalPressingEntry(input, output, Blocks.DEEPSLATE);
            }

            static AnalPressingEntry ofNetherrack(Block input, Block output) {
                return new AnalPressingEntry(input, output, Blocks.NETHERRACK);
            }
        }
        List<AnalPressingEntry> recipes = List.of(
                AnalPressingEntry.of(Blocks.RAW_IRON_BLOCK, Blocks.IRON_ORE),
                AnalPressingEntry.of(Blocks.RAW_COPPER_BLOCK, Blocks.COPPER_ORE),
                AnalPressingEntry.of(Blocks.RAW_GOLD_BLOCK, Blocks.GOLD_ORE),
                AnalPressingEntry.of(Blocks.IRON_BLOCK, Blocks.IRON_ORE),
                AnalPressingEntry.of(Blocks.COPPER_BLOCK, Blocks.COPPER_ORE),
                AnalPressingEntry.of(Blocks.GOLD_BLOCK, Blocks.GOLD_ORE),
                AnalPressingEntry.of(Blocks.COAL_BLOCK, Blocks.COAL_ORE),
                AnalPressingEntry.of(Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE),
                AnalPressingEntry.of(Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE),
                AnalPressingEntry.of(Blocks.REDSTONE_BLOCK, Blocks.REDSTONE_ORE),
                AnalPressingEntry.of(Blocks.EMERALD_BLOCK, Blocks.EMERALD_ORE),

                AnalPressingEntry.ofDeepslate(Blocks.RAW_IRON_BLOCK, Blocks.DEEPSLATE_IRON_ORE),
                AnalPressingEntry.ofDeepslate(Blocks.RAW_COPPER_BLOCK, Blocks.DEEPSLATE_COPPER_ORE),
                AnalPressingEntry.ofDeepslate(Blocks.RAW_GOLD_BLOCK, Blocks.DEEPSLATE_GOLD_ORE),
                AnalPressingEntry.ofDeepslate(Blocks.IRON_BLOCK, Blocks.DEEPSLATE_IRON_ORE),
                AnalPressingEntry.ofDeepslate(Blocks.COPPER_BLOCK, Blocks.DEEPSLATE_COPPER_ORE),
                AnalPressingEntry.ofDeepslate(Blocks.GOLD_BLOCK, Blocks.DEEPSLATE_GOLD_ORE),
                AnalPressingEntry.ofDeepslate(Blocks.COAL_BLOCK, Blocks.DEEPSLATE_COAL_ORE),
                AnalPressingEntry.ofDeepslate(Blocks.DIAMOND_BLOCK, Blocks.DEEPSLATE_DIAMOND_ORE),
                AnalPressingEntry.ofDeepslate(Blocks.LAPIS_BLOCK, Blocks.DEEPSLATE_LAPIS_ORE),
                AnalPressingEntry.ofDeepslate(Blocks.REDSTONE_BLOCK, Blocks.DEEPSLATE_REDSTONE_ORE),
                AnalPressingEntry.ofDeepslate(Blocks.EMERALD_BLOCK, Blocks.DEEPSLATE_EMERALD_ORE),

                AnalPressingEntry.ofNetherrack(Blocks.RAW_GOLD_BLOCK, Blocks.NETHER_GOLD_ORE),
                AnalPressingEntry.ofNetherrack(Blocks.GOLD_BLOCK, Blocks.NETHER_GOLD_ORE),
                AnalPressingEntry.ofNetherrack(Blocks.QUARTZ_BLOCK, Blocks.NETHER_QUARTZ_ORE),
                AnalPressingEntry.ofNetherrack(Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS)
        );

        for (AnalPressingEntry entry : recipes) {
            var builder = AnalPressingRecipeBuilder.analPressing(entry.input(), entry.output());
            if (entry.replaceTarget() != null) {
                builder.replaceTarget(entry.replaceTarget());
            }
            builder.unlockedBy(getItemName(entry.output()), has(entry.input()))
                    .save(recipeOutput, getModConversionRecipeName(entry.input(), entry.output()));
        }
    }

    private void buildSieveRecipes(RecipeOutput recipeOutput) {
        RecipeOutput createLoaded = recipeOutput.withConditions(modLoaded(PSMods.CREATE.id()));
        RecipeOutput createNotLoaded = recipeOutput.withConditions(not(modLoaded(PSMods.CREATE.id())));

        SieveRecipeBuilder.sieve(PBlocks.POOP_BLOCK, 200)
                .addOutput(Items.IRON_NUGGET, 8)
                .addOutput(Items.IRON_NUGGET, 8, 0.75F)
                .addOutput(Items.IRON_NUGGET, 8, 0.5F)
                .addOutput(Items.RAW_IRON, 0.5F)
                .unlockedBy(getItemName(PBlocks.SIEVE.get()), has(PBlocks.SIEVE.get()))
                .save(createNotLoaded, "poop_block");
        SieveRecipeBuilder.sieve(PBlocks.POOP_BLOCK, 200)
                .addOutput(Items.IRON_NUGGET, 8)
                .addOutput(AllItems.ZINC_NUGGET, 8)
                .addOutput(Items.IRON_NUGGET, 8, 0.5F)
                .addOutput(AllItems.ZINC_NUGGET, 8, 0.5F)
                .addOutput(Items.RAW_IRON, 0.5F)
                .addOutput(AllItems.RAW_ZINC, 0.5F)
                .unlockedBy(getItemName(PBlocks.SIEVE.get()), has(PBlocks.SIEVE.get()))
                .save(createLoaded, "poop_block_has_create");

        SieveRecipeBuilder.sieve(PBlocks.CHILI_POOP_BLOCK, 300)
                .addOutput(Items.QUARTZ, 4)
                .addOutput(Items.QUARTZ, 4, 0.5F)
                .addOutput(Items.NETHER_WART, 2, 0.75F)
                .addOutput(Items.MAGMA_CREAM, 0.5F)
                .addOutput(Items.GHAST_TEAR, 0.2F)
                .unlockedBy(getItemName(PBlocks.SIEVE.get()), has(PBlocks.SIEVE.get()))
                .save(recipeOutput, "chili_poop_block");

        SieveRecipeBuilder.sieve(PBlocks.GOLDEN_POOP_BLOCK, 300)
                .addOutput(Items.GOLD_NUGGET, 8)
                .addOutput(Items.GOLD_NUGGET, 8, 0.75F)
                .addOutput(Items.RAW_GOLD, 0.5F)
                .unlockedBy(getItemName(PBlocks.SIEVE.get()), has(PBlocks.SIEVE.get()))
                .save(recipeOutput, "golden_poop_block");

        SieveRecipeBuilder.sieve(PBlocks.RAW_POOP_BLOCK, 100)
                .addOutput(Items.RAW_COPPER)
                .addOutput(Items.LAPIS_LAZULI, 0.8F)
                .addOutput(Items.REDSTONE, 0.75F)
                .addOutput(Items.AMETHYST_SHARD, 0.25F)
                .unlockedBy(getItemName(PBlocks.SIEVE.get()), has(PBlocks.SIEVE.get()))
                .save(recipeOutput, "raw_poop_block");

        SieveRecipeBuilder.sieve(PBlocks.RAW_SAPLING_POOP_BLOCK, 100)
                .addOutput(Items.SUNFLOWER).addOutput(Items.LILAC)
                .addOutput(Items.ROSE_BUSH).addOutput(Items.PEONY)
                .addOutput(Items.VINE, 0.75F)
                .addOutput(Items.SMALL_DRIPLEAF, 0.5F)
                .addOutput(Items.SNIFFER_EGG, 0.05F)
                .unlockedBy(getItemName(PBlocks.SIEVE.get()), has(PBlocks.SIEVE.get()))
                .save(recipeOutput, "raw_sapling_poop_block");

        SieveRecipeBuilder.sieve(PBlocks.RAW_SEA_POOP_BLOCK, 100)
                .addOutput(Items.SEAGRASS, 2).addOutput(Items.SEAGRASS, 0.75F)
                .addOutput(Items.LILY_PAD)
                .addOutput(Items.SEA_PICKLE, 0.5F)
                .addOutput(Items.KELP, 0.5F)
                .addOutput(Items.PRISMARINE_SHARD, 0.25F)
                .addOutput(Items.PRISMARINE_CRYSTALS, 0.2F)
                .addOutput(Items.NAUTILUS_SHELL, 0.09F)
                .addOutput(Items.HEART_OF_THE_SEA, 0.01F)
                .unlockedBy(getItemName(PBlocks.SIEVE.get()), has(PBlocks.SIEVE.get()))
                .save(recipeOutput, "raw_sea_poop_block");

        SieveRecipeBuilder.sieve(PBlocks.RAW_WITHER_POOP_BLOCK, 100)
                .addOutput(Items.COAL)
                .addOutput(Items.NETHERITE_SCRAP, 0.1F)
                .addOutput(Items.WITHER_SKELETON_SKULL, 0.005F)
                .unlockedBy(getItemName(PBlocks.SIEVE.get()), has(PBlocks.SIEVE.get()))
                .save(recipeOutput, "raw_wither_poop_block");
        SieveRecipeBuilder.sieve(Blocks.CACTUS, 100)
                .addOutput(PItems.KING_OF_DRAGON_FRUIT.get())
                .addOutput(PItems.KING_OF_DRAGON_FRUIT.get(), 0.5F)
                .unlockedBy(getItemName(PBlocks.SIEVE.get()), has(PBlocks.SIEVE.get()))
                .save(recipeOutput, "cactus");
    }

    private void buildBreedingBoxRecipes(RecipeOutput recipeOutput) {
        record MutationRecipe(String p1, String p2, String result) {
            static MutationRecipe of(String p1, String p2, String result) {
                return new MutationRecipe(p1, p2, result);
            }
        }
        List<MutationRecipe> breedingRecipes = List.of(
                MutationRecipe.of("green", "blue", "cyan"),
                MutationRecipe.of("purple", "pink", "magenta"),
                MutationRecipe.of("red", "blue", "purple"),
                MutationRecipe.of("red", "yellow", "orange"),
                MutationRecipe.of("white", "black", "gray"),
                MutationRecipe.of("white", "blue", "light_blue"),
                MutationRecipe.of("white", "gray", "light_gray"),
                MutationRecipe.of("white", "green", "lime"),
                MutationRecipe.of("white", "red", "pink")
        );

        for (MutationRecipe recipe : breedingRecipes) {
            String id = recipe.p1 + "_plus_" + recipe.p2;
            var builder = BreedingBoxRecipeBuilder.breedingBox(recipe.p1, recipe.p2, recipe.result);
            builder.unlockedBy(getHasName(PBlocks.FLY_NEST), has(PBlocks.FLY_NEST))
                    .save(recipeOutput, id);
        }
    }

    private void buildFlyNestRecipes(RecipeOutput recipeOutput) {
        LinkedHashMap<String, ItemLike> flyNestMap = new LinkedHashMap<>();
        flyNestMap.put("normal", PItems.MAGGOTS_SEEDS);
        flyNestMap.put("white", Items.BONE_MEAL);
        flyNestMap.put("black", Items.COAL);
        flyNestMap.put("green", Items.CACTUS);
        flyNestMap.put("yellow", Items.YELLOW_DYE);
        flyNestMap.put("blue", Items.LAPIS_LAZULI);
        flyNestMap.put("red", Items.REDSTONE);
        flyNestMap.put("brown", Items.COCOA_BEANS);
        flyNestMap.put("gray", Items.GRAY_DYE);
        flyNestMap.put("light_gray", Items.LIGHT_GRAY_DYE);
        flyNestMap.put("light_blue", Items.LIGHT_BLUE_DYE);
        flyNestMap.put("lime", Items.SEA_PICKLE);
        flyNestMap.put("magenta", Items.MAGENTA_DYE);
        flyNestMap.put("cyan", Items.PRISMARINE_CRYSTALS);
        flyNestMap.put("pink", Items.PINK_DYE);
        flyNestMap.put("orange", Items.ORANGE_DYE);
        flyNestMap.put("purple", Items.AMETHYST_SHARD);

        flyNestMap.forEach((typeId, result) -> FlyNestRecipeBuilder.flyNest(typeId, result)
                .unlockedBy(getHasName(PBlocks.FLY_NEST), has(PBlocks.FLY_NEST))
                .save(recipeOutput, typeId));
    }

    private void toiletRecipes(RecipeOutput recipeOutput, ItemLike toilet, ItemLike block, ToiletType toiletType) {
        new ToiletShapedRecipeBuilder(RecipeCategory.BUILDING_BLOCKS, toilet, toiletType)
                .pattern("P")
                .pattern("#")
                .define('P', PItems.POOP.get())
                .define('#', block)
                .unlockedBy(getItemName(PItems.POOP), has(PItems.POOP.get()))
                .save(recipeOutput, PoopSky.loc(getItemName(toilet) + "_from_" + toiletType.id()));
    }

    public void stairsRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 8)
                .pattern("P  ")
                .pattern("PP ")
                .pattern("PPP")
                .define('P', input)
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput);
    }

    private void slabRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 6)
                .pattern("PPP")
                .define('P', input)
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput);
    }

    private void verticalSlabRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 6)
                .pattern("P")
                .pattern("P")
                .pattern("P")
                .define('P', input)
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput);
    }

    private void wallRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 6)
                .pattern("PPP")
                .pattern("PPP")
                .define('P', input)
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput);
    }

    public static void offerCompactingRecipe(RecipeOutput recipeOutput, RecipeCategory category, ItemLike output, ItemLike input) {
        ShapedRecipeBuilder.shaped(category, output)
                .define('#', input)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput, getConversionRecipeName(output) + "_from_compacting");
    }

    public static void offer2x2CompactingRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike input) {
        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, RecipeCategory.MISC, output, input, 1, 4);
    }

    public static void offer2x2CompactingRecipe(RecipeOutput recipeOutput, RecipeCategory category, RecipeCategory category2, ItemLike output, ItemLike input, int count, int count2) {
        ShapedRecipeBuilder.shaped(category, output, count)
                .define('#', input)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput, getConversionRecipeName(output, input));
        ShapelessRecipeBuilder.shapeless(category2, input, count2)
                .requires(output)
                .unlockedBy(getItemName(output), has(output))
                .save(recipeOutput, getConversionRecipeName(input, output));
    }

    public static void offer2x2CompactingRecipe(RecipeOutput recipeOutput, RecipeCategory category, ItemLike output, ItemLike input, int count) {
        ShapedRecipeBuilder.shaped(category, output, count)
                .define('#', input)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput, getConversionRecipeName(output, input));
    }

    private void create1x2ShapelessFrom(RecipeOutput recipeOutput, ItemLike output, ItemLike input1, ItemLike input2) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, output)
                .requires(input1).requires(input2)
                .unlockedBy(getItemName(input2), has(input2))
                .save(recipeOutput, getConversionRecipeName(output, input2));
    }

    private void create1x2ShapelessFrom(RecipeOutput recipeOutput, ItemLike output, ItemLike input1, ItemLike input2, int count) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, output, count)
                .requires(input1).requires(input2)
                .unlockedBy(getItemName(input2), has(input2))
                .save(recipeOutput, getConversionRecipeName(output, input2));
    }

    protected static void shapeless1x1Recipe(RecipeOutput recipeOutput, ItemLike result, ItemLike input, ItemLike input1) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result)
                .requires(input1).requires(input)
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput, getConversionRecipeName(result));
    }

    protected static void omenSmithing(RecipeOutput recipeOutput, Item ingredientItem, RecipeCategory category, Item resultItem) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(PItems.OMEN_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), Ingredient.of(PItems.OMINOUS_FILTHY_INGOT), category, resultItem
                )
                .unlocks("has_ominous_filthy_ingot", has(PItems.OMINOUS_FILTHY_INGOT))
                .save(recipeOutput, PoopSky.MOD_ID + ":" + getItemName(resultItem) + "_smithing");
    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredients, category, result, experience, cookingTime, group, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredients, category, result, experience, cookingTime, group, "_from_blasting");
    }

    protected static void oreCooking(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.CAMPFIRE_COOKING_RECIPE, CampfireCookingRecipe::new, ingredients, category, result, experience, cookingTime, group, "_from_campfire_cooking");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> serializer, AbstractCookingRecipe.Factory<T> recipeFactory, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String suffix) {
        for (ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, serializer, recipeFactory)
                    .group(group)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, PoopSky.MOD_ID + ":" + getItemName(result) + suffix + "_" + getItemName(itemlike));
        }
    }

    protected static void stonecutterResult(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, ItemLike material) {
        stonecutterResult(recipeOutput, category, result, material, 1);
    }

    protected static void stonecutterResult(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, ItemLike material, int resultCount) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), category, result, resultCount)
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput, getConversionRecipeName(result, material) + "_stonecutting");
    }

    protected static void copySmithingTemplate(RecipeOutput recipeOutput, ItemLike template, ItemLike baseItem, ItemLike item) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, template, 2).define('#', item).define('C', baseItem).define('S', template).pattern("#S#").pattern("#C#").pattern("###").unlockedBy(getHasName(template), has(template)).save(recipeOutput);
    }

    protected static void nineBlockStorageRecipes(RecipeOutput recipeOutput, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed) {
        nineBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed, getItemName(packed) + "_from_" + getItemName(unpacked), getItemName(unpacked) + "_from_" + getItemName(packed));
    }

    protected static void nineBlockStorageRecipes(RecipeOutput recipeOutput, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, String unpackedName) {
        ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 9).requires(packed).unlockedBy(getHasName(packed), has(packed)).save(recipeOutput, PoopSky.loc(unpackedName));
        ShapedRecipeBuilder.shaped(packedCategory, packed).define('#', unpacked).pattern("###").pattern("###").pattern("###").unlockedBy(getHasName(unpacked), has(unpacked)).save(recipeOutput, PoopSky.loc(packedName));
    }

    protected static String getConversionRecipeName(ItemLike result) {
        return PoopSky.MOD_ID + ":" + getItemName(result);
    }

    protected static String getConversionRecipeName(ItemLike result, ItemLike input) {
        return PoopSky.MOD_ID + ":" + getItemName(result) + "_from_" + getItemName(input);
    }

    protected static String getModConversionRecipeName(ItemLike result, ItemLike input) {
        return getItemName(input) + "_to_" + getItemName(result);
    }
}