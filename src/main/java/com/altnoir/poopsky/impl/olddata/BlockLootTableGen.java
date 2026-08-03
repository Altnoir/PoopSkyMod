package com.altnoir.poopsky.impl.olddata;

import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Set;
import java.util.function.BiConsumer;

public class BlockLootTableGen extends BlockLootSubProvider {
    public BlockLootTableGen(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        this.add(PoBlocks.WOODEN_TOILET.get(), PoBlocks.createToiletDrop(PoBlocks.WOODEN_TOILET.get()));
        this.add(PoBlocks.HARD_TOILET.get(), PoBlocks.createToiletDrop(PoBlocks.HARD_TOILET.get()));
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        generate();
        map.forEach(output);
    }
}
