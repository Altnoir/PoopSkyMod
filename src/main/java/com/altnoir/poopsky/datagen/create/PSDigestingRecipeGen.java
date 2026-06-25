package com.altnoir.poopsky.datagen.create;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PBlocks;
import com.altnoir.poopsky.compat.PSMods;
import com.altnoir.poopsky.compat.create.DigestingRecipeGen;
import com.altnoir.poopsky.item.PItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class PSDigestingRecipeGen extends DigestingRecipeGen implements IConditionBuilder {

    public PSDigestingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, PoopSky.MOD_ID);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        RecipeOutput conditionalOutput = output.withConditions(modLoaded(PSMods.CREATE.id()));

        convert(Items.GLASS_BOTTLE, PItems.URINE_BOTTLE.get()).register(conditionalOutput);
        convert(Items.SNOWBALL, PItems.POOP_BALL.get()).register(conditionalOutput);
        convert(Items.SNOW, PBlocks.POOP_PIECE.get()).register(conditionalOutput);
        convert(Blocks.SNOW_BLOCK, PBlocks.POOP_BLOCK.get()).register(conditionalOutput);
        convert(Blocks.TNT, PBlocks.POOP_TNT.get()).register(conditionalOutput);
        convert(ItemTags.LEAVES, PItems.FOLIUM_SENNAE.get()).register(conditionalOutput);
        convert(Items.BREAD, PItems.POOP_BREAD.get()).register(conditionalOutput);
        convert(Items.CAKE, PBlocks.POOP_CAKE.get()).register(conditionalOutput);
        convert(Items.POPPY, Items.WITHER_ROSE).register(conditionalOutput);
        convert(Items.POWDER_SNOW_BUCKET, PItems.URINE_BUCKET.get()).register(conditionalOutput);
    }
}