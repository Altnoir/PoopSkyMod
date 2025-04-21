package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PSRecipeProvider extends FabricRecipeProvider {
    public PSRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, PSBlocks.POOP_BLOCK)
                .pattern("PP")
                .pattern("PP")
                .input('P', PSItems.POOP)
                .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, PSItems.POOP, 4)
                .input(PSBlocks.POOP_BLOCK)
                .criterion(hasItem(PSBlocks.POOP_BLOCK), conditionsFromItem(PSBlocks.POOP_BLOCK))
                .offerTo(exporter);


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

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ToiletBlocks.RAINBOW_TOILET)
                .input(ToiletBlocks.RED_CONCRETE_TOILET)
                .input(ToiletBlocks.GREEN_CONCRETE_TOILET)
                .input(ToiletBlocks.BLUE_CONCRETE_TOILET)
                .criterion(hasItem(PSItems.POOP), conditionsFromItem(PSItems.POOP))
                .offerTo(exporter);
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
