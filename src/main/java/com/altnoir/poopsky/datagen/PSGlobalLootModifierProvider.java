package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class PSGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public PSGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, PoopSky.MOD_ID);
    }

    @Override
    protected void start() {
        add("seenae",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(
                                        ResourceLocation.withDefaultNamespace("gameplay/fishing")
                                ).build(),
                                LootItemRandomChanceCondition.randomChance(0.5f).build()
                        },
                        PSFishingLootProvider.FISHING_SENNAE
                ));

//        add("seenae_from_open",
//                new AddTableLootModifier(
//                        new LootItemCondition[]{
//                                LootTableIdCondition.builder(
//                                        ResourceLocation.withDefaultNamespace("gameplay/fishing")
//                                ).build(),
//                                LootItemEntityPropertyCondition.hasProperties(
//                                        LootContext.EntityTarget.THIS,
//                                        EntityPredicate.Builder.entity()
//                                                .subPredicate(FishingHookPredicate.inOpenWater(true))
//                                ).build(),
//                                LootItemRandomChanceCondition.randomChance(0.125f).build()
//                        },
//                        PSFishingLootProvider.FISHING_SENNAE
//                ));
    }
}
