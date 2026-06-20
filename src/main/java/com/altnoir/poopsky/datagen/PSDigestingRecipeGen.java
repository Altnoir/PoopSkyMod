package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.create.DigestingRecipeGen;
import com.altnoir.poopsky.item.PItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class PSDigestingRecipeGen extends DigestingRecipeGen {

    public PSDigestingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, PoopSky.MOD_ID);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        convert(PItems.POOP.get(), Items.SPONGE);
        convert(Items.GLASS_BOTTLE, PItems.URINE_BOTTLE.get());
        convert(Items.SLIME_BALL, PItems.POOP_BALL.get());
    }
}