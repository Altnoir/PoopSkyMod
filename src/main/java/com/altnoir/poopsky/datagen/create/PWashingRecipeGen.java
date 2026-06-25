package com.altnoir.poopsky.datagen.create;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.compat.PSMods;
import com.altnoir.poopsky.init.PItems;
import com.simibubi.create.api.data.recipe.WashingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
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
        RecipeOutput conditionalOutput = p_recipeOutput.withConditions(modLoaded(PSMods.CREATE.id()));

        convert(PBlocks.POOP_PIECE, Blocks.SNOW).register(conditionalOutput);
        convert(PBlocks.POOP_BLOCK, Blocks.SNOW_BLOCK).register(conditionalOutput);
        convert(PItems.POOP_BALL, Items.SNOWBALL).register(conditionalOutput);
        convert(PItems.URINE_BUCKET, Items.POWDER_SNOW_BUCKET).register(conditionalOutput);
    }

    private GeneratedRecipe convert(ItemLike input, ItemLike result) {
        return create(() -> input, b -> b.output(result));
    }
}
