package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.init.PBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class PBlockLootTableProvider extends BlockLootSubProvider {
    protected PBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.<Item>of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.add(PBlocks.WOODEN_TOILET.get(), PBlocks.createToiletDrop(PBlocks.WOODEN_TOILET.get()));
        this.add(PBlocks.HARD_TOILET.get(), PBlocks.createToiletDrop(PBlocks.HARD_TOILET.get()));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return List.of(
                PBlocks.WOODEN_TOILET.get(),
                PBlocks.HARD_TOILET.get()
        );
    }
}
