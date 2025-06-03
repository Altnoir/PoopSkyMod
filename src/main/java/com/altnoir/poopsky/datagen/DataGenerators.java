package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = PoopSky.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generators = event.getGenerator();
        PackOutput packOutput = generators.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generators.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(PSBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
        generators.addProvider(event.includeServer(), new PSRecipeProvider(packOutput, lookupProvider));

        BlockTagsProvider blockTagsProvider = new PSBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        ItemTagsProvider itemTagsProvider = new PSItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper);
        generators.addProvider(event.includeServer(), blockTagsProvider);
        generators.addProvider(event.includeServer(), itemTagsProvider);

        generators.addProvider(event.includeServer(), new PSDatapackProvider(packOutput, lookupProvider));

        generators.addProvider(event.includeClient(), new PSBlockStateProvider(packOutput, existingFileHelper));
        generators.addProvider(event.includeClient(), new PSItemModelProvider(packOutput, existingFileHelper));
    }
}
