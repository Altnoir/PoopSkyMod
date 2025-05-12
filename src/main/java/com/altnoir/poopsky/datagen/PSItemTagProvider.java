package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.tag.PSItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
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
        getOrCreateTagBuilder(PSItemTags.COMPOOPER_SAPLINGS)
                .add(Items.OAK_SAPLING)
                .add(Items.SPRUCE_SAPLING)
                .add(Items.BIRCH_SAPLING)
                .add(Items.JUNGLE_SAPLING)
                .add(Items.ACACIA_SAPLING)
                .add(Items.DARK_OAK_SAPLING)
                .add(Items.MANGROVE_PROPAGULE)
                .add(Items.CHERRY_SAPLING);
    }
}
