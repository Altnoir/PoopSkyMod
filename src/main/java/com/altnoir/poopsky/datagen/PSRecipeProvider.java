package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PSRecipeProvider extends FabricRecipeProvider {
    public PSRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        List<ItemConvertible> POOP_LIST = List.of(PSBlocks.POOP_BLOCK);
        List<ItemConvertible> POOP_BRICK_LIST = List.of(PSBlocks.POOP_BRICKS);
        List<ItemConvertible> SMOOTH_POOP_LIST = List.of(PSBlocks.DRIED_POOP_BLOCK);
        List<ItemConvertible> MAGGOTS_LIST = List.of(PSItems.MAGGOTS_SEEDS);

        offerSmelting(exporter, POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK, 0.1F, 200, "dried_poop_block");
        offerBlasting(exporter, POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK, 0.1F, 100, "dried_poop_block");

        offerSmelting(exporter, POOP_BRICK_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CRACKED_POOP_BRICKS, 0.1F, 200, "cracked_poop_bricks");
        offerBlasting(exporter, POOP_BRICK_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CRACKED_POOP_BRICKS, 0.1F, 100, "cracked_poop_bricks");

        offerSmelting(exporter, SMOOTH_POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK, 0.1F, 200, "smooth_poop_block");
        offerBlasting(exporter, SMOOTH_POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK, 0.1F, 100, "smooth_poop_block");

        //食物类
        offerSmelting(exporter, MAGGOTS_LIST, RecipeCategory.BUILDING_BLOCKS, PSItems.BAKED_MAGGOTS, 0.35F, 200, "maggots_seeds");
        offerMultipleOptions(exporter, RecipeSerializer.SMOKING, SmokingRecipe::new, MAGGOTS_LIST, RecipeCategory.BUILDING_BLOCKS, PSItems.BAKED_MAGGOTS, 0.35F, 100, "maggots_seeds", "_from_blasting");

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSItems.POOP_BREAD)
                .pattern("PMP")
                .input('P', PSItems.POOP)
                .input('M', PSItems.MAGGOTS_SEEDS)
                .criterion(hasItem(PSItems.MAGGOTS_SEEDS), conditionsFromItem(PSItems.MAGGOTS_SEEDS))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, PSItems.POOP_SOUP)
                .input(Items.BOWL).input(PSItems.POOP).input(PSItems.MAGGOTS_SEEDS).input(PSItems.URINE_BOTTLE)
                .criterion(hasItem(PSItems.MAGGOTS_SEEDS), conditionsFromItem(PSItems.MAGGOTS_SEEDS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_CAKE)
                .pattern("MMM")
                .pattern("SES")
                .pattern("PPP")
                .input('M', PSItems.MAGGOTS_SEEDS)
                .input('S', Items.SUGAR).input('E', Items.EGG)
                .input('P', PSItems.POOP)
                .criterion(hasItem(PSItems.MAGGOTS_SEEDS), conditionsFromItem(PSItems.MAGGOTS_SEEDS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.COBWEB)
                .pattern("SSS")
                .pattern("SPS")
                .pattern("SSS")
                .input('S', Items.STRING)
                .input('P', PSItems.POOP)
                .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                .offerTo(exporter);

        //主类
        offer2x2CompactingRecipe(exporter,RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, PSItems.POOP, 1);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, PSItems.TOILET_LINKER)
                .input(PSItems.POOP)
                .input(Items.ENDER_EYE)
                .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, PSItems.POOP, 4)
                .input(PSBlocks.POOP_BLOCK)
                .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                .offerTo(exporter);

        createStairsRecipe(exporter, PSBlocks.POOP_STAIRS, PSBlocks.POOP_BLOCK);
        createSlabRecipe(exporter, PSBlocks.POOP_SLAB, PSBlocks.POOP_BLOCK);
        createVerticalSlabRecipe(exporter, PSBlocks.POOP_VERTICAL_SLAB, PSBlocks.POOP_BLOCK);
        createWallRecipe(exporter, PSBlocks.POOP_WALL, PSBlocks.POOP_BLOCK);

        createStairsRecipe(exporter, PSBlocks.DRIED_POOP_BLOCK_STAIRS, PSBlocks.DRIED_POOP_BLOCK);
        createSlabRecipe(exporter, PSBlocks.DRIED_POOP_BLOCK_SLAB, PSBlocks.DRIED_POOP_BLOCK);
        createVerticalSlabRecipe(exporter, PSBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.DRIED_POOP_BLOCK);
        createWallRecipe(exporter, PSBlocks.DRIED_POOP_BLOCK_WALL, PSBlocks.DRIED_POOP_BLOCK);

        createStairsRecipe(exporter, PSBlocks.SMOOTH_POOP_BLOCK_STAIRS, PSBlocks.SMOOTH_POOP_BLOCK);
        createSlabRecipe(exporter, PSBlocks.SMOOTH_POOP_BLOCK_SLAB, PSBlocks.SMOOTH_POOP_BLOCK);
        createVerticalSlabRecipe(exporter, PSBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.SMOOTH_POOP_BLOCK);
        createWallRecipe(exporter, PSBlocks.SMOOTH_POOP_BLOCK_WALL, PSBlocks.SMOOTH_POOP_BLOCK);

        offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK, PSBlocks.DRIED_POOP_BLOCK, 4);
        createStairsRecipe(exporter, PSBlocks.CUT_POOP_BLOCK_STAIRS, PSBlocks.CUT_POOP_BLOCK);
        createSlabRecipe(exporter, PSBlocks.CUT_POOP_BLOCK_SLAB, PSBlocks.CUT_POOP_BLOCK);
        createVerticalSlabRecipe(exporter, PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PSBlocks.CUT_POOP_BLOCK);
        createWallRecipe(exporter, PSBlocks.CUT_POOP_BLOCK_WALL, PSBlocks.CUT_POOP_BLOCK);

        offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICKS, PSBlocks.POOP_BLOCK, 4);
        createStairsRecipe(exporter, PSBlocks.POOP_BRICK_STAIRS, PSBlocks.POOP_BLOCK);
        createSlabRecipe(exporter, PSBlocks.POOP_BRICK_SLAB, PSBlocks.POOP_BLOCK);
        createVerticalSlabRecipe(exporter, PSBlocks.POOP_BRICK_VERTICAL_SLAB, PSBlocks.POOP_BLOCK);
        createWallRecipe(exporter, PSBlocks.POOP_BRICK_WALL, PSBlocks.POOP_BLOCK);

        create1x2ShapelessFrom(exporter, PSBlocks.MOSSY_POOP_BRICKS, PSBlocks.POOP_BRICKS, Blocks.MOSS_BLOCK);
        create1x2ShapelessFrom(exporter, PSBlocks.MOSSY_POOP_BRICKS, PSBlocks.POOP_BRICKS, Blocks.VINE);
        createStairsRecipe(exporter, PSBlocks.MOSSY_POOP_BRICK_STAIRS, PSBlocks.MOSSY_POOP_BRICKS);
        createSlabRecipe(exporter, PSBlocks.MOSSY_POOP_BRICK_SLAB, PSBlocks.MOSSY_POOP_BRICKS);
        createVerticalSlabRecipe(exporter, PSBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB, PSBlocks.MOSSY_POOP_BRICKS);
        createWallRecipe(exporter, PSBlocks.MOSSY_POOP_BRICK_WALL, PSBlocks.MOSSY_POOP_BRICKS);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK)
                .pattern("P")
                .pattern("P")
                .input('P', PSBlocks.POOP_SLAB)
                .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                .offerTo(exporter, getItemPath(PSBlocks.POOP_BLOCK) + "_from_slab");

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK)
                .pattern("PP")
                .input('P', PSBlocks.POOP_VERTICAL_SLAB)
                .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                .offerTo(exporter, getItemPath(PSBlocks.POOP_BLOCK) + "_from_vertical_slab");

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BUTTON)
                .input(PSItems.POOP)
                .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PRESSURE_PLATE)
                .pattern("PP")
                .input('P', PSItems.POOP)
                .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE, 3)
                .pattern("BPB")
                .pattern("BPB")
                .input('B', PSBlocks.POOP_BLOCK)
                .input('P', PSItems.POOP)
                .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE_GATE)
                .pattern("PBP")
                .pattern("PBP")
                .input('B', PSBlocks.POOP_BLOCK)
                .input('P', PSItems.POOP)
                .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_DOOR, 3)
                .pattern("PP")
                .pattern("PP")
                .pattern("PP")
                .input('P', PSBlocks.POOP_BLOCK)
                .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_TRAPDOOR, 2)
                .pattern("PP")
                .pattern("PP")
                .input('P', PSBlocks.POOP_SLAB)
                .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, 4)
                .input(PSBlocks.POOP_EMPTY_LOG)
                .criterion(hasItem(PSBlocks.POOP_LOG), conditionsFromItem(PSBlocks.POOP_LOG))
                .offerTo(exporter, getItemPath(PSBlocks.POOP_BLOCK) + "_from_empty_log");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, 4)
                .input(PSBlocks.STRIPPED_POOP_EMPTY_LOG)
                .criterion(hasItem(PSBlocks.POOP_LOG), conditionsFromItem(PSBlocks.POOP_LOG))
                .offerTo(exporter,  getItemPath(PSBlocks.POOP_BLOCK) + "_from_stripped_empty_log");
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PIECE, 3)
                .pattern("PP")
                .input('P', PSBlocks.POOP_BLOCK)
                .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSItems.LAWRENCE_MUSIC_DISC)
                .input(ConventionalItemTags.MUSIC_DISCS).input(PSItems.POOP)
                .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.COMPOOPER)
                .pattern("S S")
                .pattern("S S")
                .pattern("SSS")
                .input('S', Blocks.MOSSY_COBBLESTONE_SLAB)
                .criterion(hasItem(Blocks.MOSSY_COBBLESTONE_SLAB), conditionsFromItem(Blocks.MOSSY_COBBLESTONE_SLAB))
                .offerTo(exporter);

        //原版物品配方
        offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CRAFTING_TABLE, PSItems.SPALL);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.COARSE_DIRT, 4)
                .pattern("PG")
                .pattern("GP")
                .input('G', Blocks.GRAVEL)
                .input('P', PSBlocks.POOP_BLOCK)
                .criterion(hasItem( PSBlocks.POOP_BLOCK), conditionsFromItem( PSBlocks.POOP_BLOCK))
                .offerTo(exporter,  getItemPath(Blocks.COARSE_DIRT) + "_from_poop_block");

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.POINTED_DRIPSTONE)
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .input('S', PSItems.SPALL)
                .criterion(hasItem(PSItems.SPALL), conditionsFromItem(PSItems.SPALL))
                .offerTo(exporter,getItemPath(Blocks.POINTED_DRIPSTONE) + "_from_spall");
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.FLINT)
                .pattern("S")
                .pattern("S")
                .input('S', PSItems.SPALL)
                .criterion(hasItem(PSItems.SPALL), conditionsFromItem(PSItems.SPALL))
                .offerTo(exporter,getItemPath(Items.FLINT) + "_from_spall");
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.GRAVEL)
                .pattern("FF")
                .pattern("FF")
                .input('F', Items.FLINT)
                .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
                .offerTo(exporter,getItemPath(Blocks.GRAVEL) + "_from_flint_x4");

        offerCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE, PSItems.SPALL);
        create1x2ShapelessFrom(exporter, Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.MOSS_BLOCK);
        create1x2ShapelessFrom(exporter, Blocks.COBBLESTONE, Blocks.GRAVEL, PSItems.SPALL);
        create1x2ShapelessFrom(exporter, Blocks.ANDESITE, Blocks.GRAVEL, Items.FLINT);
        create1x2ShapelessFrom(exporter, Blocks.DIORITE, Blocks.GRAVEL, Items.CLAY_BALL);
        create1x2ShapelessFrom(exporter, Blocks.GRANITE, Blocks.GRAVEL, Items.BRICK);
        create1x2ShapelessFrom(exporter, Blocks.TUFF, Blocks.ANDESITE, PSItems.SPALL);
        create1x2ShapelessFrom(exporter, Blocks.CALCITE, Blocks.DIORITE, PSItems.SPALL);
        create1x2ShapelessFrom(exporter, Blocks.COBBLED_DEEPSLATE, Blocks.MUD, PSItems.SPALL);

        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.STOOL, PSBlocks.POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PIECE, PSBlocks.POOP_BLOCK, 8);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_STAIRS, PSBlocks.POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_SLAB, PSBlocks.POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_VERTICAL_SLAB, PSBlocks.POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE, PSBlocks.POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE_GATE, PSBlocks.POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_WALL, PSBlocks.POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_DOOR, PSBlocks.POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_TRAPDOOR, PSBlocks.POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PRESSURE_PLATE, PSBlocks.POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BUTTON, PSBlocks.POOP_BLOCK, 4);

        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICKS, PSBlocks.POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_STAIRS, PSBlocks.POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_SLAB, PSBlocks.POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_VERTICAL_SLAB, PSBlocks.POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_WALL, PSBlocks.POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_STAIRS, PSBlocks.POOP_BRICKS);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_SLAB, PSBlocks.POOP_BRICKS, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_VERTICAL_SLAB, PSBlocks.POOP_BRICKS, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BRICK_WALL, PSBlocks.POOP_BRICKS);

        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.MOSSY_POOP_BRICK_STAIRS, PSBlocks.MOSSY_POOP_BRICKS);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.MOSSY_POOP_BRICK_SLAB,  PSBlocks.MOSSY_POOP_BRICKS, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB,  PSBlocks.MOSSY_POOP_BRICKS, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.MOSSY_POOP_BRICK_WALL,  PSBlocks.MOSSY_POOP_BRICKS);

        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK_STAIRS, PSBlocks.DRIED_POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK_SLAB,  PSBlocks.DRIED_POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB,  PSBlocks.DRIED_POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.DRIED_POOP_BLOCK_WALL,  PSBlocks.DRIED_POOP_BLOCK);

        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK, PSBlocks.DRIED_POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_STAIRS,  PSBlocks.DRIED_POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_SLAB,  PSBlocks.DRIED_POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB,  PSBlocks.DRIED_POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_WALL,  PSBlocks.DRIED_POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_STAIRS,  PSBlocks.CUT_POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_SLAB,  PSBlocks.CUT_POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB,  PSBlocks.CUT_POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.CUT_POOP_BLOCK_WALL,  PSBlocks.CUT_POOP_BLOCK);

        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK_STAIRS,  PSBlocks.SMOOTH_POOP_BLOCK);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK_SLAB,  PSBlocks.SMOOTH_POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB,  PSBlocks.SMOOTH_POOP_BLOCK, 2);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.SMOOTH_POOP_BLOCK_WALL,  PSBlocks.SMOOTH_POOP_BLOCK);

        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.STRIPPED_POOP_LOG, PSBlocks.POOP_LOG);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_EMPTY_LOG, PSBlocks.POOP_LOG);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.STRIPPED_POOP_EMPTY_LOG, PSBlocks.STRIPPED_POOP_LOG);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.STRIPPED_POOP_EMPTY_LOG, PSBlocks.POOP_EMPTY_LOG);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, PSBlocks.POOP_EMPTY_LOG, 4);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, PSBlocks.STRIPPED_POOP_EMPTY_LOG, 4);

        toiletRecipes(exporter, ToiletBlocks.OAK_TOILET, Blocks.OAK_PLANKS);
        toiletRecipes(exporter, ToiletBlocks.SPRUCE_TOILET, Blocks.SPRUCE_PLANKS);
        toiletRecipes(exporter, ToiletBlocks.BIRCH_TOILET, Blocks.BIRCH_PLANKS);
        toiletRecipes(exporter, ToiletBlocks.JUNGLE_TOILET, Blocks.JUNGLE_PLANKS);
        toiletRecipes(exporter, ToiletBlocks.ACACIA_TOILET, Blocks.ACACIA_PLANKS);
        toiletRecipes(exporter, ToiletBlocks.DARK_OAK_TOILET, Blocks.DARK_OAK_PLANKS);
        toiletRecipes(exporter, ToiletBlocks.MANGROVE_TOILET, Blocks.MANGROVE_PLANKS);
        toiletRecipes(exporter, ToiletBlocks.CRIMSON_TOILET, Blocks.CRIMSON_PLANKS);
        toiletRecipes(exporter, ToiletBlocks.BAMBOO_TOILET, Blocks.BAMBOO_PLANKS);
        toiletRecipes(exporter, ToiletBlocks.CHERRY_TOILET, Blocks.CHERRY_PLANKS);
        toiletRecipes(exporter, ToiletBlocks.WARPED_TOILET, Blocks.WARPED_PLANKS);

        toiletRecipes(exporter, ToiletBlocks.STONE_TOILET, Blocks.STONE);
        toiletRecipes(exporter, ToiletBlocks.COBBLESTONE_TOILET, Blocks.COBBLESTONE);
        toiletRecipes(exporter, ToiletBlocks.MOSSY_COBBLESTONE_TOILET, Blocks.MOSSY_COBBLESTONE);
        toiletRecipes(exporter, ToiletBlocks.SMOOTH_STONE_TOILET, Blocks.SMOOTH_STONE);
        toiletRecipes(exporter, ToiletBlocks.STONE_BRICK_TOILET, Blocks.STONE_BRICKS);
        toiletRecipes(exporter, ToiletBlocks.MOSSY_STONE_BRICK_TOILET, Blocks.MOSSY_STONE_BRICKS);

        toiletRecipes(exporter, ToiletBlocks.WHITE_CONCRETE_TOILET, Blocks.WHITE_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET, Blocks.LIGHT_GRAY_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.GRAY_CONCRETE_TOILET, Blocks.GRAY_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.BLACK_CONCRETE_TOILET, Blocks.BLACK_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.BROWN_CONCRETE_TOILET, Blocks.BROWN_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.RED_CONCRETE_TOILET, Blocks.RED_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.ORANGE_CONCRETE_TOILET, Blocks.ORANGE_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.YELLOW_CONCRETE_TOILET, Blocks.YELLOW_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.LIME_CONCRETE_TOILET, Blocks.LIME_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.GREEN_CONCRETE_TOILET, Blocks.GREEN_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.CYAN_CONCRETE_TOILET, Blocks.CYAN_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET, Blocks.LIGHT_BLUE_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.BLUE_CONCRETE_TOILET, Blocks.BLUE_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.PURPLE_CONCRETE_TOILET, Blocks.PURPLE_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.MAGENTA_CONCRETE_TOILET, Blocks.MAGENTA_CONCRETE);
        toiletRecipes(exporter, ToiletBlocks.PINK_CONCRETE_TOILET, Blocks.PINK_CONCRETE);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ToiletBlocks.RAINBOW_TOILET)
                .input(ToiletBlocks.RED_CONCRETE_TOILET)
                .input(ToiletBlocks.GREEN_CONCRETE_TOILET)
                .input(ToiletBlocks.BLUE_CONCRETE_TOILET)
                .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                .offerTo(exporter);
    }

    public void createStairsRecipe(RecipeExporter exporter, Block output, Block input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 8)
                .pattern("P  ")
                .pattern("PP ")
                .pattern("PPP")
                .input('P', input)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter);
    }
    private void createSlabRecipe(RecipeExporter exporter, Block output, Block input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 6)
            .pattern("PPP")
            .input('P', input)
            .criterion(hasItem(input), conditionsFromItem(input))
            .offerTo(exporter);
    }
    private void createVerticalSlabRecipe(RecipeExporter exporter, Block output, Block input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 6)
                .pattern("P")
                .pattern("P")
                .pattern("P")
                .input('P', input)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter);
    }
    private void createWallRecipe(RecipeExporter exporter, Block output, Block input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 6)
            .pattern("PPP")
            .pattern("PPP")
            .input('P', input)
            .criterion(hasItem(input), conditionsFromItem(input))
            .offerTo(exporter);
    }
    public static void offer2x2CompactingRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input, int count) {
        ShapedRecipeJsonBuilder.create(category, output, count)
                .input('#', input)
                .pattern("##")
                .pattern("##")
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter);
    }
    private void create1x2ShapelessFrom(RecipeExporter exporter, ItemConvertible output, ItemConvertible input1, ItemConvertible input2) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output)
                .input(input1).input(input2)
                .criterion(hasItem(input2), conditionsFromItem(input2))
                .offerTo(exporter,getItemPath(output) + "_from_" + getItemPath(input2));
    }

    private void toiletRecipes(RecipeExporter exporter, Block toilet, Block block) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, toilet)
                .pattern("#P#")
                .pattern("###")
                .pattern("###")
                .input('P', PSItems.POOP)
                .input('#', block)
                .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                .offerTo(exporter);
    }
}
