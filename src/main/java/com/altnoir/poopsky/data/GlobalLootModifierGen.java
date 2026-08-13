package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.FishingHookPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public final class GlobalLootModifierGen extends GlobalLootModifierProvider {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private GlobalLootModifierGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, PoopSky.MOD_ID);
    }

    public static void register() {
        REGISTRATE.addDataGenerator(
                ProviderType.GENERIC_SERVER,
                provider -> provider.add(data -> new GlobalLootModifierGen(data.output(), data.registries())));
    }

    @Override
    protected void start() {
        add("seenae",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(
                                        PoopSky.mcloc("gameplay/fishing")
                                ).build(),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity()
                                                .subPredicate(FishingHookPredicate.inOpenWater(false))
                                ).build(),
                                LootItemRandomChanceCondition.randomChance(0.5f).build()
                        }, 0,
                        FishingLootGen.FISHING_SENNAE
                ));
    }
}
