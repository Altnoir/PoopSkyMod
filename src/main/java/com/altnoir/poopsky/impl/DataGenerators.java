package com.altnoir.poopsky.impl;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.create.PDigestingRecipeGen;
import com.altnoir.poopsky.impl.create.PHauntingRecipeGen;
import com.altnoir.poopsky.impl.create.PWashingRecipeGen;
import com.altnoir.poopsky.impl.registrate.*;
import com.altnoir.poopsky.impl.sound.SoundGen;
import com.altnoir.poopsky.impl.type.FlyTypeGen;
import com.altnoir.poopsky.impl.type.ToiletTypeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generators = event.getGenerator();
        PackOutput packOutput = generators.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generators.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(
                        new LootTableProvider.SubProviderEntry(BlockLootTableGen::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(FishingLootGen::new, LootContextParamSets.FISHING)
                ), lookupProvider));
        generators.addProvider(event.includeServer(), new RecipeGen(packOutput, lookupProvider));
        generators.addProvider(event.includeServer(), new FlyTypeGen(packOutput));
        generators.addProvider(event.includeServer(), new ToiletTypeGen(packOutput));

        DatapackGen datapackProvider = new DatapackGen(packOutput, lookupProvider);
        DamageTypeTagsGen damageTypeTagsProvider = new DamageTypeTagsGen(packOutput, datapackProvider.getRegistryProvider(), existingFileHelper);

        generators.addProvider(event.includeServer(), datapackProvider);
        generators.addProvider(event.includeServer(), damageTypeTagsProvider);

        generators.addProvider(event.includeServer(), new GlobalLootModifierGen(packOutput, lookupProvider));

        generators.addProvider(event.includeClient(), new BlockStateGen(PoopSky.registrate(), packOutput, existingFileHelper));
        generators.addProvider(event.includeClient(), new ItemModelGen(PoopSky.registrate(), packOutput, existingFileHelper));
        generators.addProvider(event.includeClient(), new ParticleGen(packOutput, existingFileHelper));
        generators.addProvider(event.includeClient(), new SoundGen(packOutput, existingFileHelper));

        // Compat
        generators.addProvider(event.includeServer(), new PDigestingRecipeGen(packOutput, lookupProvider));
        generators.addProvider(event.includeServer(), new PWashingRecipeGen(packOutput, lookupProvider));
        generators.addProvider(event.includeServer(), new PHauntingRecipeGen(packOutput, lookupProvider));
    }
}

