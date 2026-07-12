package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class PBlockLootTableProvider extends BlockLootSubProvider {
    protected PBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.add(PoBlocks.WOODEN_TOILET.get(), PoBlocks.createToiletDrop(PoBlocks.WOODEN_TOILET.get()));
        this.add(PoBlocks.HARD_TOILET.get(), PoBlocks.createToiletDrop(PoBlocks.HARD_TOILET.get()));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return List.of(
                PoBlocks.WOODEN_TOILET.get(),
                PoBlocks.HARD_TOILET.get()
        );
    }
}
