package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PBlocks;
import com.altnoir.poopsky.compat.create.DigestingRecipeGen;
import com.altnoir.poopsky.item.PItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class PSDigestingRecipeProvider extends DigestingRecipeGen {

    public PSDigestingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, PoopSky.MOD_ID);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        convert(Items.GLASS_BOTTLE, PItems.URINE_BOTTLE.get()).register(output);
        convert(Items.SNOWBALL, PItems.POOP_BALL.get()).register(output);
        convert(Blocks.TNT, PBlocks.POOP_TNT.get()).register(output);
        convert(ItemTags.LEAVES, PItems.FOLIUM_SENNAE.get()).register(output);
        convert(Items.BREAD, PItems.POOP_BREAD.get()).register(output);
        convert(Items.POPPY, Items.WITHER_ROSE).register(output);
    }
}
