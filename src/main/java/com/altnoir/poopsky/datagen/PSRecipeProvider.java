package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PSRecipeProvider extends FabricRecipeProvider {
    public PSRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.COBWEB)
                        .pattern("SSS")
                        .pattern("SPS")
                        .pattern("SSS")
                        .input('S', Items.STRING)
                        .input('P', PSItems.POOP)
                        .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                        .offerTo(exporter);

                createShapeless(RecipeCategory.MISC, PSItems.TOILET_LINKER)
                        .input(PSItems.POOP)
                        .input(Items.ENDER_EYE)
                        .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE))
                        .offerTo(exporter);

                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK)
                        .pattern("PP")
                        .pattern("PP")
                        .input('P', PSItems.POOP)
                        .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                        .offerTo(exporter);
                createShapeless(RecipeCategory.MISC, PSItems.POOP, 4)
                        .input(PSBlocks.POOP_BLOCK)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter);

                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_STAIRS, 8)
                        .pattern("P  ")
                        .pattern("PP ")
                        .pattern("PPP")
                        .input('P', PSBlocks.POOP_BLOCK)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter);
                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_SLAB, 6)
                        .pattern("PPP")
                        .input('P', PSBlocks.POOP_BLOCK)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter);

                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK)
                        .pattern("P")
                        .pattern("P")
                        .input('P', PSBlocks.POOP_SLAB)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter, getItemPath(PSBlocks.POOP_BLOCK) + "_from_slab");

                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_VERTICAL_SLAB, 6)
                        .pattern("P")
                        .pattern("P")
                        .pattern("P")
                        .input('P', PSBlocks.POOP_BLOCK)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter);
                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK)
                        .pattern("PP")
                        .input('P', PSBlocks.POOP_VERTICAL_SLAB)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter, getItemPath(PSBlocks.POOP_BLOCK) + "_from_vertical_slab");

                createShapeless(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BUTTON)
                        .input(PSItems.POOP)
                        .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                        .offerTo(exporter);
                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PRESSURE_PLATE)
                        .pattern("PP")
                        .input('P', PSItems.POOP)
                        .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                        .offerTo(exporter);

                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE, 3)
                        .pattern("BPB")
                        .pattern("BPB")
                        .input('B', PSBlocks.POOP_BLOCK)
                        .input('P', PSItems.POOP)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter);
                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE_GATE)
                        .pattern("PBP")
                        .pattern("PBP")
                        .input('B', PSBlocks.POOP_BLOCK)
                        .input('P', PSItems.POOP)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter);
                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_WALL, 6)
                        .pattern("PPP")
                        .pattern("PPP")
                        .input('P', PSBlocks.POOP_BLOCK)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter);

                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_DOOR, 3)
                        .pattern("PP")
                        .pattern("PP")
                        .pattern("PP")
                        .input('P', PSBlocks.POOP_BLOCK)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter);
                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_TRAPDOOR, 2)
                        .pattern("PP")
                        .pattern("PP")
                        .input('P', PSBlocks.POOP_BLOCK)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter);

                createShapeless(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, 4)
                        .input(PSBlocks.POOP_EMPTY_LOG)
                        .criterion(hasItem(PSBlocks.POOP_LOG), conditionsFromItem(PSBlocks.POOP_LOG))
                        .offerTo(exporter, getItemPath(PSBlocks.POOP_BLOCK) + "_from_empty_log");
                createShapeless(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, 4)
                        .input(PSBlocks.STRIPPED_POOP_EMPTY_LOG)
                        .criterion(hasItem(PSBlocks.POOP_LOG), conditionsFromItem(PSBlocks.POOP_LOG))
                        .offerTo(exporter, getItemPath(PSBlocks.POOP_BLOCK) + "_from_stripped_empty_log");
                createShaped(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PIECE, 3)
                        .pattern("PP")
                        .input('P', PSBlocks.POOP_BLOCK)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter);
                createShapeless(RecipeCategory.BUILDING_BLOCKS, PSItems.LAWRENCE_MUSIC_DISC)
                        .input(ConventionalItemTags.MUSIC_DISCS).input(PSItems.POOP)
                        .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                        .offerTo(exporter);

                //原版物品配方
                createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.CRAFTING_TABLE)
                        .pattern("SS")
                        .pattern("PP")
                        .input('S', PSItems.SPALL)
                        .input('P', PSBlocks.POOP_BLOCK)
                        .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                        .offerTo(exporter, getItemPath(Blocks.CRAFTING_TABLE) + "_from_poop_block");

                createShaped(RecipeCategory.MISC, Blocks.POINTED_DRIPSTONE)
                        .pattern("S")
                        .pattern("S")
                        .pattern("S")
                        .input('S', PSItems.SPALL)
                        .criterion(hasItem(PSItems.SPALL), conditionsFromItem(PSItems.SPALL))
                        .offerTo(exporter, getItemPath(Blocks.POINTED_DRIPSTONE) + "_from_spall");
                createShaped(RecipeCategory.MISC, Items.FLINT)
                        .pattern("S")
                        .pattern("S")
                        .input('S', PSItems.SPALL)
                        .criterion(hasItem(PSItems.SPALL), conditionsFromItem(PSItems.SPALL))
                        .offerTo(exporter, getItemPath(Items.FLINT) + "_from_spall");
                createShaped(RecipeCategory.MISC, Blocks.GRAVEL)
                        .pattern("FF")
                        .pattern("FF")
                        .input('F', Items.FLINT)
                        .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
                        .offerTo(exporter, getItemPath(Blocks.GRAVEL) + "_from_flint_x4");

                offerCompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE, PSItems.SPALL);

                createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLESTONE)
                        .input(Blocks.GRAVEL).input(PSItems.SPALL)
                        .criterion(hasItem(PSItems.SPALL), conditionsFromItem(PSItems.SPALL))
                        .offerTo(exporter, getItemPath(Blocks.COBBLESTONE) + "_from_spall");
                createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.ANDESITE)
                        .input(Blocks.GRAVEL).input(Items.FLINT)
                        .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
                        .offerTo(exporter, getItemPath(Blocks.COBBLESTONE) + "_from_flint");
                createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.DIORITE)
                        .input(Blocks.GRAVEL).input(Items.CLAY_BALL)
                        .criterion(hasItem(Items.CLAY_BALL), conditionsFromItem(Items.CLAY_BALL))
                        .offerTo(exporter, getItemPath(Blocks.COBBLESTONE) + "_from_clay_ball");
                createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.GRANITE)
                        .input(Blocks.GRAVEL).input(Items.BRICK)
                        .criterion(hasItem(Items.CLAY_BALL), conditionsFromItem(Items.CLAY_BALL))
                        .offerTo(exporter, getItemPath(Blocks.COBBLESTONE) + "_from_brick");

                createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF)
                        .input(Blocks.ANDESITE).input(PSItems.SPALL)
                        .criterion(hasItem(PSItems.SPALL), conditionsFromItem(PSItems.SPALL))
                        .offerTo(exporter, getItemPath(Blocks.TUFF) + "_from_spall");
                createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.CALCITE)
                        .input(Blocks.DIORITE).input(PSItems.SPALL)
                        .criterion(hasItem(PSItems.SPALL), conditionsFromItem(PSItems.SPALL))
                        .offerTo(exporter, getItemPath(Blocks.CALCITE) + "_from_spall");
                createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLED_DEEPSLATE)
                        .input(Blocks.MUD).input(PSItems.SPALL)
                        .criterion(hasItem(PSItems.SPALL), conditionsFromItem(PSItems.SPALL))
                        .offerTo(exporter, getItemPath(Blocks.COBBLED_DEEPSLATE) + "_from_spall");

                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.STOOL, PSBlocks.POOP_BLOCK);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PIECE, PSBlocks.POOP_BLOCK, 8);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_STAIRS, PSBlocks.POOP_BLOCK);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_SLAB, PSBlocks.POOP_BLOCK, 2);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_VERTICAL_SLAB, PSBlocks.POOP_BLOCK, 2);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE, PSBlocks.POOP_BLOCK, 2);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_FENCE_GATE, PSBlocks.POOP_BLOCK);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_WALL, PSBlocks.POOP_BLOCK, 2);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_DOOR, PSBlocks.POOP_BLOCK);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_TRAPDOOR, PSBlocks.POOP_BLOCK);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_PRESSURE_PLATE, PSBlocks.POOP_BLOCK);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BUTTON, PSBlocks.POOP_BLOCK);

                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.STRIPPED_POOP_LOG, PSBlocks.POOP_LOG);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_EMPTY_LOG, PSBlocks.POOP_LOG);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.STRIPPED_POOP_EMPTY_LOG, PSBlocks.STRIPPED_POOP_LOG);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.STRIPPED_POOP_EMPTY_LOG, PSBlocks.POOP_EMPTY_LOG);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, PSBlocks.POOP_EMPTY_LOG, 4);
                offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK, PSBlocks.STRIPPED_POOP_EMPTY_LOG, 4);

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

                createShapeless(RecipeCategory.BUILDING_BLOCKS, ToiletBlocks.RAINBOW_TOILET)
                        .input(ToiletBlocks.RED_CONCRETE_TOILET)
                        .input(ToiletBlocks.GREEN_CONCRETE_TOILET)
                        .input(ToiletBlocks.BLUE_CONCRETE_TOILET)
                        .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                        .offerTo(exporter);
            }

            private void toiletRecipes(RecipeExporter exporter, Block toilet, Block block) {
                createShaped(RecipeCategory.BUILDING_BLOCKS, toilet)
                        .pattern("#P#")
                        .pattern("###")
                        .pattern("###")
                        .input('P', PSItems.POOP)
                        .input('#', block)
                        .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                        .offerTo(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return PoopSky.MOD_ID + "Recipe";
    }
}
