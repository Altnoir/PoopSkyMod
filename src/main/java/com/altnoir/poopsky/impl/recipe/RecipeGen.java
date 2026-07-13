package com.altnoir.poopsky.impl.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.PSMods;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.recipe.*;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.ToiletTypes;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeGen extends RegistrateRecipeProvider implements IConditionBuilder {
    public RecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(PoopSky.registrate(), output, provider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        List<ItemLike> POOP_LIST = List.of(PoBlocks.POOP_BLOCK);
        List<ItemLike> POOP_BRICK_LIST = List.of(PoBlocks.POOP_BRICKS);
        List<ItemLike> SMOOTH_POOP_LIST = List.of(PoBlocks.DRIED_POOP_BLOCK);
        List<ItemLike> TILE_BLOCK_LIST = List.of(PoBlocks.POOLIME_BLOCK);
        List<ItemLike> MAGGOTS_LIST = List.of(PoItems.MAGGOTS_SEEDS);
        List<ItemLike> ROUNDWORM_LIST = List.of(PoItems.ROUNDWORM);

        shapeless1x1Recipe(recipeOutput, Blocks.CRIMSON_NYLIUM, Blocks.CRIMSON_FUNGUS, Blocks.NETHERRACK);
        shapeless1x1Recipe(recipeOutput, Blocks.WARPED_NYLIUM, Blocks.WARPED_FUNGUS, Blocks.NETHERRACK);
        shapeless1x1Recipe(recipeOutput, Blocks.SLIME_BLOCK, Items.LIME_DYE, PoBlocks.POOLIME_BLOCK);

        oreSmelting(recipeOutput, POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_POOP_BLOCK, 0.1F, 200, "dried_poop_block");
        oreBlasting(recipeOutput, POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_POOP_BLOCK, 0.1F, 100, "dried_poop_block");

        oreSmelting(recipeOutput, POOP_BRICK_LIST, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CRACKED_POOP_BRICKS, 0.1F, 200, "cracked_poop_bricks");
        oreBlasting(recipeOutput, POOP_BRICK_LIST, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CRACKED_POOP_BRICKS, 0.1F, 100, "cracked_poop_bricks");

        oreSmelting(recipeOutput, SMOOTH_POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PoBlocks.SMOOTH_POOP_BLOCK, 0.1F, 200, "smooth_poop_block");
        oreBlasting(recipeOutput, SMOOTH_POOP_LIST, RecipeCategory.BUILDING_BLOCKS, PoBlocks.SMOOTH_POOP_BLOCK, 0.1F, 100, "smooth_poop_block");

        oreSmelting(recipeOutput, TILE_BLOCK_LIST, RecipeCategory.BUILDING_BLOCKS, PoBlocks.TILE_BLOCK, 0.1F, 200, "tile_block");
        oreBlasting(recipeOutput, TILE_BLOCK_LIST, RecipeCategory.BUILDING_BLOCKS, PoBlocks.TILE_BLOCK, 0.1F, 100, "tile_block");

        oreCooking(recipeOutput, List.of(PoItems.POOP.get()), RecipeCategory.MISC, Items.COCOA_BEANS, 0.35F, 600, "cocoa_beans");
        oreSmelting(recipeOutput, ROUNDWORM_LIST, RecipeCategory.MISC, Items.STRING, 0.35F, 200, "roundworm");
        oreCooking(recipeOutput, ROUNDWORM_LIST, RecipeCategory.MISC, Items.STRING, 0.35F, 200, "roundworm");
        // 食物
        oreSmelting(recipeOutput, MAGGOTS_LIST, RecipeCategory.BUILDING_BLOCKS, PoItems.BAKED_MAGGOTS, 0.35F, 200, "maggots_seeds");
        oreCooking(recipeOutput, RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, MAGGOTS_LIST, RecipeCategory.BUILDING_BLOCKS, PoItems.BAKED_MAGGOTS, 0.35F, 100, "maggots_seeds", "_from_smoking");
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PoItems.POOP_BREAD)
                .pattern("PMP")
                .define('P', PoItems.POOP)
                .define('M', PoItems.MAGGOTS_SEEDS)
                .unlockedBy(getItemName(PoItems.MAGGOTS_SEEDS), has(PoItems.MAGGOTS_SEEDS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PoItems.POOP_MOONCAKE, 2)
                .pattern("WPW")
                .define('W', Items.WHEAT)
                .define('P', PoItems.POOP)
                .unlockedBy(getItemName(Items.WHEAT), has(Items.WHEAT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PoItems.CHILI_POOP_MOONCAKE, 2)
                .pattern("WPW")
                .define('W', Items.WHEAT)
                .define('P', PoItems.CHILI_POOP)
                .unlockedBy(getItemName(Items.WHEAT), has(Items.WHEAT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PoItems.GOLDEN_POOP_MOONCAKE, 2)
                .pattern("WPW")
                .define('W', Items.WHEAT)
                .define('P', PoItems.GOLDEN_POOP)
                .unlockedBy(getItemName(Items.WHEAT), has(Items.WHEAT))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PoItems.POOP_DUMPLINGS)
                .requires(PoItems.POOP_BALL.get())
                .requires(ItemTags.LEAVES)
                .unlockedBy(getItemName(PoItems.POOP_BALL.get()), has(PoItems.POOP_BALL.get()))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PoItems.POOP_SOUP)
                .requires(Items.BOWL)
                .requires(PoItems.POOP)
                .requires(PoItems.MAGGOTS_SEEDS)
                .requires(PoItems.URINE_BOTTLE)
                .unlockedBy(getItemName(PoItems.MAGGOTS_SEEDS), has(PoItems.MAGGOTS_SEEDS))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PoItems.POOBURGER_MEAT.get(), 3)
                .requires(PoItems.POOP, 3)
                .requires(Items.EGG)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PoItems.POOBURGER.get())
                .requires(Items.BREAD)
                .requires(PoItems.POOBURGER_MEAT)
                .requires(PoItems.SEEDBED_CURSE)
                .unlockedBy(getItemName(PoItems.SEEDBED_CURSE), has(PoItems.SEEDBED_CURSE))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PoItems.POODDING.get(), 2)
                .requires(PoItems.POOP_BALL)
                .requires(Items.EGG).requires(Items.SUGAR)
                .unlockedBy(getItemName(PoItems.POOP_BALL), has(PoItems.POOP_BALL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PoBlocks.POOP_CAKE.get())
                .pattern("MMM")
                .pattern("SES")
                .pattern("PPP")
                .define('M', PoItems.MAGGOTS_SEEDS)
                .define('S', Items.SUGAR).define('E', Items.EGG)
                .define('P', PoItems.POOP)
                .unlockedBy(getItemName(PoItems.MAGGOTS_SEEDS), has(PoItems.MAGGOTS_SEEDS))
                .save(recipeOutput);

        // 杂项
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PoItems.TOILET_PLUG_WAND)
                .requires(PoItems.TOILET_PLUG.get())
                .requires(PoItems.POOP.get())
                .requires(Items.ENDER_EYE)
                .unlockedBy(getItemName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PoItems.TIME_BELL)
                .requires(Items.BELL)
                .requires(PoItems.POOP.get())
                .requires(Items.DRAGON_EGG)
                .unlockedBy(getItemName(Items.DRAGON_EGG), has(Items.DRAGON_EGG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.COBWEB)
                .pattern("S S")
                .pattern(" P ")
                .pattern("S S")
                .define('P', PoItems.POOP_BALL)
                .define('S', PoItems.MAGGOTS_SEEDS)
                .unlockedBy(getItemName(PoItems.MAGGOTS_SEEDS), has(PoItems.MAGGOTS_SEEDS))
                .save(recipeOutput, getConversionRecipeName(PoItems.MAGGOTS_SEEDS));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoItems.WITHER_POOP_BALL.get(), 8)
                .pattern("PPP")
                .pattern("PSP")
                .pattern("PPP")
                .define('P', PoItems.POOP_BALL)
                .define('S', Items.WITHER_ROSE)
                .unlockedBy(getItemName(Items.WITHER_ROSE), has(Items.WITHER_ROSE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoItems.GOLDEN_POOP.get())
                .pattern("PPP")
                .pattern("PSP")
                .pattern("PPP")
                .define('P', Items.GOLD_NUGGET)
                .define('S', PoItems.POOP)
                .unlockedBy(getItemName(Items.GOLD_NUGGET), has(Items.GOLD_NUGGET))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PoItems.SEEDBED_CURSE.get())
                .requires(Items.ROTTEN_FLESH, 4)
                .requires(PoItems.POOP_BALL)
                .requires(PoItems.POOP, 4)
                .unlockedBy(getItemName(Items.ROTTEN_FLESH), has(Items.ROTTEN_FLESH))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PoItems.OMINOUS_FILTHY_INGOT.get())
                .requires(PoItems.SEEDBED_CURSE, 4)
                .requires(Items.IRON_INGOT, 4)
                .unlockedBy(getItemName(PoItems.SEEDBED_CURSE), has(PoItems.SEEDBED_CURSE))
                .save(recipeOutput);
        copySmithingTemplate(recipeOutput, PoItems.OMEN_UPGRADE_SMITHING_TEMPLATE, PoBlocks.POOP_BLOCK, PoItems.SEEDBED_CURSE);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, PoItems.MILOS_SWORD)
                .pattern("BOB")
                .pattern(" O ")
                .pattern(" P ")
                .define('B', Items.BONE)
                .define('O', PoItems.OMINOUS_FILTHY_INGOT)
                .define('P', PoItems.TOILET_PLUG)
                .unlockedBy(getItemName(PoItems.OMINOUS_FILTHY_INGOT), has(PoItems.OMINOUS_FILTHY_INGOT))
                .save(recipeOutput);
        spallToolRecipes(recipeOutput);
        // 盔甲
        omenSmithing(recipeOutput, Items.GOLDEN_CHESTPLATE, RecipeCategory.COMBAT, PoItems.OMEN_CHESTPLATE.get());
        omenSmithing(recipeOutput, Items.GOLDEN_LEGGINGS, RecipeCategory.COMBAT, PoItems.OMEN_LEGGINGS.get());
        omenSmithing(recipeOutput, Items.GOLDEN_HELMET, RecipeCategory.COMBAT, PoItems.OMEN_HELMET.get());
        omenSmithing(recipeOutput, Items.GOLDEN_BOOTS, RecipeCategory.COMBAT, PoItems.OMEN_BOOTS.get());

        // 建筑
        offer2x2CompactingRecipe(recipeOutput, PoBlocks.POOP_BLOCK, PoItems.POOP);
        blockFamilyRecipes(recipeOutput, PoBlocks.POOP_FAMILY);

        offer2x2CompactingRecipe(recipeOutput, PoBlocks.CHILI_POOP_BLOCK, PoItems.CHILI_POOP);
        blockFamilyRecipes(recipeOutput, PoBlocks.CHILI_POOP_FAMILY);

        offer2x2CompactingRecipe(recipeOutput, PoBlocks.GOLDEN_POOP_BLOCK, PoItems.GOLDEN_POOP);
        blockFamilyRecipes(recipeOutput, PoBlocks.GOLDEN_POOP_FAMILY);

        blockFamilyRecipes(recipeOutput, PoBlocks.DRIED_POOP_BLOCK_FAMILY);

        blockFamilyRecipes(recipeOutput, PoBlocks.SMOOTH_POOP_BLOCK_FAMILY);

        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK, PoBlocks.DRIED_POOP_BLOCK, 4);
        blockFamilyRecipes(recipeOutput, PoBlocks.CUT_POOP_BLOCK_FAMILY);

        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICKS, PoBlocks.POOP_BLOCK, 4);
        blockFamilyRecipes(recipeOutput, PoBlocks.POOP_BRICK_FAMILY);

        create1x2ShapelessFrom(recipeOutput, PoBlocks.MOSSY_POOP_BRICKS, PoBlocks.POOP_BRICKS, Blocks.MOSS_BLOCK);
        create1x2ShapelessFrom(recipeOutput, PoBlocks.MOSSY_POOP_BRICKS, PoBlocks.POOP_BRICKS, Blocks.VINE);
        blockFamilyRecipes(recipeOutput, PoBlocks.MOSSY_POOP_BRICK_FAMILY);

        blockFamilyRecipes(recipeOutput, PoBlocks.TILE_BLOCK_FAMILY);

        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PoBlocks.RAW_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.SAPLING_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PoBlocks.RAW_SAPLING_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.SEA_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PoBlocks.RAW_SEA_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.WITHER_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PoBlocks.RAW_WITHER_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.MAGGOTS_SEEDS, RecipeCategory.BUILDING_BLOCKS, PoBlocks.MAGGOTS_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.ROUNDWORM, RecipeCategory.BUILDING_BLOCKS, PoBlocks.ROUNDWORM_BLOCK);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BLOCK)
                .pattern("P")
                .pattern("P")
                .define('P', PoBlocks.POOP_SLAB)
                .unlockedBy(getItemName(PoBlocks.POOP_BLOCK), has(PoBlocks.POOP_BLOCK))
                .save(recipeOutput, getConversionRecipeName(PoBlocks.POOP_BLOCK) + "_from_slab");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BLOCK)
                .pattern("PP")
                .define('P', PoBlocks.POOP_VERTICAL_SLAB)
                .unlockedBy(getItemName(PoBlocks.POOP_BLOCK), has(PoBlocks.POOP_BLOCK))
                .save(recipeOutput, getConversionRecipeName(PoBlocks.POOP_BLOCK) + "_from_vertical_slab");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BUTTON)
                .requires(PoItems.POOP.get())
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_PRESSURE_PLATE)
                .pattern("PP")
                .define('P', PoItems.POOP)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_FENCE, 3)
                .pattern("BPB")
                .pattern("BPB")
                .define('B', PoBlocks.POOP_BLOCK)
                .define('P', PoItems.POOP)
                .unlockedBy(getItemName(PoBlocks.POOP_BLOCK), has(PoBlocks.POOP_BLOCK))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_FENCE_GATE)
                .pattern("PBP")
                .pattern("PBP")
                .define('B', PoBlocks.POOP_BLOCK)
                .define('P', PoItems.POOP)
                .unlockedBy(getItemName(PoBlocks.POOP_BLOCK), has(PoBlocks.POOP_BLOCK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_DOOR, 3)
                .pattern("PP")
                .pattern("PP")
                .pattern("PP")
                .define('P', PoBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PoBlocks.POOP_BLOCK), has(PoBlocks.POOP_BLOCK))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_TRAPDOOR, 2)
                .pattern("PP")
                .pattern("PP")
                .define('P', PoBlocks.POOP_SLAB)
                .unlockedBy(getItemName(PoBlocks.POOP_BLOCK), has(PoBlocks.POOP_BLOCK))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BLOCK, 4)
                .requires(PoBlocks.POOP_EMPTY_LOG)
                .unlockedBy(getItemName(PoBlocks.POOP_LOG), has(PoBlocks.POOP_LOG))
                .save(recipeOutput, getConversionRecipeName(PoBlocks.POOP_BLOCK, PoBlocks.POOP_EMPTY_LOG));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BLOCK, 4)
                .requires(PoBlocks.STRIPPED_POOP_EMPTY_LOG)
                .unlockedBy(getItemName(PoBlocks.POOP_LOG), has(PoBlocks.POOP_LOG))
                .save(recipeOutput, getConversionRecipeName(PoBlocks.POOP_BLOCK, PoBlocks.STRIPPED_POOP_EMPTY_LOG));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_PIECE, 3)
                .pattern("PP")
                .define('P', PoBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PoBlocks.POOP_BLOCK), has(PoBlocks.POOP_BLOCK))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PoItems.LAWRENCE_MUSIC_DISC)
                .requires(Tags.Items.MUSIC_DISCS)
                .requires(PoItems.POOP)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.COMPOOPER)
                .pattern("S S")
                .pattern("S S")
                .pattern("SSS")
                .define('S', Blocks.MOSSY_COBBLESTONE_SLAB)
                .unlockedBy(getItemName(Blocks.MOSSY_COBBLESTONE_SLAB), has(Blocks.MOSSY_COBBLESTONE_SLAB))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PoBlocks.PLACER)
                .pattern("SSS")
                .pattern("SPS")
                .pattern("SAS")
                .define('S', Blocks.MOSSY_COBBLESTONE)
                .define('A', Items.REDSTONE)
                .define('P', PoItems.TOILET_PLUG)
                .unlockedBy(getItemName(PoItems.TOILET_PLUG), has(PoItems.TOILET_PLUG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PoBlocks.SIEVE)
                .pattern("SAS")
                .pattern("S S")
                .pattern("S S")
                .define('S', Blocks.MOSSY_COBBLESTONE_WALL)
                .define('A', Items.STRING)
                .unlockedBy(getItemName(Blocks.MOSSY_COBBLESTONE_WALL), has(Blocks.MOSSY_COBBLESTONE_WALL))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PoBlocks.POOP_TNT)
                .pattern("SAS")
                .pattern("ASA")
                .pattern("SAS")
                .define('S', Tags.Items.GUNPOWDERS)
                .define('A', PoBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PoItems.KING_OF_DRAGON_FRUIT), has(PoItems.KING_OF_DRAGON_FRUIT))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PoItems.FLY_CATCHER)
                .pattern("RR")
                .pattern("RS")
                .pattern(" S")
                .define('R', PoItems.ROUNDWORM)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PoItems.ROUNDWORM), has(PoItems.ROUNDWORM))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, PoBlocks.FLY_BARREL)
                .requires(Items.BARREL)
                .requires(PoBlocks.MAGGOTS_BLOCK)
                .unlockedBy(getItemName(PoBlocks.MAGGOTS_BLOCK), has(PoBlocks.MAGGOTS_BLOCK))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PoBlocks.BREEDING_CHEST)
                .pattern("CCC")
                .pattern("CPC")
                .pattern("CBC")
                .define('C', PoBlocks.CUT_POOP_BLOCK)
                .define('P', PoItems.POOP)
                .define('B', Tags.Items.BARRELS)
                .unlockedBy(getItemName(PoBlocks.CUT_POOP_BLOCK), has(PoBlocks.CUT_POOP_BLOCK))
                .save(recipeOutput);

        offer2x2CompactingRecipe(recipeOutput, PoBlocks.POOLIME_BLOCK.get(), PoItems.POOP_BALL.get());
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOLIME_MAGGOTS_BLOCK.get())
                .pattern("PPP")
                .pattern("PMP")
                .pattern("PPP")
                .define('P', PoBlocks.POOP_BLOCK)
                .define('M', PoBlocks.MAGGOTS_BLOCK)
                .unlockedBy(getItemName(PoBlocks.MAGGOTS_BLOCK), has(PoBlocks.MAGGOTS_BLOCK))
                .save(recipeOutput);

        // 原版物品配方
        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, Blocks.CRAFTING_TABLE, PoItems.SPALL, 1);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER)
                .requires(PoItems.KING_OF_DRAGON_FRUIT)
                .unlockedBy(getItemName(PoItems.KING_OF_DRAGON_FRUIT), has(PoItems.KING_OF_DRAGON_FRUIT))
                .save(recipeOutput, getConversionRecipeName(Items.GUNPOWDER) + "_from_" + getItemName(PoItems.KING_OF_DRAGON_FRUIT));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.COARSE_DIRT, 4)
                .pattern("PG")
                .pattern("GP")
                .define('G', Blocks.GRAVEL)
                .define('P', PoBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PoBlocks.POOP_BLOCK), has(PoBlocks.POOP_BLOCK))
                .save(recipeOutput, getConversionRecipeName(Blocks.COARSE_DIRT) + "_from_poop_block");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.POINTED_DRIPSTONE)
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .define('S', PoItems.SPALL)
                .unlockedBy(getItemName(PoItems.SPALL), has(PoItems.SPALL))
                .save(recipeOutput, getConversionRecipeName(Blocks.POINTED_DRIPSTONE) + "_from_spall");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.FLINT)
                .pattern("S")
                .pattern("S")
                .define('S', PoItems.SPALL)
                .unlockedBy(getItemName(PoItems.SPALL), has(PoItems.SPALL))
                .save(recipeOutput, getConversionRecipeName(Items.FLINT) + "_from_spall");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH, 4)
                .pattern("P")
                .pattern("S")
                .define('P', PoItems.POOP_BALL)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PoItems.POOP_BALL), has(PoItems.POOP_BALL))
                .save(recipeOutput, getConversionRecipeName(Items.TORCH) + "_from_poop_ball");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.TUBE_CORAL_BLOCK)
                .requires(PoBlocks.POOP_BLOCK)
                .requires(Blocks.TUBE_CORAL).requires(Blocks.TUBE_CORAL_FAN)
                .unlockedBy(getItemName(Blocks.TUBE_CORAL), has(Blocks.TUBE_CORAL))
                .save(recipeOutput, getConversionRecipeName(Blocks.TUBE_CORAL_BLOCK));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.BRAIN_CORAL_BLOCK)
                .requires(PoBlocks.POOP_BLOCK)
                .requires(Blocks.BRAIN_CORAL).requires(Blocks.BRAIN_CORAL_FAN)
                .unlockedBy(getItemName(Blocks.BRAIN_CORAL), has(Blocks.BRAIN_CORAL))
                .save(recipeOutput, getConversionRecipeName(Blocks.BRAIN_CORAL_BLOCK));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.BUBBLE_CORAL_BLOCK)
                .requires(PoBlocks.POOP_BLOCK)
                .requires(Blocks.BUBBLE_CORAL).requires(Blocks.BUBBLE_CORAL_FAN)
                .unlockedBy(getItemName(Blocks.BUBBLE_CORAL), has(Blocks.BUBBLE_CORAL))
                .save(recipeOutput, getConversionRecipeName(Blocks.BUBBLE_CORAL_BLOCK));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.FIRE_CORAL_BLOCK)
                .requires(PoBlocks.POOP_BLOCK)
                .requires(Blocks.FIRE_CORAL).requires(Blocks.FIRE_CORAL_FAN)
                .unlockedBy(getItemName(Blocks.FIRE_CORAL), has(Blocks.FIRE_CORAL))
                .save(recipeOutput, getConversionRecipeName(Blocks.FIRE_CORAL_BLOCK));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.HORN_CORAL_BLOCK)
                .requires(PoBlocks.POOP_BLOCK)
                .requires(Blocks.HORN_CORAL).requires(Blocks.HORN_CORAL_FAN)
                .unlockedBy(getItemName(Blocks.HORN_CORAL), has(Blocks.HORN_CORAL))
                .save(recipeOutput, getConversionRecipeName(Blocks.HORN_CORAL_BLOCK));

        offerCompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE, PoItems.SPALL);
        create1x2ShapelessFrom(recipeOutput, Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.MOSS_BLOCK);

        create1x2ShapelessFrom(recipeOutput, Blocks.DIORITE, Blocks.COBBLESTONE, Blocks.CLAY, 2);
        create1x2ShapelessFrom(recipeOutput, Blocks.GRANITE, Blocks.COBBLESTONE, Blocks.DRIPSTONE_BLOCK, 2);
        create1x2ShapelessFrom(recipeOutput, Blocks.DIRT, Blocks.MUD, PoItems.POOP.get());
        //create1x2ShapelessFrom(recipeOutput, Blocks.TUFF, Blocks.ANDESITE, PSItems.SPALL);
        //create1x2ShapelessFrom(recipeOutput, Blocks.CALCITE, Blocks.DIORITE, PSItems.SPALL);

        // 切石配方
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.STOOL, PoBlocks.DRIED_POOP_BLOCK, 2);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_PIECE, PoBlocks.POOP_BLOCK, 8);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_STAIRS, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_SLAB, PoBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_VERTICAL_SLAB, PoBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_FENCE, PoBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_FENCE_GATE, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_WALL, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_DOOR, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_TRAPDOOR, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_PRESSURE_PLATE, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BUTTON, PoBlocks.POOP_BLOCK, 4);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CHILI_POOP_STAIRS, PoBlocks.CHILI_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CHILI_POOP_SLAB, PoBlocks.CHILI_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CHILI_POOP_VERTICAL_SLAB, PoBlocks.CHILI_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CHILI_POOP_WALL, PoBlocks.CHILI_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.GOLDEN_POOP_STAIRS, PoBlocks.GOLDEN_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.GOLDEN_POOP_SLAB, PoBlocks.GOLDEN_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.GOLDEN_POOP_VERTICAL_SLAB, PoBlocks.GOLDEN_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.GOLDEN_POOP_WALL, PoBlocks.GOLDEN_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICKS, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_STAIRS, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_SLAB, PoBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_VERTICAL_SLAB, PoBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_WALL, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_STAIRS, PoBlocks.POOP_BRICKS);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_SLAB, PoBlocks.POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_VERTICAL_SLAB, PoBlocks.POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_WALL, PoBlocks.POOP_BRICKS);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.MOSSY_POOP_BRICK_STAIRS, PoBlocks.MOSSY_POOP_BRICKS);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.MOSSY_POOP_BRICK_SLAB, PoBlocks.MOSSY_POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB, PoBlocks.MOSSY_POOP_BRICKS, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.MOSSY_POOP_BRICK_WALL, PoBlocks.MOSSY_POOP_BRICKS);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_POOP_BLOCK_STAIRS, PoBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_POOP_BLOCK_SLAB, PoBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB, PoBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_POOP_BLOCK_WALL, PoBlocks.DRIED_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK, PoBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_STAIRS, PoBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_SLAB, PoBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PoBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_WALL, PoBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_STAIRS, PoBlocks.CUT_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_SLAB, PoBlocks.CUT_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PoBlocks.CUT_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_WALL, PoBlocks.CUT_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.SMOOTH_POOP_BLOCK_STAIRS, PoBlocks.SMOOTH_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.SMOOTH_POOP_BLOCK_SLAB, PoBlocks.SMOOTH_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB, PoBlocks.SMOOTH_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.SMOOTH_POOP_BLOCK_WALL, PoBlocks.SMOOTH_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.STRIPPED_POOP_LOG, PoBlocks.POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_EMPTY_LOG, PoBlocks.POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.STRIPPED_POOP_EMPTY_LOG, PoBlocks.STRIPPED_POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.STRIPPED_POOP_EMPTY_LOG, PoBlocks.POOP_EMPTY_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BLOCK, PoBlocks.POOP_EMPTY_LOG, 4);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BLOCK, PoBlocks.STRIPPED_POOP_EMPTY_LOG, 4);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.TILE_BLOCK_STAIRS, PoBlocks.TILE_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.TILE_BLOCK_SLAB, PoBlocks.TILE_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.TILE_BLOCK_VERTICAL_SLAB, PoBlocks.TILE_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.TILE_BLOCK_WALL, PoBlocks.TILE_BLOCK);

        // 厕所配方
        for (var entry : ToiletType.getByCategory(ToiletType.Category.WOOD).entrySet()) {
            toiletRecipes(recipeOutput, PoBlocks.WOODEN_TOILET, entry.getValue().sourceBlock(), entry.getValue());
        }
        for (var entry : ToiletType.getByCategory(ToiletType.Category.HARD).entrySet()) {
            if (entry.getValue().sourceBlock() != null) {
                toiletRecipes(recipeOutput, PoBlocks.HARD_TOILET, entry.getValue().sourceBlock(), entry.getValue());
            }
        }
        ToiletRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.HARD_TOILET, ToiletTypes.RAINBOW)
                .pattern(" P ")
                .pattern("RGB")
                .define('P', PoItems.POOP.get())
                .define('R', Blocks.RED_CONCRETE)
                .define('G', Blocks.GREEN_CONCRETE)
                .define('B', Blocks.BLUE_CONCRETE)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP.get()))
                .save(recipeOutput, PoopSky.loc("hard_toilet_from_rainbow"));

        buildSieveRecipes(recipeOutput);
        buildpopExplosionRecipes(recipeOutput);
        buildAnalPressingRecipes(recipeOutput);
        buildBreedingChestRecipes(recipeOutput);
        buildFlyBarrelRecipes(recipeOutput);
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

        SieveRecipeBuilder.sieve(PoBlocks.POOP_BLOCK, 200)
                .addOutput(Items.IRON_NUGGET, 8)
                .addOutput(Items.IRON_NUGGET, 8, 0.75F)
                .addOutput(Items.IRON_NUGGET, 8, 0.5F)
                .addOutput(Items.RAW_IRON, 0.5F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(createNotLoaded);

        SieveRecipeBuilder.sieve(PoBlocks.CHILI_POOP_BLOCK, 300)
                .addOutput(Items.QUARTZ, 4)
                .addOutput(Items.QUARTZ, 4, 0.5F)
                .addOutput(Items.NETHER_WART, 2, 0.75F)
                .addOutput(Items.MAGMA_CREAM, 0.5F)
                .addOutput(Items.GHAST_TEAR, 0.1F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(recipeOutput);

        SieveRecipeBuilder.sieve(PoBlocks.GOLDEN_POOP_BLOCK, 300)
                .addOutput(Items.GOLD_NUGGET, 8)
                .addOutput(Items.GOLD_NUGGET, 8, 0.75F)
                .addOutput(Items.RAW_GOLD, 0.5F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(recipeOutput);

        SieveRecipeBuilder.sieve(PoBlocks.RAW_POOP_BLOCK, 100)
                .addOutput(Items.RAW_COPPER)
                .addOutput(Items.REDSTONE, 2, 0.75F)
                .addOutput(Items.REDSTONE, 0.5F)
                .addOutput(Items.AMETHYST_SHARD, 0.25F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(createNotLoaded);

        SieveRecipeBuilder.sieve(PoBlocks.RAW_SAPLING_POOP_BLOCK, 100)
                .addOutput(Items.SUNFLOWER).addOutput(Items.LILAC)
                .addOutput(Items.ROSE_BUSH).addOutput(Items.PEONY)
                .addOutput(Items.SMALL_DRIPLEAF, 0.5F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(recipeOutput);

        SieveRecipeBuilder.sieve(PoBlocks.RAW_SEA_POOP_BLOCK, 100)
                .addOutput(Items.SEAGRASS, 2).addOutput(Items.SEAGRASS, 0.75F)
                .addOutput(Items.LILY_PAD)
                .addOutput(Items.KELP, 0.5F)
                .addOutput(Items.NAUTILUS_SHELL, 0.09F)
                .addOutput(Items.HEART_OF_THE_SEA, 0.01F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(recipeOutput);

        SieveRecipeBuilder.sieve(PoBlocks.RAW_WITHER_POOP_BLOCK, 100)
                .addOutput(Items.COAL)
                .addOutput(Items.NETHERITE_SCRAP, 0.1F)
                .addOutput(Items.WITHER_SKELETON_SKULL, 0.005F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(recipeOutput);
        SieveRecipeBuilder.sieve(Blocks.CACTUS, 100)
                .addOutput(PoItems.KING_OF_DRAGON_FRUIT.get())
                .addOutput(PoItems.KING_OF_DRAGON_FRUIT.get(), 0.5F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(recipeOutput);

        SieveRecipeBuilder.sieve(Items.DIRT, 200)
                .addOutput(Items.WHEAT_SEEDS)
                .addOutput(Items.VINE, 0.75F)
                .addOutput(Items.HANGING_ROOTS, 0.5F)
                .addOutput(Items.GLOW_LICHEN, 0.25F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(recipeOutput);
        SieveRecipeBuilder.sieve(Items.GRAVEL, 200)
                .addOutput(Items.FLINT)
                .addOutput(Items.GUNPOWDER, 0.5F)
                .addOutput(Items.LAPIS_LAZULI, 0.25F)
                .addOutput(Items.SNIFFER_EGG, 0.01F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(recipeOutput);
        SieveRecipeBuilder.sieve(ItemTags.SAND, 200)
                .addOutput(Items.CLAY)
                .addOutput(Items.IRON_NUGGET, 4, 0.5F)
                .addOutput(Items.GOLD_NUGGET, 4, 0.25F)
                .addOutput(Items.TURTLE_EGG, 4, 0.01F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(recipeOutput, "sand");
        SieveRecipeBuilder.sieve(ItemTags.SOUL_FIRE_BASE_BLOCKS, 200)
                .addOutput(Items.BONE, 4, 0.5F)
                .addOutput(Items.NETHER_WART, 0.5F)
                .addOutput(Items.GLOWSTONE_DUST, 0.5F)
                .addOutput(Items.GHAST_TEAR, 0.25F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(recipeOutput, "soul_blocks");

        // Create
        SieveRecipeBuilder.sieve(PoBlocks.POOP_BLOCK, 200)
                .addOutput(Items.IRON_NUGGET, 8)
                .addOutput(AllItems.ZINC_NUGGET, 8)
                .addOutput(Items.IRON_NUGGET, 8, 0.5F)
                .addOutput(AllItems.ZINC_NUGGET, 8, 0.5F)
                .addOutput(Items.RAW_IRON, 0.5F)
                .addOutput(AllItems.RAW_ZINC, 0.5F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(createLoaded, "poop_block_has_create");
        SieveRecipeBuilder.sieve(PoBlocks.RAW_POOP_BLOCK, 100)
                .addOutput(AllItems.COPPER_NUGGET, 8)
                .addOutput(AllItems.COPPER_NUGGET, 8, 0.75F)
                .addOutput(Items.RAW_COPPER, 0.5F)
                .addOutput(Items.REDSTONE, 2, 0.75F)
                .addOutput(Items.REDSTONE, 0.5F)
                .addOutput(Items.AMETHYST_SHARD, 0.25F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(createLoaded, "raw_poop_block_has_create");
    }

    private void buildFlyBarrelRecipes(RecipeOutput recipeOutput) {
        RecipeOutput create = recipeOutput.withConditions(modLoaded(PSMods.CREATE.id()));

        class FlyMap {
            private final LinkedHashMap<FlyType.Type, ItemLike> items = new LinkedHashMap<>();
            private final HashMap<FlyType.Type, RecipeOutput> overrides = new HashMap<>();
            private FlyType.Type lastKey;

            FlyMap put(FlyType.Type type, ItemLike result) {
                items.put(type, result);
                lastKey = type;
                return this;
            }

            void loaded(RecipeOutput output) {
                if (lastKey != null) overrides.put(lastKey, output);
            }

            void saveAll() {
                items.forEach((type, result) -> FlyBarrelRecipeBuilder.flyBarrel(type.id(), result)
                        .unlockedBy(getHasName(PoBlocks.FLY_BARREL.get()), has(PoBlocks.FLY_BARREL.get()))
                        .save(overrides.getOrDefault(type, recipeOutput), type.id()));
            }
        }

        var flyMap = new FlyMap();
        flyMap.put(FlyTypes.NORMAL.get(), PoItems.MAGGOTS_SEEDS);
        flyMap.put(FlyTypes.WHITE.get(), Items.BONE_MEAL);
        flyMap.put(FlyTypes.LIGHT_GRAY.get(), Items.QUARTZ);
        flyMap.put(FlyTypes.GRAY.get(), Items.GRAVEL);
        flyMap.put(FlyTypes.BLACK.get(), Items.WITHER_ROSE);
        flyMap.put(FlyTypes.BROWN.get(), Items.COCOA_BEANS);
        flyMap.put(FlyTypes.RED.get(), Items.REDSTONE);
        flyMap.put(FlyTypes.ORANGE.get(), Items.TORCHFLOWER);
        flyMap.put(FlyTypes.YELLOW.get(), Items.GLOW_BERRIES);
        flyMap.put(FlyTypes.LIME.get(), Items.SEA_PICKLE);
        flyMap.put(FlyTypes.GREEN.get(), Items.CACTUS);
        flyMap.put(FlyTypes.CYAN.get(), Items.PRISMARINE_SHARD);
        flyMap.put(FlyTypes.LIGHT_BLUE.get(), Items.PRISMARINE_CRYSTALS);
        flyMap.put(FlyTypes.BLUE.get(), Items.LAPIS_LAZULI);
        flyMap.put(FlyTypes.PURPLE.get(), Items.AMETHYST_SHARD);
        flyMap.put(FlyTypes.MAGENTA.get(), Items.CHORUS_FRUIT);
        flyMap.put(FlyTypes.PINK.get(), Items.PINK_PETALS);
        // More
        flyMap.put(FlyTypes.IRON.get(), Items.RAW_IRON);
        flyMap.put(FlyTypes.COPPER.get(), Items.RAW_COPPER);
        flyMap.put(FlyTypes.GOLD.get(), Items.RAW_GOLD);
        flyMap.put(FlyTypes.EMERALD.get(), Items.EMERALD);
        flyMap.put(FlyTypes.DIAMOND.get(), Items.DIAMOND);
        flyMap.put(FlyTypes.NETHERITE.get(), Items.NETHERITE_SCRAP);
        flyMap.put(FlyTypes.DRAGON_FRUIT.get(), Items.GUNPOWDER);
        flyMap.put(FlyTypes.GLOWSTONE.get(), Items.GLOWSTONE_DUST);
        flyMap.put(FlyTypes.ENDER.get(), Items.ENDER_PEARL);
        // Create
        flyMap.put(FlyTypes.ZINC.get(), AllItems.RAW_ZINC).loaded(create);

        flyMap.saveAll();
    }

    private void buildBreedingChestRecipes(RecipeOutput recipeOutput) {
        RecipeOutput create = recipeOutput.withConditions(modLoaded(PSMods.CREATE.id()));

        record Breeding(String p1, String p2, String result, RecipeOutput output) {
            static Breeding of(FlyType.Type p1, FlyType.Type p2, FlyType.Type result) {
                return new Breeding(p1.id(), p2.id(), result.id(), null);
            }
            Breeding loaded(RecipeOutput output) {
                return new Breeding(p1, p2, result, output);
            }
        }

        List<Breeding> breedingRecipes = List.of(
                Breeding.of(FlyTypes.BLACK.get(), FlyTypes.YELLOW.get(), FlyTypes.BROWN.get()),
                Breeding.of(FlyTypes.GREEN.get(), FlyTypes.BLUE.get(), FlyTypes.CYAN.get()),
                Breeding.of(FlyTypes.PURPLE.get(), FlyTypes.PINK.get(), FlyTypes.MAGENTA.get()),
                Breeding.of(FlyTypes.RED.get(), FlyTypes.BLUE.get(), FlyTypes.PURPLE.get()),
                Breeding.of(FlyTypes.RED.get(), FlyTypes.GREEN.get(), FlyTypes.YELLOW.get()),
                Breeding.of(FlyTypes.RED.get(), FlyTypes.YELLOW.get(), FlyTypes.ORANGE.get()),
                Breeding.of(FlyTypes.WHITE.get(), FlyTypes.BLACK.get(), FlyTypes.GRAY.get()),
                Breeding.of(FlyTypes.WHITE.get(), FlyTypes.BLUE.get(), FlyTypes.LIGHT_BLUE.get()),
                Breeding.of(FlyTypes.WHITE.get(), FlyTypes.GRAY.get(), FlyTypes.LIGHT_GRAY.get()),
                Breeding.of(FlyTypes.WHITE.get(), FlyTypes.GREEN.get(), FlyTypes.LIME.get()),
                Breeding.of(FlyTypes.WHITE.get(), FlyTypes.RED.get(), FlyTypes.PINK.get()),
                // More
                Breeding.of(FlyTypes.PURPLE.get(), FlyTypes.CYAN.get(), FlyTypes.ENDER.get()),
                Breeding.of(FlyTypes.GREEN.get(), FlyTypes.YELLOW.get(), FlyTypes.DRAGON_FRUIT.get()),
                Breeding.of(FlyTypes.DRAGON_FRUIT.get(), FlyTypes.YELLOW.get(), FlyTypes.GLOWSTONE.get()),
                Breeding.of(FlyTypes.WHITE.get(), FlyTypes.ORANGE.get(), FlyTypes.COPPER.get()),
                Breeding.of(FlyTypes.LIGHT_GRAY.get(), FlyTypes.COPPER.get(), FlyTypes.IRON.get()),
                Breeding.of(FlyTypes.GLOWSTONE.get(), FlyTypes.COPPER.get(), FlyTypes.GOLD.get()),
                Breeding.of(FlyTypes.LIME.get(), FlyTypes.GOLD.get(), FlyTypes.EMERALD.get()),
                Breeding.of(FlyTypes.GOLD.get(), FlyTypes.EMERALD.get(), FlyTypes.DIAMOND.get()),
                Breeding.of(FlyTypes.EMERALD.get(), FlyTypes.DIAMOND.get(), FlyTypes.NETHERITE.get()),
                Breeding.of(FlyTypes.NORMAL.get(), FlyTypes.COPPER.get(), FlyTypes.BLACK.get()),
                // Create
                Breeding.of(FlyTypes.CYAN.get(), FlyTypes.IRON.get(), FlyTypes.ZINC.get()).loaded(create)
        );

        for (Breeding recipe : breedingRecipes) {
            String id = recipe.p1 + "_plus_" + recipe.p2;
            var builder = BreedingChestRecipeBuilder.breedingChest(recipe.p1, recipe.p2, recipe.result);
            builder.unlockedBy(getHasName(PoBlocks.FLY_BARREL), has(PoBlocks.FLY_BARREL))
                    .save(recipe.output != null ? recipe.output : recipeOutput, id);
        }
    }

    private void toiletRecipes(RecipeOutput recipeOutput, ItemLike toilet, ItemLike block, ToiletType toiletType) {
        ToiletRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, toilet, toiletType)
                .pattern("P")
                .pattern("#")
                .define('P', PoItems.POOP.get())
                .define('#', block)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP.get()))
                .save(recipeOutput, PoopSky.loc(getItemName(toilet) + "_from_" + toiletType.id()));
    }

    protected static void spallToolRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PoItems.SPALL_SWORD)
                .pattern("M")
                .pattern("M")
                .pattern("S")
                .define('M', PoItems.SPALL)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PoItems.SPALL), has(PoItems.SPALL))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PoItems.SPALL_SHOVEL)
                .pattern("M")
                .pattern("S")
                .pattern("S")
                .define('M', PoItems.SPALL)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PoItems.SPALL), has(PoItems.SPALL))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PoItems.SPALL_PICKAXE)
                .pattern("MMM")
                .pattern(" S ")
                .pattern(" S ")
                .define('M', PoItems.SPALL)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PoItems.SPALL), has(PoItems.SPALL))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PoItems.SPALL_AXE)
                .pattern("MM")
                .pattern("MS")
                .pattern(" S")
                .define('M', PoItems.SPALL)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PoItems.SPALL), has(PoItems.SPALL))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PoItems.SPALL_HOE)
                .pattern("MM")
                .pattern(" S")
                .pattern(" S")
                .define('M', PoItems.SPALL)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PoItems.SPALL), has(PoItems.SPALL))
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

    private void blockFamilyRecipes(RecipeOutput recipeOutput, PoBlocks.BlockFamily family) {
        stairsRecipe(recipeOutput, family.stairs(), family.block());
        slabRecipe(recipeOutput, family.slab(), family.block());
        verticalSlabRecipe(recipeOutput, family.verticalSlab(), family.block());
        wallRecipe(recipeOutput, family.wall(), family.block());
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
                        Ingredient.of(PoItems.OMEN_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), Ingredient.of(PoItems.OMINOUS_FILTHY_INGOT), category, resultItem
                )
                .unlocks("has_ominous_filthy_ingot", has(PoItems.OMINOUS_FILTHY_INGOT))
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

    public static String getConversionRecipeName(ItemLike result, ItemLike input) {
        return PoopSky.MOD_ID + ":" + getItemName(result) + "_from_" + getItemName(input);
    }

    public static String getModConversionRecipeName(ItemLike result, ItemLike input) {
        return getItemName(input) + "_to_" + getItemName(result);
    }
}