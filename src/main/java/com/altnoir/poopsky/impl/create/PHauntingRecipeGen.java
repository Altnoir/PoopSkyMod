package com.altnoir.poopsky.impl.create;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.init.PoBlocks;
import com.simibubi.create.api.data.recipe.HauntingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class PHauntingRecipeGen extends HauntingRecipeGen implements IConditionBuilder {
    public PHauntingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries,  PoopSky.MOD_ID);
    }

    @Override
    protected void buildRecipes(RecipeOutput p_recipeOutput, HolderLookup.Provider holderLookup) {
        RecipeOutput conditionalOutput = p_recipeOutput.withConditions(modLoaded(PoMods.CREATE.id()));

        convert(PoBlocks.POOLIME_BLOCK.get(), Blocks.ICE).register(conditionalOutput);
        convert(PoBlocks.CHILI_POOP_BLOCK.get(), Blocks.NETHERRACK).register(conditionalOutput);
    }
}
