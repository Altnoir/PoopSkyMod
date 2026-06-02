package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.recipe.SieveRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PSRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public PSRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        List<ItemLike> POOP_LIST = List.of(PSBlocks.POOP_BLOCK);
        List<ItemLike> POOP_BRICK_LIST = List.of(PSBlocks.POOP_BRICKS);
        List<ItemLike> SMOOTH_POOP_LIST = List.of(PSBlocks.DRIED_POOP_BLOCK);
        List<ItemLike> TILE_BLOCK_LIST = List.of(PSBlocks.RAW_POOP_BLOCK);
        List<ItemLike> MAGGOTS_LIST = List.of(PSItems.MAGGOTS_SEEDS);
        List<ItemLike> ROUNDWORM_LIST = List.of(PSItems.ROUNDWORM);

        shapeless1x1Recipe(recipeOutput, Blocks.CRIMSON_NYLIUM, Blocks.CRIMSON_FUNGUS, Blocks.NETHERRACK);
        shapeless1x1Recipe(recipeOutput, Blocks.WARPED_NYLIUM, Blocks.WARPED_FUNGUS, Blocks.NETHERRACK);

        oreSmelting(recipeOutput, POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK, 0.1F, 200, "dried_poop_block");
        oreBlasting(recipeOutput, POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK, 0.1F, 100, "dried_poop_block");

        oreSmelting(recipeOutput, POOP_BRICK_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CRACKED_POOP_BRICKS, 0.1F, 200, "cracked_poop_bricks");
        oreBlasting(recipeOutput, POOP_BRICK_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CRACKED_POOP_BRICKS, 0.1F, 100, "cracked_poop_bricks");

        oreSmelting(recipeOutput, SMOOTH_POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK, 0.1F, 200, "smooth_poop_block");
        oreBlasting(recipeOutput, SMOOTH_POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK, 0.1F, 100, "smooth_poop_block");

        oreSmelting(recipeOutput, TILE_BLOCK_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.TILE_BLOCK, 0.1F, 200, "tile_block");
        oreBlasting(recipeOutput, TILE_BLOCK_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.TILE_BLOCK, 0.1F, 100, "tile_block");

        oreCooking(recipeOutput, List.of(PSItems.POOP.get()), RecipeCategory.MISC, Items.COCOA_BEANS, 0.35F, 600, "cocoa_beans");
        oreSmelting(recipeOutput, ROUNDWORM_LIST, RecipeCategory.MISC, Items.STRING, 0.35F, 200, "roundworm");
        oreCooking(recipeOutput, ROUNDWORM_LIST, RecipeCategory.MISC, Items.STRING, 0.35F, 200, "roundworm");
        // 食物
        oreSmelting(recipeOutput, MAGGOTS_LIST, RecipeCategory.BUILDING_BLOCKS, PSItems.BAKED_MAGGOTS, 0.35F, 200, "maggots_seeds");
        oreCooking(recipeOutput, RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, MAGGOTS_LIST, RecipeCategory.BUILDING_BLOCKS, PSItems.BAKED_MAGGOTS, 0.35F, 100, "maggots_seeds", "_from_smoking");
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PSItems.POOP_BREAD)
                .pattern("PMP")
                .define('P', PSItems.POOP)
                .define('M', PSItems.MAGGOTS_SEEDS)
                .unlockedBy(getItemName(PSItems.MAGGOTS_SEEDS), has(PSItems.MAGGOTS_SEEDS))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PSItems.POOP_DUMPLINGS)
                .requires(PSItems.POOP_BALL.get())
                .requires(ItemTags.LEAVES)
                .unlockedBy(getItemName(PSItems.POOP_BALL.get()), has(PSItems.POOP_BALL.get()))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PSItems.POOP_SOUP)
                .requires(Items.BOWL)
                .requires(PSItems.POOP)
                .requires(PSItems.MAGGOTS_SEEDS)
                .requires(PSItems.URINE_BOTTLE)
                .unlockedBy(getItemName(PSItems.MAGGOTS_SEEDS), has(PSItems.MAGGOTS_SEEDS))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PSItems.POOBURGER_MEAT.get())
                .requires(PSItems.POOP, 3)
                .requires(Items.EGG)
                .unlockedBy(getItemName(PSItems.POOP), has(PSItems.POOP))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PSItems.POOBURGER.get())
                .pattern("P")
                .pattern("M")
                .pattern("P")
                .define('P', Items.BREAD)
                .define('M', PSItems.POOBURGER_MEAT)
                .unlockedBy(getItemName(PSItems.POOBURGER_MEAT), has(PSItems.POOBURGER_MEAT))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PSItems.POODDING.get(), 2)
                .requires(PSItems.POOP_BALL)
                .requires(Items.EGG).requires(Items.SUGAR)
                .unlockedBy(getItemName(PSItems.POOP_BALL), has(PSItems.POOP_BALL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PSBlocks.POOP_CAKE.get())
                .pattern("MMM")
                .pattern("SES")
                .pattern("PPP")
                .define('M', PSItems.MAGGOTS_SEEDS)
                .define('S', Items.SUGAR).define('E', Items.EGG)
                .define('P', PSItems.POOP)
                .unlockedBy(getItemName(PSItems.MAGGOTS_SEEDS), has(PSItems.MAGGOTS_SEEDS))
                .save(recipeOutput);

        // 杂
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.COBWEB)
                .pattern("S S")
                .pattern(" P ")
                .pattern("S S")
                .define('P', PSItems.POOP_BALL)
                .define('S', PSItems.MAGGOTS_SEEDS)
                .unlockedBy(getItemName(PSItems.MAGGOTS_SEEDS), has(PSItems.MAGGOTS_SEEDS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSItems.WITHER_POOP_BALL.get(), 8)
                .pattern("PPP")
                .pattern("PSP")
                .pattern("PPP")
                .define('P', PSItems.POOP_BALL)
                .define('S', Items.WITHER_ROSE)
                .unlockedBy(getItemName(Items.WITHER_ROSE), has(Items.WITHER_ROSE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSItems.GOLDEN_POOP.get())
                .pattern("PPP")
                .pattern("PSP")
                .pattern("PPP")
                .define('P', Items.GOLD_NUGGET)
                .define('S', PSItems.POOP)
                .unlockedBy(getItemName(Items.GOLD_NUGGET), has(Items.GOLD_NUGGET))
                .save(recipeOutput);

        // 建筑
        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, PSItems.POOP, 1);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PSItems.TOILET_PLUG_WAND)
                .requires(PSItems.TOILET_PLUG.get())
                .requires(PSItems.POOP.get())
                .requires(Items.ENDER_EYE)
                .unlockedBy(getItemName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PSItems.TIME_BELL)
                .requires(Items.BELL)
                .requires(PSItems.POOP.get())
                .requires(Items.DRAGON_EGG)
                .unlockedBy(getItemName(Items.DRAGON_EGG), has(Items.DRAGON_EGG))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PSItems.POOP, 4)
                .requires(PSBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PSBlocks.POOP_BLOCK), has(PSBlocks.POOP_BLOCK))
                .save(recipeOutput);

        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CHILI_POOP_BLOCK, PSItems.CHILI_POOP, 1);
        stairsRecipe(recipeOutput, PSBlocks.POOP_STAIRS, PSBlocks.POOP_BLOCK);
        slabRecipe(recipeOutput, PSBlocks.POOP_SLAB, PSBlocks.POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PSBlocks.POOP_VERTICAL_SLAB, PSBlocks.POOP_BLOCK);
        wallRecipe(recipeOutput, PSBlocks.POOP_WALL, PSBlocks.POOP_BLOCK);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PSItems.CHILI_POOP, 4)
                .requires(PSBlocks.CHILI_POOP_BLOCK)
                .unlockedBy(getItemName(PSBlocks.CHILI_POOP_BLOCK), has(PSBlocks.CHILI_POOP_BLOCK))
                .save(recipeOutput);

        stairsRecipe(recipeOutput, PSBlocks.CHILI_POOP_STAIRS, PSBlocks.CHILI_POOP_BLOCK);
        slabRecipe(recipeOutput, PSBlocks.CHILI_POOP_SLAB, PSBlocks.CHILI_POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PSBlocks.CHILI_POOP_VERTICAL_SLAB, PSBlocks.CHILI_POOP_BLOCK);
        wallRecipe(recipeOutput, PSBlocks.CHILI_POOP_WALL, PSBlocks.CHILI_POOP_BLOCK);

        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.GOLDEN_POOP_BLOCK, PSItems.GOLDEN_POOP, 1);
        stairsRecipe(recipeOutput, PSBlocks.GOLDEN_POOP_STAIRS, PSBlocks.GOLDEN_POOP_BLOCK);
        slabRecipe(recipeOutput, PSBlocks.GOLDEN_POOP_SLAB, PSBlocks.GOLDEN_POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PSBlocks.GOLDEN_POOP_VERTICAL_SLAB, PSBlocks.GOLDEN_POOP_BLOCK);
        wallRecipe(recipeOutput, PSBlocks.GOLDEN_POOP_WALL, PSBlocks.GOLDEN_POOP_BLOCK);

        stairsRecipe(recipeOutput, PSBlocks.DRIED_POOP_BLOCK_STAIRS, PSBlocks.DRIED_POOP_BLOCK);
        slabRecipe(recipeOutput, PSBlocks.DRIED_POOP_BLOCK_SLAB, PSBlocks.DRIED_POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PSBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.DRIED_POOP_BLOCK);
        wallRecipe(recipeOutput, PSBlocks.DRIED_POOP_BLOCK_WALL, PSBlocks.DRIED_POOP_BLOCK);

        stairsRecipe(recipeOutput, PSBlocks.SMOOTH_POOP_BLOCK_STAIRS, PSBlocks.SMOOTH_POOP_BLOCK);
        slabRecipe(recipeOutput, PSBlocks.SMOOTH_POOP_BLOCK_SLAB, PSBlocks.SMOOTH_POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PSBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.SMOOTH_POOP_BLOCK);
        wallRecipe(recipeOutput, PSBlocks.SMOOTH_POOP_BLOCK_WALL, PSBlocks.SMOOTH_POOP_BLOCK);

        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK, PSBlocks.DRIED_POOP_BLOCK, 4);
        stairsRecipe(recipeOutput, PSBlocks.CUT_POOP_BLOCK_STAIRS, PSBlocks.CUT_POOP_BLOCK);
        slabRecipe(recipeOutput, PSBlocks.CUT_POOP_BLOCK_SLAB, PSBlocks.CUT_POOP_BLOCK);
        verticalSlabRecipe(recipeOutput, PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.CUT_POOP_BLOCK);
        wallRecipe(recipeOutput, PSBlocks.CUT_POOP_BLOCK_WALL, PSBlocks.CUT_POOP_BLOCK);

        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICKS, PSBlocks.POOP_BLOCK, 4);
        stairsRecipe(recipeOutput, PSBlocks.POOP_BRICK_STAIRS, PSBlocks.POOP_BRICKS);
        slabRecipe(recipeOutput, PSBlocks.POOP_BRICK_SLAB, PSBlocks.POOP_BRICKS);
        verticalSlabRecipe(recipeOutput, PSBlocks.POOP_BRICK_VERTICAL_SLAB, PSBlocks.POOP_BRICKS);
        wallRecipe(recipeOutput, PSBlocks.POOP_BRICK_WALL, PSBlocks.POOP_BRICKS);

        create1x2ShapelessFrom(recipeOutput, PSBlocks.MOSSY_POOP_BRICKS, PSBlocks.POOP_BRICKS, Blocks.MOSS_BLOCK);
        create1x2ShapelessFrom(recipeOutput, PSBlocks.MOSSY_POOP_BRICKS, PSBlocks.POOP_BRICKS, Blocks.VINE);
        stairsRecipe(recipeOutput, PSBlocks.MOSSY_POOP_BRICK_STAIRS, PSBlocks.MOSSY_POOP_BRICKS);
        slabRecipe(recipeOutput, PSBlocks.MOSSY_POOP_BRICK_SLAB, PSBlocks.MOSSY_POOP_BRICKS);
        verticalSlabRecipe(recipeOutput, PSBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB, PSBlocks.MOSSY_POOP_BRICKS);
        wallRecipe(recipeOutput, PSBlocks.MOSSY_POOP_BRICK_WALL, PSBlocks.MOSSY_POOP_BRICKS);

        stairsRecipe(recipeOutput, PSBlocks.TILE_BLOCK_STAIRS, PSBlocks.TILE_BLOCK);
        slabRecipe(recipeOutput, PSBlocks.TILE_BLOCK_SLAB, PSBlocks.TILE_BLOCK);
        verticalSlabRecipe(recipeOutput, PSBlocks.TILE_BLOCK_VERTICAL_SLAB, PSBlocks.TILE_BLOCK);
        wallRecipe(recipeOutput, PSBlocks.TILE_BLOCK_WALL, PSBlocks.TILE_BLOCK);

        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PSItems.POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PSBlocks.RAW_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PSItems.SAPING_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PSBlocks.RAW_SAPING_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PSItems.WITHER_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PSBlocks.RAW_WITHER_POOP_BLOCK);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK)
                .pattern("P")
                .pattern("P")
                .define('P', PSBlocks.POOP_SLAB)
                .unlockedBy(getItemName(PSBlocks.POOP_BLOCK), has(PSBlocks.POOP_BLOCK))
                .save(recipeOutput, getConversionRecipeName(PSBlocks.POOP_BLOCK) + "_from_slab");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK)
                .pattern("PP")
                .define('P', PSBlocks.POOP_VERTICAL_SLAB)
                .unlockedBy(getItemName(PSBlocks.POOP_BLOCK), has(PSBlocks.POOP_BLOCK))
                .save(recipeOutput, getConversionRecipeName(PSBlocks.POOP_BLOCK) + "_from_vertical_slab");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BUTTON)
                .requires(PSItems.POOP.get())
                .unlockedBy(getItemName(PSItems.POOP), has(PSItems.POOP))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PRESSURE_PLATE)
                .pattern("PP")
                .define('P', PSItems.POOP)
                .unlockedBy(getItemName(PSItems.POOP), has(PSItems.POOP))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE, 3)
                .pattern("BPB")
                .pattern("BPB")
                .define('B', PSBlocks.POOP_BLOCK)
                .define('P', PSItems.POOP)
                .unlockedBy(getItemName(PSBlocks.POOP_BLOCK), has(PSBlocks.POOP_BLOCK))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE_GATE)
                .pattern("PBP")
                .pattern("PBP")
                .define('B', PSBlocks.POOP_BLOCK)
                .define('P', PSItems.POOP)
                .unlockedBy(getItemName(PSBlocks.POOP_BLOCK), has(PSBlocks.POOP_BLOCK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_DOOR, 3)
                .pattern("PP")
                .pattern("PP")
                .pattern("PP")
                .define('P', PSBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PSBlocks.POOP_BLOCK), has(PSBlocks.POOP_BLOCK))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_TRAPDOOR, 2)
                .pattern("PP")
                .pattern("PP")
                .define('P', PSBlocks.POOP_SLAB)
                .unlockedBy(getItemName(PSBlocks.POOP_BLOCK), has(PSBlocks.POOP_BLOCK))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, 4)
                .requires(PSBlocks.POOP_EMPTY_LOG)
                .unlockedBy(getItemName(PSBlocks.POOP_LOG), has(PSBlocks.POOP_LOG))
                .save(recipeOutput, getConversionRecipeName(PSBlocks.POOP_BLOCK, PSBlocks.POOP_EMPTY_LOG));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, 4)
                .requires(PSBlocks.STRIPPED_POOP_EMPTY_LOG)
                .unlockedBy(getItemName(PSBlocks.POOP_LOG), has(PSBlocks.POOP_LOG))
                .save(recipeOutput, getConversionRecipeName(PSBlocks.POOP_BLOCK, PSBlocks.STRIPPED_POOP_EMPTY_LOG));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PIECE, 3)
                .pattern("PP")
                .define('P', PSBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PSBlocks.POOP_BLOCK), has(PSBlocks.POOP_BLOCK))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PSItems.LAWRENCE_MUSIC_DISC)
                .requires(Tags.Items.MUSIC_DISCS)
                .requires(PSItems.POOP)
                .unlockedBy(getItemName(PSItems.POOP), has(PSItems.POOP))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.COMPOOPER)
                .pattern("S S")
                .pattern("S S")
                .pattern("SSS")
                .define('S', Blocks.MOSSY_COBBLESTONE_SLAB)
                .unlockedBy(getItemName(Blocks.MOSSY_COBBLESTONE_SLAB), has(Blocks.MOSSY_COBBLESTONE_SLAB))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.SIEVE)
                .pattern("SAS")
                .pattern("S S")
                .pattern("S S")
                .define('S', Blocks.MOSSY_COBBLESTONE_WALL)
                .define('A', Items.STRING)
                .unlockedBy(getItemName(Blocks.MOSSY_COBBLESTONE_WALL), has(Blocks.MOSSY_COBBLESTONE_WALL))
                .save(recipeOutput);

        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOLIME_BLOCK.get(), PSItems.POOP_BALL.get(), 1);
        offerCompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOLIME_POOP_BLOCK.get(), PSBlocks.POOP_BLOCK.get());

        //原版物品配方
        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, Blocks.CRAFTING_TABLE, PSItems.SPALL, 1);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.COARSE_DIRT, 4)
                .pattern("PG")
                .pattern("GP")
                .define('G', Blocks.GRAVEL)
                .define('P', PSBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PSBlocks.POOP_BLOCK), has(PSBlocks.POOP_BLOCK))
                .save(recipeOutput, getConversionRecipeName(Blocks.COARSE_DIRT) + "_from_poop_block");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.POINTED_DRIPSTONE)
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .define('S', PSItems.SPALL)
                .unlockedBy(getItemName(PSItems.SPALL), has(PSItems.SPALL))
                .save(recipeOutput, getConversionRecipeName(Blocks.POINTED_DRIPSTONE) + "_from_spall");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.FLINT)
                .pattern("S")
                .pattern("S")
                .define('S', PSItems.SPALL)
                .unlockedBy(getItemName(PSItems.SPALL), has(PSItems.SPALL))
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
                .define('P', PSItems.POOP_BALL)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PSItems.POOP_BALL), has(PSItems.POOP_BALL))
                .save(recipeOutput, getConversionRecipeName(Items.TORCH) + "_from_poop_ball");

        offerCompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE, PSItems.SPALL);
        create1x2ShapelessFrom(recipeOutput, Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.MOSS_BLOCK);

        create1x2ShapelessFrom(recipeOutput, Blocks.DIORITE, Blocks.COBBLESTONE, Blocks.CLAY, 2);
        create1x2ShapelessFrom(recipeOutput, Blocks.GRANITE, Blocks.COBBLESTONE, Blocks.DRIPSTONE_BLOCK, 2);
        create1x2ShapelessFrom(recipeOutput, Blocks.DIRT, Blocks.MUD, PSItems.POOP.get());
        //create1x2ShapelessFrom(recipeOutput, Blocks.TUFF, Blocks.ANDESITE, PSItems.SPALL);
        //create1x2ShapelessFrom(recipeOutput, Blocks.CALCITE, Blocks.DIORITE, PSItems.SPALL);

        //切石配方
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.STOOL, PSBlocks.DRIED_POOP_BLOCK, 2);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PIECE, PSBlocks.POOP_BLOCK, 8);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_STAIRS, PSBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_SLAB, PSBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_VERTICAL_SLAB, PSBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE, PSBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE_GATE, PSBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_WALL, PSBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_DOOR, PSBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_TRAPDOOR, PSBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PRESSURE_PLATE, PSBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BUTTON, PSBlocks.POOP_BLOCK, 4);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CHILI_POOP_STAIRS, PSBlocks.CHILI_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CHILI_POOP_SLAB, PSBlocks.CHILI_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CHILI_POOP_VERTICAL_SLAB, PSBlocks.CHILI_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CHILI_POOP_WALL, PSBlocks.CHILI_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.GOLDEN_POOP_STAIRS, PSBlocks.GOLDEN_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.GOLDEN_POOP_SLAB, PSBlocks.GOLDEN_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.GOLDEN_POOP_VERTICAL_SLAB, PSBlocks.GOLDEN_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.GOLDEN_POOP_WALL, PSBlocks.GOLDEN_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICKS, PSBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_STAIRS, PSBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_SLAB, PSBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_VERTICAL_SLAB, PSBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_WALL, PSBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_STAIRS, PSBlocks.POOP_BRICKS);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_SLAB, PSBlocks.POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_VERTICAL_SLAB, PSBlocks.POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_WALL, PSBlocks.POOP_BRICKS);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.MOSSY_POOP_BRICK_STAIRS, PSBlocks.MOSSY_POOP_BRICKS);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.MOSSY_POOP_BRICK_SLAB, PSBlocks.MOSSY_POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB, PSBlocks.MOSSY_POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.MOSSY_POOP_BRICK_WALL, PSBlocks.MOSSY_POOP_BRICKS);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK_STAIRS, PSBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK_SLAB, PSBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK_WALL, PSBlocks.DRIED_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK, PSBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_STAIRS, PSBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_SLAB, PSBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_WALL, PSBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_STAIRS, PSBlocks.CUT_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_SLAB, PSBlocks.CUT_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.CUT_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_WALL, PSBlocks.CUT_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK_STAIRS, PSBlocks.SMOOTH_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK_SLAB, PSBlocks.SMOOTH_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.SMOOTH_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK_WALL, PSBlocks.SMOOTH_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.STRIPPED_POOP_LOG, PSBlocks.POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_EMPTY_LOG, PSBlocks.POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.STRIPPED_POOP_EMPTY_LOG, PSBlocks.STRIPPED_POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.STRIPPED_POOP_EMPTY_LOG, PSBlocks.POOP_EMPTY_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, PSBlocks.POOP_EMPTY_LOG, 4);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, PSBlocks.STRIPPED_POOP_EMPTY_LOG, 4);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.TILE_BLOCK_STAIRS, PSBlocks.TILE_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.TILE_BLOCK_SLAB, PSBlocks.TILE_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.TILE_BLOCK_VERTICAL_SLAB, PSBlocks.TILE_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PSBlocks.TILE_BLOCK_WALL, PSBlocks.TILE_BLOCK);

        //厕所配方
        toiletRecipes(recipeOutput, ToiletBlocks.OAK_TOILET, Blocks.OAK_PLANKS);
        toiletRecipes(recipeOutput, ToiletBlocks.SPRUCE_TOILET, Blocks.SPRUCE_PLANKS);
        toiletRecipes(recipeOutput, ToiletBlocks.BIRCH_TOILET, Blocks.BIRCH_PLANKS);
        toiletRecipes(recipeOutput, ToiletBlocks.JUNGLE_TOILET, Blocks.JUNGLE_PLANKS);
        toiletRecipes(recipeOutput, ToiletBlocks.ACACIA_TOILET, Blocks.ACACIA_PLANKS);
        toiletRecipes(recipeOutput, ToiletBlocks.DARK_OAK_TOILET, Blocks.DARK_OAK_PLANKS);
        toiletRecipes(recipeOutput, ToiletBlocks.MANGROVE_TOILET, Blocks.MANGROVE_PLANKS);
        toiletRecipes(recipeOutput, ToiletBlocks.CRIMSON_TOILET, Blocks.CRIMSON_PLANKS);
        toiletRecipes(recipeOutput, ToiletBlocks.BAMBOO_TOILET, Blocks.BAMBOO_PLANKS);
        toiletRecipes(recipeOutput, ToiletBlocks.CHERRY_TOILET, Blocks.CHERRY_PLANKS);
        toiletRecipes(recipeOutput, ToiletBlocks.WARPED_TOILET, Blocks.WARPED_PLANKS);

        toiletRecipes(recipeOutput, ToiletBlocks.STONE_TOILET, Blocks.STONE);
        toiletRecipes(recipeOutput, ToiletBlocks.COBBLESTONE_TOILET, Blocks.COBBLESTONE);
        toiletRecipes(recipeOutput, ToiletBlocks.MOSSY_COBBLESTONE_TOILET, Blocks.MOSSY_COBBLESTONE);
        toiletRecipes(recipeOutput, ToiletBlocks.SMOOTH_STONE_TOILET, Blocks.SMOOTH_STONE);
        toiletRecipes(recipeOutput, ToiletBlocks.STONE_BRICK_TOILET, Blocks.STONE_BRICKS);
        toiletRecipes(recipeOutput, ToiletBlocks.MOSSY_STONE_BRICK_TOILET, Blocks.MOSSY_STONE_BRICKS);
        toiletRecipes(recipeOutput, ToiletBlocks.TILE_TOILET, PSBlocks.TILE_BLOCK);

        toiletRecipes(recipeOutput, ToiletBlocks.WHITE_CONCRETE_TOILET, Blocks.WHITE_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET, Blocks.LIGHT_GRAY_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.GRAY_CONCRETE_TOILET, Blocks.GRAY_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.BLACK_CONCRETE_TOILET, Blocks.BLACK_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.BROWN_CONCRETE_TOILET, Blocks.BROWN_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.RED_CONCRETE_TOILET, Blocks.RED_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.ORANGE_CONCRETE_TOILET, Blocks.ORANGE_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.YELLOW_CONCRETE_TOILET, Blocks.YELLOW_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.LIME_CONCRETE_TOILET, Blocks.LIME_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.GREEN_CONCRETE_TOILET, Blocks.GREEN_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.CYAN_CONCRETE_TOILET, Blocks.CYAN_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET, Blocks.LIGHT_BLUE_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.BLUE_CONCRETE_TOILET, Blocks.BLUE_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.PURPLE_CONCRETE_TOILET, Blocks.PURPLE_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.MAGENTA_CONCRETE_TOILET, Blocks.MAGENTA_CONCRETE);
        toiletRecipes(recipeOutput, ToiletBlocks.PINK_CONCRETE_TOILET, Blocks.PINK_CONCRETE);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ToiletBlocks.RAINBOW_TOILET, 3)
                .requires(ToiletBlocks.RED_CONCRETE_TOILET)
                .requires(ToiletBlocks.GREEN_CONCRETE_TOILET)
                .requires(ToiletBlocks.BLUE_CONCRETE_TOILET)
                .unlockedBy(getItemName(PSItems.POOP), has(PSItems.POOP.get()))
                .save(recipeOutput);

        buildSieveRecipes(recipeOutput);
    }

    private void buildSieveRecipes(RecipeOutput recipeOutput) {
        SieveRecipeBuilder.sieve(PSBlocks.POOP_BLOCK, 200)
                .addOutput(Items.IRON_NUGGET, 8)
                .addOutput(Items.IRON_NUGGET, 8, 0.75F)
                .addOutput(Items.IRON_NUGGET, 8, 0.5F)
                .addOutput(Items.RAW_IRON, 0.5F)
                .unlockedBy(getItemName(PSBlocks.SIEVE.get()), has(PSBlocks.SIEVE.get()))
                .save(recipeOutput, "poop_block");

        SieveRecipeBuilder.sieve(PSBlocks.CHILI_POOP_BLOCK, 300)
                .addOutput(Items.QUARTZ, 4)
                .addOutput(Items.QUARTZ, 4, 0.5F)
                .addOutput(Items.NETHER_WART, 2, 0.75F)
                .addOutput(Items.MAGMA_CREAM, 0.5F)
                .addOutput(Items.GHAST_TEAR, 0.2F)
                .unlockedBy(getItemName(PSBlocks.SIEVE.get()), has(PSBlocks.SIEVE.get()))
                .save(recipeOutput, "chili_poop_block");

        SieveRecipeBuilder.sieve(PSBlocks.GOLDEN_POOP_BLOCK, 300)
                .addOutput(Items.GOLD_NUGGET, 8)
                .addOutput(Items.GOLD_NUGGET, 8, 0.75F)
                .addOutput(Items.RAW_GOLD, 0.5F)
                .unlockedBy(getItemName(PSBlocks.SIEVE.get()), has(PSBlocks.SIEVE.get()))
                .save(recipeOutput, "golden_poop_block");

        SieveRecipeBuilder.sieve(PSBlocks.RAW_POOP_BLOCK, 100)
                .addOutput(Items.RAW_COPPER)
                .addOutput(Items.LAPIS_LAZULI, 0.8F)
                .addOutput(Items.REDSTONE, 0.75F)
                .addOutput(Items.DIAMOND, 0.15F)
                .unlockedBy(getItemName(PSBlocks.SIEVE.get()), has(PSBlocks.SIEVE.get()))
                .save(recipeOutput, "raw_poop_block");

        SieveRecipeBuilder.sieve(PSBlocks.RAW_SAPING_POOP_BLOCK, 100)
                .addOutput(Items.SUNFLOWER).addOutput(Items.LILAC)
                .addOutput(Items.ROSE_BUSH).addOutput(Items.PEONY)
                .addOutput(Items.VINE, 0.75F)
                .addOutput(Items.SMALL_DRIPLEAF, 0.5F)
                .addOutput(Items.SNIFFER_EGG, 0.05F)
                .unlockedBy(getItemName(PSBlocks.SIEVE.get()), has(PSBlocks.SIEVE.get()))
                .save(recipeOutput, "raw_saping_poop_block");

        SieveRecipeBuilder.sieve(PSBlocks.RAW_WITHER_POOP_BLOCK, 100)
                .addOutput(Items.COAL)
                .addOutput(Items.NETHERITE_SCRAP, 0.1F)
                .addOutput(Items.WITHER_SKELETON_SKULL, 0.01F)
                .unlockedBy(getItemName(PSBlocks.SIEVE.get()), has(PSBlocks.SIEVE.get()))
                .save(recipeOutput, "raw_wither_poop_block");
    }

    private void toiletRecipes(RecipeOutput recipeOutput, ItemLike toilet, ItemLike block) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, toilet)
                .pattern("P")
                .pattern("#")
                .define('P', PSItems.POOP.get())
                .define('#', block)
                .unlockedBy(getItemName(PSItems.POOP), has(PSItems.POOP.get()))
                .save(recipeOutput);
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

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        oreCooking(
                recipeOutput,
                RecipeSerializer.SMELTING_RECIPE,
                SmeltingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_smelting"
        );
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        oreCooking(
                recipeOutput,
                RecipeSerializer.BLASTING_RECIPE,
                BlastingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_blasting"
        );
    }

    protected static void oreCooking(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        oreCooking(
                recipeOutput,
                RecipeSerializer.CAMPFIRE_COOKING_RECIPE,
                CampfireCookingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_campfire_cooking"
        );
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

    protected static String getConversionRecipeName(ItemLike result) {
        return PoopSky.MOD_ID + ":" + getItemName(result);
    }

    protected static String getConversionRecipeName(ItemLike result, ItemLike input) {
        return PoopSky.MOD_ID + ":" + getItemName(result) + "_from_" + getItemName(input);
    }

}
