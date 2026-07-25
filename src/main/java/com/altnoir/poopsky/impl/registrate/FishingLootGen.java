package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public final class FishingLootGen {
    public static final ResourceKey<LootTable> FISHING_SENNAE = ResourceKey.create(
            Registries.LOOT_TABLE, PoopSky.loc("gameplay/fishing/seenae"));
    public static final ResourceKey<LootTable> FISHING_URINE = ResourceKey.create(
            Registries.LOOT_TABLE, PoopSky.loc("gameplay/fishing/poop_fluid"));

    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private FishingLootGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(
                ProviderType.LOOT,
                provider -> provider.addLootAction(LootContextParamSets.FISHING, FishingLootGen::generate));
    }

    private static void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(FISHING_SENNAE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(PoItems.FOLIUM_SENNAE.get()).setWeight(100))
                ));
        consumer.accept(FISHING_URINE, LootTable.lootTable()
                .withPool(
                        LootPool.lootPool().setBonusRolls(ConstantValue.exactly(1.0F))
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(PoItems.SEA_POOP_BALL.get()).setWeight(100))
                                .add(LootItem.lootTableItem(PoItems.POOP_DUMPLINGS.get()).setWeight(20))
                                .add(LootItem.lootTableItem(PoItems.POOBURGER_MEAT.get()).setWeight(20))
                                .add(LootItem.lootTableItem(PoItems.POOP_PASTA.get()).setWeight(20))
                                .add(LootItem.lootTableItem(PoItems.ROUNDWORM.get()).setWeight(10))
                                .add(LootItem.lootTableItem(Items.BAMBOO).setWeight(20))
                                .add(LootItem.lootTableItem(Items.TROPICAL_FISH).setWeight(10))
                                .add(LootItem.lootTableItem(Items.PUFFERFISH).setWeight(10))
                )
        );
    }
}
