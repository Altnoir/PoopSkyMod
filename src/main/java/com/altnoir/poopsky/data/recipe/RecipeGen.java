package com.altnoir.poopsky.data.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.compat.farmersdelight.FarmersDelightRecipeGen;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.CompooperType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.*;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.ToiletTypes;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeGen extends RegistrateRecipeProvider implements IConditionBuilder {
    private final CompletableFuture<HolderLookup.Provider> registriesFuture;
    private HolderLookup.Provider registries;

    public RecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(PoopSky.registrate(), output, provider);
        this.registriesFuture = provider;
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        this.registries = this.registriesFuture.join();
        buildCookingRecipes(recipeOutput);
        buildFoodRecipes(recipeOutput);
        buildItemRecipes(recipeOutput);
        buildBuildingRecipes(recipeOutput);
        buildVanillaRecipes(recipeOutput);
        buildStonecuttingRecipes(recipeOutput);
        buildToiletRecipes(recipeOutput);

        buildSieveRecipes(recipeOutput);
        buildCompooperRecipes(recipeOutput);
        buildPopExplosionRecipes(recipeOutput);
        buildAnalPressingRecipes(recipeOutput);
        buildFlyBarrelRecipes(recipeOutput);
        buildBreedingChestRecipes(recipeOutput);

        RecipeOutput fd = recipeOutput.withConditions(modLoaded(PoMods.FARMERSDELIGHT.id()));
        FarmersDelightRecipeGen.buildRecipes(fd);
    }

    private void buildCookingRecipes(RecipeOutput recipeOutput) {
        shapeless1x1Recipe(recipeOutput, Blocks.CRIMSON_NYLIUM, Blocks.CRIMSON_FUNGUS, Blocks.NETHERRACK);
        shapeless1x1Recipe(recipeOutput, Blocks.WARPED_NYLIUM, Blocks.WARPED_FUNGUS, Blocks.NETHERRACK);
        shapeless1x1Recipe(recipeOutput, Blocks.SLIME_BLOCK, Items.LIME_DYE, PoBlocks.POOLIME_BLOCK);

        smelting(recipeOutput, PoBlocks.POOP_BLOCK, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_POOP_BLOCK, 0.1F, 200, "dried_poop_block");
        blasting(recipeOutput, PoBlocks.POOP_BLOCK, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_POOP_BLOCK, 0.1F, 100, "dried_poop_block");
        smelting(recipeOutput, PoBlocks.POOP_BRICKS, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CRACKED_POOP_BRICKS, 0.1F, 200, "cracked_poop_bricks");
        blasting(recipeOutput, PoBlocks.POOP_BRICKS, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CRACKED_POOP_BRICKS, 0.1F, 100, "cracked_poop_bricks");
        smelting(recipeOutput, PoBlocks.DRIED_POOP_BLOCK, RecipeCategory.BUILDING_BLOCKS, PoBlocks.SMOOTH_POOP_BLOCK, 0.1F, 200, "smooth_poop_block");
        blasting(recipeOutput, PoBlocks.DRIED_POOP_BLOCK, RecipeCategory.BUILDING_BLOCKS, PoBlocks.SMOOTH_POOP_BLOCK, 0.1F, 100, "smooth_poop_block");
        smelting(recipeOutput, PoBlocks.CHILI_POOP_BLOCK, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_CHILI_POOP_BLOCK, 0.1F, 200, "dried_chili_poop_block");
        blasting(recipeOutput, PoBlocks.CHILI_POOP_BLOCK, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_CHILI_POOP_BLOCK, 0.1F, 100, "dried_chili_poop_block");
        smelting(recipeOutput, PoBlocks.GOLDEN_POOP_BLOCK, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_GOLDEN_POOP_BLOCK, 0.1F, 200, "dried_golden_poop_block");
        blasting(recipeOutput, PoBlocks.GOLDEN_POOP_BLOCK, RecipeCategory.BUILDING_BLOCKS, PoBlocks.DRIED_GOLDEN_POOP_BLOCK, 0.1F, 100, "dried_golden_poop_block");
        smelting(recipeOutput, PoBlocks.POOLIME_BLOCK, RecipeCategory.BUILDING_BLOCKS, PoBlocks.BROWN_TILE_BLOCK, 0.1F, 200, "tile_block");
        blasting(recipeOutput, PoBlocks.POOLIME_BLOCK, RecipeCategory.BUILDING_BLOCKS, PoBlocks.BROWN_TILE_BLOCK, 0.1F, 100, "tile_block");

        campfireCooking(recipeOutput, PoItems.POOP, RecipeCategory.MISC, Items.COCOA_BEANS, 0.35F, 600, "cocoa_beans");
        smelting(recipeOutput, PoItems.ROUNDWORM, RecipeCategory.MISC, Items.STRING, 0.35F, 200, "roundworm");
        campfireCooking(recipeOutput, PoItems.ROUNDWORM, RecipeCategory.MISC, Items.STRING, 0.35F, 200, "roundworm");
        smelting(recipeOutput, PoItems.MAGGOTS_SEEDS, RecipeCategory.BUILDING_BLOCKS, PoItems.BAKED_MAGGOTS, 0.35F, 200, "maggots_seeds");
        cooking(recipeOutput, RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, PoItems.MAGGOTS_SEEDS,
                RecipeCategory.BUILDING_BLOCKS, PoItems.BAKED_MAGGOTS, 0.35F, 100, "maggots_seeds", "_from_smoking");
        smelting(recipeOutput, PoBlocks.ROUNDWORM_BLOCK, RecipeCategory.BUILDING_BLOCKS, Items.SAND, 0.1F, 200, "roundworm_block");
    }

    private void buildFoodRecipes(RecipeOutput recipeOutput) {
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
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PoItems.POOPSICLE.get(), 2)
                .requires(Items.STICK)
                .requires(PoItems.SEEDBED_CURSE)
                .requires(Items.SNOWBALL)
                .requires(PoItems.MAGGOTS_SEEDS)
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PoBlocks.MUSHROOM_BED.get())
                .requires(Items.BROWN_MUSHROOM)
                .requires(Items.RED_MUSHROOM)
                .unlockedBy(getItemName(Items.BROWN_MUSHROOM), has(Items.BROWN_MUSHROOM))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.MUSHROOM_STEW)
                .requires(PoBlocks.MUSHROOM_BED.get())
                .requires(Items.BOWL)
                .unlockedBy(getItemName(PoBlocks.MUSHROOM_BED.get()), has(PoBlocks.MUSHROOM_BED.get()))
                .save(recipeOutput);
    }

    private void buildItemRecipes(RecipeOutput recipeOutput) {
        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.SALTPETER_BLOCK, PoItems.SALTPETER_SHARD);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PoItems.JINKELA, 4)
                .requires(PoItems.POOP)
                .requires(PoItems.UREA)
                .requires(PoItems.SALTPETER_SHARD)
                .unlockedBy(getItemName(PoItems.UREA), has(PoItems.UREA))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PoItems.TOILET_PLUG_WAND)
                .requires(PoItems.TOILET_PLUG.get())
                .requires(PoItems.POOP.get())
                .requires(Items.ENDER_EYE)
                .unlockedBy(getItemName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PoItems.FLY_CATCHER)
                .pattern("RRD")
                .pattern("RS ")
                .pattern("S  ")
                .define('R', PoItems.ROUNDWORM)
                .define('S', Items.STICK)
                .define('D', Items.DIAMOND)
                .unlockedBy(getItemName(Items.DIAMOND), has(Items.DIAMOND))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PoItems.RETURN_TOTEM.get(), 8)
                .pattern(" S ")
                .pattern("GEG")
                .pattern(" G ")
                .define('G', Items.GOLD_INGOT)
                .define('S', PoBlocks.SHIT)
                .define('E', Items.ECHO_SHARD)
                .unlockedBy(getItemName(Items.ECHO_SHARD), has(Items.ECHO_SHARD))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PoItems.TOTEM_OF_UNPOOPING.get(), 8)
                .pattern("S")
                .pattern("E")
                .define('S', PoBlocks.SHIT)
                .define('E', Items.ENCHANTED_GOLDEN_APPLE)
                .unlockedBy(getItemName(Items.ENCHANTED_GOLDEN_APPLE), has(Items.ENCHANTED_GOLDEN_APPLE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PoItems.TOKEN.get(), 4)
                .pattern("RRR")
                .pattern("RER")
                .pattern("RRR")
                .define('R', PoItems.ROUNDWORM.get())
                .define('E', Items.NAUTILUS_SHELL)
                .unlockedBy(getItemName(PoItems.ROUNDWORM), has(PoItems.ROUNDWORM))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PoItems.TIME_BELL)
                .requires(Items.BELL)
                .requires(PoItems.POOP.get())
                .requires(Items.DRAGON_EGG)
                .unlockedBy(getItemName(Items.DRAGON_EGG), has(Items.DRAGON_EGG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PoItems.FLUSH_TOILET_CART.get())
                .pattern("ITI")
                .define('I', Items.IRON_INGOT)
                .define('T', PoBlocks.FLUSH_TOILET)
                .unlockedBy(getItemName(PoBlocks.FLUSH_TOILET), has(PoBlocks.FLUSH_TOILET))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PoItems.GOLDEN_FLUSH_TOILET_CART.get())
                .pattern("ITI")
                .define('I', Items.IRON_INGOT)
                .define('T', PoBlocks.GOLDEN_FLUSH_TOILET)
                .unlockedBy(getItemName(PoBlocks.GOLDEN_FLUSH_TOILET), has(PoBlocks.GOLDEN_FLUSH_TOILET))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, PoItems.POP_TNT_MINECART)
                .requires(PoBlocks.POOP_TNT)
                .requires(Items.MINECART)
                .unlockedBy(getHasName(PoBlocks.POOP_TNT), has(PoBlocks.POOP_TNT))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PoItems.LAWRENCE_MUSIC_DISC)
                .requires(Tags.Items.MUSIC_DISCS)
                .requires(PoItems.POOP)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP))
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
    }

    private void buildBuildingRecipes(RecipeOutput recipeOutput) {
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

        PoBlocks.COLORED_TILE_BLOCK_FAMILIES.forEach(family -> blockFamilyRecipes(recipeOutput, family));
        tileBlockDyeingRecipes(recipeOutput);
        ginkgoWoodRecipes(recipeOutput);

        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PoBlocks.RAW_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.SAPLING_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PoBlocks.RAW_SAPLING_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.SEA_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PoBlocks.RAW_SEA_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.WITHER_POOP_BALL, RecipeCategory.BUILDING_BLOCKS, PoBlocks.RAW_WITHER_POOP_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.MAGGOTS_SEEDS, RecipeCategory.BUILDING_BLOCKS, PoBlocks.MAGGOTS_BLOCK);
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.ROUNDWORM, RecipeCategory.BUILDING_BLOCKS, PoBlocks.ROUNDWORM_BLOCK);

        threeBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.POOP, RecipeCategory.BUILDING_BLOCKS, PoBlocks.SHIT);
        threeBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.CHILI_POOP, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CHILI_SHIT);
        threeBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, PoItems.GOLDEN_POOP, RecipeCategory.BUILDING_BLOCKS, PoBlocks.GOLDEN_SHIT);

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
                .define('P', PoBlocks.POOP_VERTICAL_SLAB)
                .unlockedBy(getItemName(PoBlocks.POOP_BLOCK), has(PoBlocks.POOP_BLOCK))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_TRAPDOOR, 2)
                .pattern("PPP")
                .pattern("PPP")
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
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_WOOD, 3)
                .pattern("LL")
                .pattern("LL")
                .define('L', PoBlocks.POOP_LOG)
                .unlockedBy(getItemName(PoBlocks.POOP_LOG), has(PoBlocks.POOP_LOG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.STRIPPED_POOP_WOOD, 3)
                .pattern("LL")
                .pattern("LL")
                .define('L', PoBlocks.STRIPPED_POOP_LOG)
                .unlockedBy(getItemName(PoBlocks.STRIPPED_POOP_LOG), has(PoBlocks.STRIPPED_POOP_LOG))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_PIECE, 6)
                .pattern("PPP")
                .define('P', PoBlocks.POOP_SLAB)
                .unlockedBy(getItemName(PoBlocks.POOP_SLAB), has(PoBlocks.POOP_SLAB))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOPSKY_BLOCK)
                .pattern("RRR")
                .pattern("RSR")
                .pattern("RRR")
                .define('R', PoItems.ROUNDWORM.get())
                .define('S', PoTags.Items.SHITS)
                .unlockedBy(getItemName(PoItems.ROUNDWORM), has(PoItems.ROUNDWORM))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_CRAFTING_TABLE, 8)
                .pattern("PP")
                .define('P', PoBlocks.POOP_BLOCK)
                .unlockedBy(getItemName(PoBlocks.POOP_BLOCK), has(PoBlocks.POOP_BLOCK))
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
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PoBlocks.MAGGOTS_CHUNK_LOADER)
                .pattern(" R ")
                .pattern("RMR")
                .pattern("CCC")
                .define('R', Items.REDSTONE)
                .define('M', PoBlocks.MAGGOTS_BLOCK)
                .define('C', PoBlocks.CUT_POOP_BLOCK)
                .unlockedBy(getItemName(PoBlocks.MAGGOTS_BLOCK), has(PoBlocks.MAGGOTS_BLOCK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.GACHA_MACHINE)
                .pattern("III")
                .pattern("IEI")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('E', PoTags.Items.EGG)
                .unlockedBy(getItemName(Items.IRON_INGOT), has(Items.IRON_INGOT))
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, PoItems.FASTING_PILL.get())
                .requires(PoTags.Items.POOPS).requires(PoTags.Items.POOP_MOONCAKES).requires(PoTags.Items.SHITS)
                .requires(PoItems.POOP_BALL).requires(PoBlocks.ROUNDWORM_BLOCK).requires(PoItems.POOP_DUMPLINGS)
                .requires(PoItems.KING_OF_DRAGON_FRUIT).requires(PoBlocks.POOP_CAKE).requires(PoItems.POOBURGER_MEAT)
                .unlockedBy(getItemName(PoItems.POOBURGER_MEAT), has(PoBlocks.ROUNDWORM_BLOCK))
                .save(recipeOutput);
    }

    private void buildVanillaRecipes(RecipeOutput recipeOutput) {
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
        coralBlockRecipe(recipeOutput, Blocks.TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL, Blocks.TUBE_CORAL_FAN);
        coralBlockRecipe(recipeOutput, Blocks.BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL, Blocks.BRAIN_CORAL_FAN);
        coralBlockRecipe(recipeOutput, Blocks.BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL, Blocks.BUBBLE_CORAL_FAN);
        coralBlockRecipe(recipeOutput, Blocks.FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL, Blocks.FIRE_CORAL_FAN);
        coralBlockRecipe(recipeOutput, Blocks.HORN_CORAL_BLOCK, Blocks.HORN_CORAL, Blocks.HORN_CORAL_FAN);

        offerCompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE, PoItems.SPALL);
        create1x2ShapelessFrom(recipeOutput, Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.MOSS_BLOCK, 2);
        create1x2ShapelessFrom(recipeOutput, Blocks.MYCELIUM, Blocks.DIRT, PoBlocks.MYCELIUM_BLOCK, 2);

        create1x2ShapelessFrom(recipeOutput, Blocks.DIORITE, Blocks.COBBLESTONE, Blocks.CLAY, 2);
        create1x2ShapelessFrom(recipeOutput, Blocks.DIRT, Blocks.MUD, PoItems.POOP.get());
        create1x2ShapelessFrom(recipeOutput, Blocks.WET_SPONGE, PoBlocks.ROUNDWORM_BLOCK, PoBlocks.GOLDEN_POOP_BLOCK, 2);
    }

    private static void coralBlockRecipe(RecipeOutput recipeOutput, Block result, Block coral, Block coralFan) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result)
                .requires(PoBlocks.POOP_BLOCK)
                .requires(coral)
                .requires(coralFan)
                .unlockedBy(getItemName(coral), has(coral))
                .save(recipeOutput, getConversionRecipeName(result));
    }

    private void buildStonecuttingRecipes(RecipeOutput recipeOutput) {
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.STOOL, PoBlocks.DRIED_POOP_BLOCK, 4);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_CRAFTING_TABLE, PoBlocks.POOP_BLOCK, 4);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_PIECE, PoBlocks.POOP_BLOCK, 4);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_PIECE, PoBlocks.POOP_SLAB, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_FENCE, PoBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_FENCE_GATE, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_DOOR, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_TRAPDOOR, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_PRESSURE_PLATE, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BUTTON, PoBlocks.POOP_BLOCK, 4);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICKS, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_STAIRS, PoBlocks.POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_SLAB, PoBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_VERTICAL_SLAB, PoBlocks.POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BRICK_WALL, PoBlocks.POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK, PoBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_STAIRS, PoBlocks.DRIED_POOP_BLOCK);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_SLAB, PoBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB, PoBlocks.DRIED_POOP_BLOCK, 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.CUT_POOP_BLOCK_WALL, PoBlocks.DRIED_POOP_BLOCK);

        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.STRIPPED_POOP_LOG, PoBlocks.POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_EMPTY_LOG, PoBlocks.POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.STRIPPED_POOP_EMPTY_LOG, PoBlocks.STRIPPED_POOP_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.STRIPPED_POOP_EMPTY_LOG, PoBlocks.POOP_EMPTY_LOG);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BLOCK, PoBlocks.POOP_EMPTY_LOG, 4);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, PoBlocks.POOP_BLOCK, PoBlocks.STRIPPED_POOP_EMPTY_LOG, 4);
    }

    private void buildToiletRecipes(RecipeOutput recipeOutput) {
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
                .define('P', PoItems.POOP)
                .define('R', Blocks.RED_CONCRETE)
                .define('G', Blocks.GREEN_CONCRETE)
                .define('B', Blocks.BLUE_CONCRETE)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP))
                .save(recipeOutput, PoopSky.loc("hard_toilet_from_rainbow"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.GINKGO_TOILET)
                .pattern("B")
                .pattern("B")
                .pattern("P")
                .define('P', PoItems.POOP)
                .define('B', PoBlocks.GINKGO_PLANKS)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.PORTABLE_TOILET)
                .pattern("B")
                .pattern("B")
                .pattern("P")
                .define('P', PoItems.POOP)
                .define('B', Items.IRON_INGOT)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.FLUSH_TOILET)
                .pattern("PB")
                .pattern("B ")
                .define('P', PoItems.POOP)
                .define('B', Blocks.QUARTZ_BLOCK)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.GOLDEN_FLUSH_TOILET)
                .pattern("PB")
                .pattern("B ")
                .define('P', PoItems.POOP)
                .define('B', Blocks.GOLD_BLOCK)
                .unlockedBy(getItemName(PoItems.POOP), has(PoItems.POOP))
                .save(recipeOutput);
    }

    private void buildPopExplosionRecipes(RecipeOutput recipeOutput) {
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
                PopExplosionEntry.of(PoBlocks.DRIED_POOP_BLOCK.get(), PoBlocks.POOP_SAND.get()),
                PopExplosionEntry.of(Blocks.GRANITE, Blocks.DRIPSTONE_BLOCK),
                PopExplosionEntry.of(Blocks.DIORITE, Blocks.CALCITE),
                PopExplosionEntry.of(Blocks.ANDESITE, Blocks.TUFF),
                PopExplosionEntry.of(Blocks.BONE_BLOCK, Blocks.SKELETON_SKULL, 3),
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
        record AnalPressing(Block input, Block output, Block replaceTarget, int radius) {
            static AnalPressing of(Block input, Block output) {
                return new AnalPressing(input, output, null, 1);
            }

            static AnalPressing ofDeepslate(Block input, Block output) {
                return new AnalPressing(input, output, Blocks.DEEPSLATE, 1);
            }

            static AnalPressing ofNetherrack(Block input, Block output) {
                return new AnalPressing(input, output, Blocks.NETHERRACK, 1);
            }

            static AnalPressing ofNetherrack(Block input, Block output, int radius) {
                return new AnalPressing(input, output, Blocks.NETHERRACK, radius);
            }
        }
        List<AnalPressing> recipes = List.of(
                AnalPressing.of(Blocks.RAW_IRON_BLOCK, Blocks.IRON_ORE),
                AnalPressing.of(Blocks.RAW_COPPER_BLOCK, Blocks.COPPER_ORE),
                AnalPressing.of(Blocks.RAW_GOLD_BLOCK, Blocks.GOLD_ORE),
                AnalPressing.of(Blocks.IRON_BLOCK, Blocks.IRON_ORE),
                AnalPressing.of(Blocks.COPPER_BLOCK, Blocks.COPPER_ORE),
                AnalPressing.of(Blocks.GOLD_BLOCK, Blocks.GOLD_ORE),
                AnalPressing.of(Blocks.COAL_BLOCK, Blocks.COAL_ORE),
                AnalPressing.of(Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE),
                AnalPressing.of(Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE),
                AnalPressing.of(Blocks.REDSTONE_BLOCK, Blocks.REDSTONE_ORE),
                AnalPressing.of(Blocks.EMERALD_BLOCK, Blocks.EMERALD_ORE),

                AnalPressing.ofDeepslate(Blocks.RAW_IRON_BLOCK, Blocks.DEEPSLATE_IRON_ORE),
                AnalPressing.ofDeepslate(Blocks.RAW_COPPER_BLOCK, Blocks.DEEPSLATE_COPPER_ORE),
                AnalPressing.ofDeepslate(Blocks.RAW_GOLD_BLOCK, Blocks.DEEPSLATE_GOLD_ORE),
                AnalPressing.ofDeepslate(Blocks.IRON_BLOCK, Blocks.DEEPSLATE_IRON_ORE),
                AnalPressing.ofDeepslate(Blocks.COPPER_BLOCK, Blocks.DEEPSLATE_COPPER_ORE),
                AnalPressing.ofDeepslate(Blocks.GOLD_BLOCK, Blocks.DEEPSLATE_GOLD_ORE),
                AnalPressing.ofDeepslate(Blocks.COAL_BLOCK, Blocks.DEEPSLATE_COAL_ORE),
                AnalPressing.ofDeepslate(Blocks.DIAMOND_BLOCK, Blocks.DEEPSLATE_DIAMOND_ORE),
                AnalPressing.ofDeepslate(Blocks.LAPIS_BLOCK, Blocks.DEEPSLATE_LAPIS_ORE),
                AnalPressing.ofDeepslate(Blocks.REDSTONE_BLOCK, Blocks.DEEPSLATE_REDSTONE_ORE),
                AnalPressing.ofDeepslate(Blocks.EMERALD_BLOCK, Blocks.DEEPSLATE_EMERALD_ORE),

                AnalPressing.ofNetherrack(Blocks.RAW_GOLD_BLOCK, Blocks.NETHER_GOLD_ORE),
                AnalPressing.ofNetherrack(Blocks.GOLD_BLOCK, Blocks.NETHER_GOLD_ORE),
                AnalPressing.ofNetherrack(Blocks.QUARTZ_BLOCK, Blocks.NETHER_QUARTZ_ORE),
                AnalPressing.ofNetherrack(Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS, 2)
        );

        for (AnalPressing entry : recipes) {
            var builder = AnalPressingRecipeBuilder.analPressing(entry.input(), entry.output());
            if (entry.replaceTarget() != null) {
                builder.replaceTarget(entry.replaceTarget());
            }
            if (entry.radius() != 1) {
                builder.radius(entry.radius());
            }
            builder.unlockedBy(getItemName(entry.output()), has(entry.input()))
                    .save(recipeOutput, getModConversionRecipeName(entry.input(), entry.output()));
        }

        AnalPressingRecipeBuilder.analPressing(Blocks.COAL_BLOCK, Blocks.WITHER_SKELETON_SKULL)
                .replaceTarget(Blocks.SKELETON_SKULL)
                .unlockedBy(getItemName(Blocks.COAL_BLOCK), has(Blocks.WITHER_SKELETON_SKULL))
                .save(recipeOutput);
        AnalPressingRecipeBuilder.analPressing(Blocks.MOSS_BLOCK, Blocks.GRASS_BLOCK)
                .replaceTarget(Blocks.DIRT)
                .unlockedBy(getItemName(Blocks.MOSS_BLOCK), has(Blocks.MOSS_BLOCK))
                .save(recipeOutput);
        AnalPressingRecipeBuilder.analPressing(PoBlocks.MYCELIUM_BLOCK, Blocks.MYCELIUM)
                .replaceTarget(Blocks.DIRT)
                .unlockedBy(getItemName(Blocks.MOSS_BLOCK), has(Blocks.MOSS_BLOCK))
                .save(recipeOutput);
    }

    private void buildSieveRecipes(RecipeOutput recipeOutput) {
        RecipeOutput createLoaded = recipeOutput.withConditions(modLoaded(PoMods.CREATE.id()));
        RecipeOutput createNotLoaded = recipeOutput.withConditions(not(modLoaded(PoMods.CREATE.id())));
        RecipeOutput fdLoaded = recipeOutput.withConditions(modLoaded(PoMods.FARMERSDELIGHT.id()));

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
        // FarmersDelight
        SieveRecipeBuilder.sieve(ModItems.ORGANIC_COMPOST.get(), 200)
                .addOutput(Items.DIRT)
                .addOutput(ModItems.TREE_BARK.get(), 2, 0.5F)
                .addOutput(ModItems.STRAW.get(), 2, 0.5F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(fdLoaded, "orgenic_compost_has_farmersdelight");
        SieveRecipeBuilder.sieve(ModItems.RICH_SOIL.get(), 200)
                .addOutput(ModItems.ONION.get(), 0.5F)
                .addOutput(ModItems.TOMATO_SEEDS.get(), 0.5F)
                .addOutput(ModItems.CABBAGE_SEEDS.get(), 0.5F)
                .addOutput(ModItems.RICE.get(), 0.5F)
                .unlockedBy(getItemName(PoBlocks.SIEVE.get()), has(PoBlocks.SIEVE.get()))
                .save(fdLoaded, "rich_soil_has_farmersdelight");
    }

    private void buildFlyBarrelRecipes(RecipeOutput recipeOutput) {
        RecipeOutput create = recipeOutput.withConditions(modLoaded(PoMods.CREATE.id()));
        RecipeOutput ae2 = recipeOutput.withConditions(modLoaded(PoMods.AE2.id()));
        RecipeOutput mekanism = recipeOutput.withConditions(modLoaded(PoMods.MEKANISM.id()));

        record FlyBarrelEntry(FlyType.Type type, ResourceLocation result, RecipeOutput output) {
            static FlyBarrelEntry of(FlyType.Type type, ItemLike result) {
                return of(type, result, null);
            }

            static FlyBarrelEntry of(FlyType.Type type, ItemLike result, RecipeOutput output) {
                return new FlyBarrelEntry(type, BuiltInRegistries.ITEM.getKey(result.asItem()), output);
            }

            static FlyBarrelEntry of(FlyType.Type type, ResourceLocation result, RecipeOutput output) {
                return new FlyBarrelEntry(type, result, output);
            }
        }

        List<FlyBarrelEntry> recipes = List.of(
                FlyBarrelEntry.of(FlyTypes.NORMAL.get(), PoItems.MAGGOTS_SEEDS),
                FlyBarrelEntry.of(FlyTypes.WHITE.get(), Items.BONE_MEAL),
                FlyBarrelEntry.of(FlyTypes.LIGHT_GRAY.get(), Items.QUARTZ),
                FlyBarrelEntry.of(FlyTypes.GRAY.get(), Items.GRAVEL),
                FlyBarrelEntry.of(FlyTypes.BLACK.get(), Items.WITHER_ROSE),
                FlyBarrelEntry.of(FlyTypes.BROWN.get(), Items.COCOA_BEANS),
                FlyBarrelEntry.of(FlyTypes.RED.get(), Items.REDSTONE),
                FlyBarrelEntry.of(FlyTypes.ORANGE.get(), Items.TORCHFLOWER),
                FlyBarrelEntry.of(FlyTypes.YELLOW.get(), Items.GLOW_BERRIES),
                FlyBarrelEntry.of(FlyTypes.LIME.get(), Items.SEA_PICKLE),
                FlyBarrelEntry.of(FlyTypes.GREEN.get(), Items.CACTUS),
                FlyBarrelEntry.of(FlyTypes.CYAN.get(), Items.PRISMARINE_SHARD),
                FlyBarrelEntry.of(FlyTypes.LIGHT_BLUE.get(), Items.PRISMARINE_CRYSTALS),
                FlyBarrelEntry.of(FlyTypes.BLUE.get(), Items.LAPIS_LAZULI),
                FlyBarrelEntry.of(FlyTypes.PURPLE.get(), Items.CHORUS_FLOWER),
                FlyBarrelEntry.of(FlyTypes.MAGENTA.get(), Items.SHULKER_SHELL),
                FlyBarrelEntry.of(FlyTypes.PINK.get(), Items.PINK_PETALS),
                FlyBarrelEntry.of(FlyTypes.IRON.get(), Items.RAW_IRON),
                FlyBarrelEntry.of(FlyTypes.COPPER.get(), Items.RAW_COPPER),
                FlyBarrelEntry.of(FlyTypes.GOLD.get(), Items.RAW_GOLD),
                FlyBarrelEntry.of(FlyTypes.EMERALD.get(), Items.EMERALD),
                FlyBarrelEntry.of(FlyTypes.DIAMOND.get(), Items.DIAMOND),
                FlyBarrelEntry.of(FlyTypes.NETHERITE.get(), Items.NETHERITE_SCRAP),
                FlyBarrelEntry.of(FlyTypes.DRAGON_FRUIT.get(), Items.GUNPOWDER),
                FlyBarrelEntry.of(FlyTypes.GLOWSTONE.get(), Items.GLOWSTONE_DUST),
                FlyBarrelEntry.of(FlyTypes.ENDER.get(), Items.ENDER_PEARL),
                // Create
                FlyBarrelEntry.of(FlyTypes.ZINC.get(), AllItems.RAW_ZINC, create),
                // AE2
                FlyBarrelEntry.of(FlyTypes.CERTUS.get(), PoMods.AE2.rl("certus_quartz_crystal"), ae2),
                FlyBarrelEntry.of(FlyTypes.SKY_DUST.get(), PoMods.AE2.rl("sky_dust"), ae2),
                // MEK
                FlyBarrelEntry.of(FlyTypes.OSMIUM.get(), PoMods.MEKANISM.rl("raw_osmium"), mekanism),
                FlyBarrelEntry.of(FlyTypes.TIN.get(), PoMods.MEKANISM.rl("raw_tin"), mekanism),
                FlyBarrelEntry.of(FlyTypes.LEAD.get(), PoMods.MEKANISM.rl("raw_lead"), mekanism),
                FlyBarrelEntry.of(FlyTypes.URANIUM.get(), PoMods.MEKANISM.rl("raw_uranium"), mekanism),
                FlyBarrelEntry.of(FlyTypes.FLUORITE.get(), PoMods.MEKANISM.rl("fluorite_gem"), mekanism)
        );

        for (FlyBarrelEntry recipe : recipes) {
            FlyBarrelRecipeBuilder.flyBarrel(recipe.type().id(), recipe.result())
                    .unlockedBy(getHasName(PoBlocks.FLY_BARREL), has(PoBlocks.FLY_BARREL))
                    .save(recipe.output() != null ? recipe.output() : recipeOutput, recipe.type().id());
        }
    }

    private void buildBreedingChestRecipes(RecipeOutput recipeOutput) {
        RecipeOutput create = recipeOutput.withConditions(modLoaded(PoMods.CREATE.id()));
        RecipeOutput ae2 = recipeOutput.withConditions(modLoaded(PoMods.AE2.id()));
        RecipeOutput mekanism = recipeOutput.withConditions(modLoaded(PoMods.MEKANISM.id()));

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
                Breeding.of(FlyTypes.CYAN.get(), FlyTypes.IRON.get(), FlyTypes.ZINC.get()).loaded(create),
                // AE2
                Breeding.of(FlyTypes.LIGHT_BLUE.get(), FlyTypes.DIAMOND.get(), FlyTypes.CERTUS.get()).loaded(ae2),
                Breeding.of(FlyTypes.BLACK.get(), FlyTypes.GLOWSTONE.get(), FlyTypes.SKY_DUST.get()).loaded(ae2),
                // MEK
                Breeding.of(FlyTypes.LIGHT_GRAY.get(), FlyTypes.IRON.get(), FlyTypes.TIN.get()).loaded(mekanism),
                Breeding.of(FlyTypes.IRON.get(), FlyTypes.TIN.get(), FlyTypes.LEAD.get()).loaded(mekanism),
                Breeding.of(FlyTypes.DIAMOND.get(), FlyTypes.LEAD.get(), FlyTypes.OSMIUM.get()).loaded(mekanism),
                Breeding.of(FlyTypes.DRAGON_FRUIT.get(), FlyTypes.EMERALD.get(), FlyTypes.URANIUM.get()).loaded(mekanism),
                Breeding.of(FlyTypes.PURPLE.get(), FlyTypes.OSMIUM.get(), FlyTypes.FLUORITE.get()).loaded(mekanism)
        );

        for (Breeding recipe : breedingRecipes) {
            String id = recipe.p1 + "_plus_" + recipe.p2;
            var builder = BreedingChestRecipeBuilder.breedingChest(recipe.p1, recipe.p2, recipe.result);
            builder.unlockedBy(getHasName(PoBlocks.FLY_BARREL), has(PoBlocks.FLY_BARREL))
                    .save(recipe.output != null ? recipe.output : recipeOutput, id);
        }
    }


    private void buildCompooperRecipes(RecipeOutput recipeOutput) {
        CompooperRecipeBuilder.compooper(CompooperType.WATER.id(), FlyItem.withType(FlyTypes.NORMAL.get()), FlyItem.withType(FlyTypes.BLUE.get()))
                .unlockedBy(getItemName(PoBlocks.COMPOOPER.get()), has(PoBlocks.COMPOOPER.get()))
                .save(recipeOutput, "fly_normal_to_blue");
        CompooperRecipeBuilder.compooper(CompooperType.WATER.id(), PoItems.SALTPETER_SHARD.get(), Items.SNOWBALL)
                .unlockedBy(getItemName(PoBlocks.COMPOOPER.get()), has(PoBlocks.COMPOOPER.get()))
                .save(recipeOutput);
        CompooperRecipeBuilder.compooper(CompooperType.WATER.id(), PoItems.OMEN_UPGRADE_SMITHING_TEMPLATE.get(), Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .unlockedBy(getItemName(PoBlocks.COMPOOPER.get()), has(PoBlocks.COMPOOPER.get()))
                .save(recipeOutput);

        CompooperRecipeBuilder.compooper(CompooperType.LAVA.id(), Items.STICK, Items.BLAZE_ROD)
                .unlockedBy(getItemName(PoBlocks.COMPOOPER.get()), has(PoBlocks.COMPOOPER.get()))
                .save(recipeOutput);

        CompooperRecipeBuilder.compooper(CompooperType.POWDER_SNOW.id(), Items.STICK, Items.BREEZE_ROD)
                .unlockedBy(getItemName(PoBlocks.COMPOOPER.get()), has(PoBlocks.COMPOOPER.get()))
                .save(recipeOutput);
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

    private void spallToolRecipes(RecipeOutput recipeOutput) {
        var enchantment = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        spallEnchantedRecipe(recipeOutput, enchantment.getOrThrow(Enchantments.LOOTING), PoItems.SPALL_SWORD.get(), RecipeCategory.COMBAT, "M", "M", "S");
        spallEnchantedRecipe(recipeOutput, enchantment.getOrThrow(Enchantments.EFFICIENCY), PoItems.SPALL_SHOVEL.get(), RecipeCategory.TOOLS, "M", "S", "S");
        spallEnchantedRecipe(recipeOutput, enchantment.getOrThrow(Enchantments.FORTUNE), PoItems.SPALL_PICKAXE.get(), RecipeCategory.TOOLS, "MMM", " S ", " S ");
        spallEnchantedRecipe(recipeOutput, enchantment.getOrThrow(Enchantments.SILK_TOUCH), PoItems.SPALL_AXE.get(), RecipeCategory.TOOLS, "MM", "MS", " S");
        spallEnchantedRecipe(recipeOutput, enchantment.getOrThrow(Enchantments.UNBREAKING), PoItems.SPALL_HOE.get(), RecipeCategory.TOOLS, "MM", " S", " S");
    }

    private void spallEnchantedRecipe(RecipeOutput recipeOutput, Holder<Enchantment> enchantment, ItemLike item, RecipeCategory category, String... patterns) {
        ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchants.set(enchantment, 1);
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(category, stack);
        for (String pattern : patterns) {
            builder.pattern(pattern);
        }
        builder.define('M', PoItems.SPALL)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PoItems.SPALL), has(PoItems.SPALL))
                .save(recipeOutput);
    }

    private void stairsRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike input) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 8)
                .pattern("P  ")
                .pattern("PP ")
                .pattern("PPP")
                .define('P', input)
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput);
    }

    private void ginkgoWoodRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, PoBlocks.GINKGO_PLANKS, 4)
                .requires(PoTags.Items.GINKGO_LOGS)
                .unlockedBy("has_ginkgo_logs", has(PoTags.Items.GINKGO_LOGS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.GINKGO_WOOD, 3)
                .pattern("LL")
                .pattern("LL")
                .define('L', PoBlocks.GINKGO_LOG)
                .unlockedBy(getItemName(PoBlocks.GINKGO_LOG), has(PoBlocks.GINKGO_LOG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.STRIPPED_GINKGO_WOOD, 3)
                .pattern("LL")
                .pattern("LL")
                .define('L', PoBlocks.STRIPPED_GINKGO_LOG)
                .unlockedBy(getItemName(PoBlocks.STRIPPED_GINKGO_LOG), has(PoBlocks.STRIPPED_GINKGO_LOG))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.GINKGO_STAIRS, 4)
                .pattern("P  ")
                .pattern("PP ")
                .pattern("PPP")
                .define('P', PoBlocks.GINKGO_PLANKS)
                .unlockedBy(getItemName(PoBlocks.GINKGO_PLANKS), has(PoBlocks.GINKGO_PLANKS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.GINKGO_SLAB, 6)
                .pattern("PPP")
                .define('P', PoBlocks.GINKGO_PLANKS)
                .unlockedBy(getItemName(PoBlocks.GINKGO_PLANKS), has(PoBlocks.GINKGO_PLANKS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PoBlocks.GINKGO_VERTICAL_SLAB, 6)
                .pattern("P")
                .pattern("P")
                .pattern("P")
                .define('P', PoBlocks.GINKGO_PLANKS)
                .unlockedBy(getItemName(PoBlocks.GINKGO_PLANKS), has(PoBlocks.GINKGO_PLANKS))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, PoBlocks.GINKGO_BUTTON)
                .requires(PoBlocks.GINKGO_PLANKS)
                .unlockedBy(getItemName(PoBlocks.GINKGO_PLANKS), has(PoBlocks.GINKGO_PLANKS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PoBlocks.GINKGO_PRESSURE_PLATE)
                .pattern("PP")
                .define('P', PoBlocks.GINKGO_PLANKS)
                .unlockedBy(getItemName(PoBlocks.GINKGO_PLANKS), has(PoBlocks.GINKGO_PLANKS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PoBlocks.GINKGO_FENCE, 3)
                .pattern("PSP")
                .pattern("PSP")
                .define('P', PoBlocks.GINKGO_PLANKS)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PoBlocks.GINKGO_PLANKS), has(PoBlocks.GINKGO_PLANKS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PoBlocks.GINKGO_FENCE_GATE)
                .pattern("SPS")
                .pattern("SPS")
                .define('P', PoBlocks.GINKGO_PLANKS)
                .define('S', Items.STICK)
                .unlockedBy(getItemName(PoBlocks.GINKGO_PLANKS), has(PoBlocks.GINKGO_PLANKS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PoBlocks.GINKGO_DOOR, 3)
                .pattern("PP")
                .pattern("PP")
                .pattern("PP")
                .define('P', PoBlocks.GINKGO_PLANKS)
                .unlockedBy(getItemName(PoBlocks.GINKGO_PLANKS), has(PoBlocks.GINKGO_PLANKS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, PoBlocks.GINKGO_TRAPDOOR, 2)
                .pattern("PPP")
                .pattern("PPP")
                .define('P', PoBlocks.GINKGO_PLANKS)
                .unlockedBy(getItemName(PoBlocks.GINKGO_PLANKS), has(PoBlocks.GINKGO_PLANKS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, PoItems.GINKGO_BOAT)
                .pattern("P P")
                .pattern("PPP")
                .define('P', PoBlocks.GINKGO_PLANKS)
                .unlockedBy(getItemName(PoBlocks.GINKGO_PLANKS), has(PoBlocks.GINKGO_PLANKS))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, PoItems.GINKGO_CHEST_BOAT)
                .requires(Items.CHEST)
                .requires(PoItems.GINKGO_BOAT)
                .unlockedBy(getItemName(PoItems.GINKGO_BOAT), has(PoItems.GINKGO_BOAT))
                .save(recipeOutput);
    }

    private void blockFamilyRecipes(RecipeOutput recipeOutput, PoBlocks.BlockFamily family) {
        stairsRecipe(recipeOutput, family.stairs(), family.block());
        slabRecipe(recipeOutput, family.slab(), family.block());
        verticalSlabRecipe(recipeOutput, family.verticalSlab(), family.block());
        wallRecipe(recipeOutput, family.wall(), family.block());
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, family.stairs(), family.block());
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, family.slab(), family.block(), 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, family.verticalSlab(), family.block(), 2);
        stonecutterResult(recipeOutput, RecipeCategory.BUILDING_BLOCKS, family.wall(), family.block());
    }

    private static void tileBlockDyeingRecipes(RecipeOutput recipeOutput) {
        var families = PoBlocks.COLORED_TILE_BLOCK_FAMILIES;
        for (int i = 0; i < families.size(); i++) {
            Item dye = DYES.get(i);
            var family = families.get(i);
            tileDyeRecipe(recipeOutput, dye, family.block(), PoTags.Items.TILE_BLOCKS);
            tileDyeRecipe(recipeOutput, dye, family.stairs(), PoTags.Items.TILE_STAIRS);
            tileDyeRecipe(recipeOutput, dye, family.slab(), PoTags.Items.TILE_SLABS);
            tileDyeRecipe(recipeOutput, dye, family.verticalSlab(), PoTags.Items.TILE_VERTICAL_SLABS);
            tileDyeRecipe(recipeOutput, dye, family.wall(), PoTags.Items.TILE_WALLS);
        }
    }

    private static void tileDyeRecipe(RecipeOutput recipeOutput, Item dye, BlockEntry<?> block, TagKey<Item> inputTag) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block.get(), 8)
                .pattern("TTT")
                .pattern("TDT")
                .pattern("TTT")
                .define('T', inputTag)
                .define('D', dye)
                .unlockedBy(getItemName(dye), has(dye))
                .save(recipeOutput, PoopSky.loc(getItemName(block.get()) + "_from_dyeing"));
    }

    private static final List<Item> DYES = List.of(
            Items.WHITE_DYE, Items.LIGHT_GRAY_DYE, Items.GRAY_DYE, Items.BLACK_DYE,
            Items.BROWN_DYE, Items.RED_DYE, Items.ORANGE_DYE, Items.YELLOW_DYE,
            Items.LIME_DYE, Items.GREEN_DYE, Items.CYAN_DYE, Items.LIGHT_BLUE_DYE,
            Items.BLUE_DYE, Items.PURPLE_DYE, Items.MAGENTA_DYE, Items.PINK_DYE
    );

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

    private static void offerCompactingRecipe(RecipeOutput recipeOutput, RecipeCategory category, ItemLike output, ItemLike input) {
        ShapedRecipeBuilder.shaped(category, output)
                .define('#', input)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput, getConversionRecipeName(output) + "_from_compacting");
    }

    private static void offer2x2CompactingRecipe(RecipeOutput recipeOutput, ItemLike output, ItemLike input) {
        offer2x2CompactingRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, RecipeCategory.MISC, output, input, 1, 4);
    }

    private static void offer2x2CompactingRecipe(RecipeOutput recipeOutput, RecipeCategory packedCategory,
                                                 RecipeCategory unpackedCategory, ItemLike packed, ItemLike unpacked,
                                                 int packedCount, int unpackedCount) {
        ShapedRecipeBuilder.shaped(packedCategory, packed, packedCount)
                .define('#', unpacked)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getItemName(unpacked), has(unpacked))
                .save(recipeOutput, getConversionRecipeName(packed, unpacked));
        ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, unpackedCount)
                .requires(packed)
                .unlockedBy(getItemName(packed), has(packed))
                .save(recipeOutput, getConversionRecipeName(unpacked, packed));
    }

    private static void offer2x2CompactingRecipe(RecipeOutput recipeOutput, RecipeCategory category, ItemLike output, ItemLike input) {
        offer2x2CompactingRecipe(recipeOutput, category, output, input, 1);
    }

    private static void offer2x2CompactingRecipe(RecipeOutput recipeOutput, RecipeCategory category, ItemLike output, ItemLike input, int count) {
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

    private static void shapeless1x1Recipe(RecipeOutput recipeOutput, ItemLike result, ItemLike input, ItemLike input1) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result)
                .requires(input1).requires(input)
                .unlockedBy(getItemName(input), has(input))
                .save(recipeOutput, getConversionRecipeName(result));
    }

    private static void omenSmithing(RecipeOutput recipeOutput, Item ingredientItem, RecipeCategory category, Item resultItem) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(PoItems.OMEN_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), Ingredient.of(PoItems.OMINOUS_FILTHY_INGOT), category, resultItem
                )
                .unlocks("has_ominous_filthy_ingot", has(PoItems.OMINOUS_FILTHY_INGOT))
                .save(recipeOutput, PoopSky.MOD_ID + ":" + getItemName(resultItem) + "_smithing");
    }

    private static void smelting(RecipeOutput recipeOutput, ItemLike ingredient, RecipeCategory category,
                                 ItemLike result, float experience, int cookingTime, String group) {
        cooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredient,
                category, result, experience, cookingTime, group, "_from_smelting");
    }

    private static void blasting(RecipeOutput recipeOutput, ItemLike ingredient, RecipeCategory category,
                                 ItemLike result, float experience, int cookingTime, String group) {
        cooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredient,
                category, result, experience, cookingTime, group, "_from_blasting");
    }

    private static void campfireCooking(RecipeOutput recipeOutput, ItemLike ingredient, RecipeCategory category,
                                        ItemLike result, float experience, int cookingTime, String group) {
        cooking(recipeOutput, RecipeSerializer.CAMPFIRE_COOKING_RECIPE, CampfireCookingRecipe::new, ingredient,
                category, result, experience, cookingTime, group, "_from_campfire_cooking");
    }

    private static <T extends AbstractCookingRecipe> void cooking(
            RecipeOutput recipeOutput, RecipeSerializer<T> serializer, AbstractCookingRecipe.Factory<T> recipeFactory,
            ItemLike ingredient, RecipeCategory category, ItemLike result, float experience, int cookingTime,
            String group, String suffix) {
        SimpleCookingRecipeBuilder.generic(
                        Ingredient.of(ingredient), category, result, experience, cookingTime, serializer, recipeFactory
                )
                .group(group)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, PoopSky.loc(getItemName(result) + suffix + "_" + getItemName(ingredient)));
    }

    private static void stonecutterResult(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, ItemLike material) {
        stonecutterResult(recipeOutput, category, result, material, 1);
    }

    private static void stonecutterResult(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, ItemLike material, int resultCount) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), category, result, resultCount)
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput, getConversionRecipeName(result, material) + "_stonecutting");
    }

    private static void copySmithingTemplate(RecipeOutput recipeOutput, ItemLike template, ItemLike baseItem, ItemLike item) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, template, 2).define('#', item).define('C', baseItem).define('S', template).pattern("#S#").pattern("#C#").pattern("###").unlockedBy(getHasName(template), has(template)).save(recipeOutput);
    }

    protected static void nineBlockStorageRecipes(RecipeOutput recipeOutput, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed) {
        nineBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed, getItemName(packed) + "_from_" + getItemName(unpacked), getItemName(unpacked) + "_from_" + getItemName(packed));
    }

    private static void nineBlockStorageRecipes(RecipeOutput recipeOutput, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, String unpackedName) {
        ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 9).requires(packed).unlockedBy(getHasName(packed), has(packed)).save(recipeOutput, PoopSky.loc(unpackedName));
        ShapedRecipeBuilder.shaped(packedCategory, packed).define('#', unpacked).pattern("###").pattern("###").pattern("###").unlockedBy(getHasName(unpacked), has(unpacked)).save(recipeOutput, PoopSky.loc(packedName));
    }

    private static void threeBlockStorageRecipes(RecipeOutput recipeOutput, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed) {
        threeBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed, getItemName(packed) + "_from_" + getItemName(unpacked), getItemName(unpacked) + "_from_" + getItemName(packed));
    }

    private static void threeBlockStorageRecipes(RecipeOutput recipeOutput, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, String unpackedName) {
        ShapelessRecipeBuilder.shapeless(packedCategory, packed, 1).requires(unpacked, 3).unlockedBy(getHasName(unpacked), has(unpacked)).save(recipeOutput, PoopSky.loc(packedName));
        ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 3).requires(packed).unlockedBy(getHasName(packed), has(packed)).save(recipeOutput, PoopSky.loc(unpackedName));
    }

    private static String getConversionRecipeName(ItemLike result) {
        return PoopSky.MOD_ID + ":" + getItemName(result);
    }

    public static String getConversionRecipeName(ItemLike result, ItemLike input) {
        return PoopSky.MOD_ID + ":" + getItemName(result) + "_from_" + getItemName(input);
    }

    private static String getModConversionRecipeName(ItemLike result, ItemLike input) {
        return getItemName(input) + "_to_" + getItemName(result);
    }
}
