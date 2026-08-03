/*
package com.altnoir.poopsky.impl.create;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.simibubi.create.api.data.recipe.WashingRecipeGen;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class PWashingRecipeGen extends WashingRecipeGen implements IConditionBuilder {
    public PWashingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, PoopSky.MOD_ID);
    }

    @Override
    protected void buildRecipes(RecipeOutput p_recipeOutput, HolderLookup.Provider holderLookup) {
        RecipeOutput conditionalOutput = p_recipeOutput.withConditions(modLoaded(PoMods.CREATE.id()));

        convert(PoBlocks.POOP_PIECE, Blocks.SNOW).register(conditionalOutput);
        convert(PoBlocks.POOP_BLOCK, Blocks.SNOW_BLOCK).register(conditionalOutput);
        convert(PoItems.POOP_BALL, Items.SNOWBALL).register(conditionalOutput);
        convert(PoItems.URINE_BUCKET, Items.POWDER_SNOW_BUCKET).register(conditionalOutput);
    }

    private GeneratedRecipe convert(ItemLike input, ItemLike result) {
        return create(asResource(RegisteredObjectsHelper.getKeyOrThrow(result.asItem())
                        .getPath()),
                p -> p.withItemIngredients(Ingredient.of(input))
                        .output(result));
    }
}
 */
