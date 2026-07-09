package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.common.SetToiletTypeFunction;
import com.altnoir.poopsky.init.PBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class PSBlockLootTableProvider extends BlockLootSubProvider {
    protected PSBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.add(PBlocks.WOODEN_TOILET.get(), this::dropToilet);
        this.add(PBlocks.HARD_TOILET.get(), this::dropToilet);
    }

    protected LootTable.Builder dropToilet(Block block) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(block)
                                .apply(SetToiletTypeFunction.setType())));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return List.of(
                PBlocks.WOODEN_TOILET.get(),
                PBlocks.HARD_TOILET.get()
        );
    }
}