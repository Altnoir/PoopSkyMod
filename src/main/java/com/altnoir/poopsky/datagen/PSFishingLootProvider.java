package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class PSFishingLootProvider implements LootTableSubProvider {
    public static final ResourceKey<LootTable> FISHING_SENNAE = ResourceKey.create(Registries.LOOT_TABLE,
            PoopSky.loc("fishing/seenae"));

    public PSFishingLootProvider(HolderLookup.Provider registries) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        biConsumer.accept(FISHING_SENNAE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(PSItems.FOLIUM_SENNAE.get()).setWeight(100))
                ));
    }
}
