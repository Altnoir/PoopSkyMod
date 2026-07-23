/*
package com.altnoir.poopsky.impl.create;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.compat.create.DigestingRecipeGen;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class PDigestingRecipeGen extends DigestingRecipeGen implements IConditionBuilder {

    public PDigestingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, PoopSky.MOD_ID);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        RecipeOutput conditionalOutput = output.withConditions(modLoaded(PoMods.CREATE.id()));

        convert(Items.GLASS_BOTTLE, PoItems.URINE_BOTTLE.get()).register(conditionalOutput);
        convert(Items.SNOWBALL, PoItems.POOP_BALL.get()).register(conditionalOutput);
        convert(Items.SNOW, PoBlocks.POOP_PIECE.get()).register(conditionalOutput);
        convert(Blocks.SNOW_BLOCK, PoBlocks.POOP_BLOCK.get()).register(conditionalOutput);
        convert(Blocks.TNT, PoBlocks.POOP_TNT.get()).register(conditionalOutput);
        convert(ItemTags.LEAVES, PoItems.FOLIUM_SENNAE.get()).register(conditionalOutput);
        convert(Items.BREAD, PoItems.POOP_BREAD.get()).register(conditionalOutput);
        convert(Items.CAKE, PoBlocks.POOP_CAKE.get()).register(conditionalOutput);
        convert(Items.POPPY, Items.WITHER_ROSE).register(conditionalOutput);
        convert(Items.POWDER_SNOW_BUCKET, PoItems.URINE_BUCKET.get()).register(conditionalOutput);
    }
}
 */