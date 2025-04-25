package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class PSItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public PSItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ConventionalItemTags.FOOD_POISONING_FOODS)
                .add(PSItems.POOP);
        getOrCreateTagBuilder(ItemTags.SAPLINGS)
                .add(PSBlocks.POOP_SAPLING.asItem());
        getOrCreateTagBuilder(ItemTags.LEAVES)
                .add(PSBlocks.POOP_LEAVES.asItem());
    }
}
