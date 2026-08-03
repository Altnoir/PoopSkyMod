package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.fabric.port.data.GlobalLootModifierProvider;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

import java.util.concurrent.CompletableFuture;

public class GlobalLootModifierGen extends GlobalLootModifierProvider {
    public GlobalLootModifierGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, PoopSky.MOD_ID);
    }

    private GlobalLootModifierGen() {
        super(PoopSky.MOD_ID);
    }

    public static void register() {
        new GlobalLootModifierGen().registerModifiers();
    }

    @Override
    protected void start() {
        add("seenae", BuiltInLootTables.FISHING, (tableBuilder, registries) -> tableBuilder.withPool(
                LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(FishingLootGen.FISHING_SENNAE))
                        .when(LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity()
                                        .subPredicate(FishingHookPredicate.inOpenWater(false))))
                        .when(LootItemRandomChanceCondition.randomChance(0.5f))));
    }
}
