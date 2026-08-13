package com.altnoir.poopsky.impl;

//import com.altnoir.poopsky.compat.create.data.PDigestingRecipeGen;
//import com.altnoir.poopsky.compat.create.data.PHauntingRecipeGen;
//import com.altnoir.poopsky.compat.create.data.PWashingRecipeGen;
import com.altnoir.poopsky.data.PaintingVariantTagsGen;
import com.altnoir.poopsky.data.sound.SoundGen;
import com.altnoir.poopsky.impl.type.FlyTypeData;
import com.altnoir.poopsky.impl.type.ToiletTypeData;
import com.altnoir.poopsky.impl.type.damageType.DamageTypeTagsGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        gatherData(event);
    }

    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event) {
        gatherData(event);
    }

    private static void gatherData(GatherDataEvent event) {
        DataGenerator generators = event.getGenerator();
        PackOutput packOutput = generators.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        event.addProvider(new FlyTypeData(packOutput));
        event.addProvider(new ToiletTypeData(packOutput));

        DatapackGen datapackProvider = new DatapackGen(packOutput, lookupProvider);
        event.addProvider(new PaintingVariantTagsGen(packOutput, datapackProvider.getRegistryProvider()));
        event.addProvider(datapackProvider);
        event.addProvider(new DamageTypeTagsGen(packOutput, datapackProvider.getRegistryProvider()));
        event.addProvider(new SoundGen(packOutput));

        // Compat
//        generators.addProvider(event.includeServer(), new PDigestingRecipeGen(packOutput, lookupProvider));
//        generators.addProvider(event.includeServer(), new PWashingRecipeGen(packOutput, lookupProvider));
//        generators.addProvider(event.includeServer(), new PHauntingRecipeGen(packOutput, lookupProvider));
    }
}
