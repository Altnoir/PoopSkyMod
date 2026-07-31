package com.altnoir.poopsky.impl;

import com.altnoir.poopsky.compat.create.datagen.PDigestingRecipeGen;
import com.altnoir.poopsky.compat.create.datagen.PHauntingRecipeGen;
import com.altnoir.poopsky.compat.create.datagen.PWashingRecipeGen;
import com.altnoir.poopsky.data.recipe.RecipeGen;
import com.altnoir.poopsky.data.BlockStateGen;
import com.altnoir.poopsky.data.ItemModelGen;
import com.altnoir.poopsky.data.PaintingVariantTagsGen;
import com.altnoir.poopsky.data.sound.SoundGen;
import com.altnoir.poopsky.impl.type.FlyTypeData;
import com.altnoir.poopsky.impl.type.ToiletTypeData;
import com.altnoir.poopsky.impl.type.damageType.DamageTypeTagsGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generators = event.getGenerator();
        PackOutput packOutput = generators.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generators.addProvider(event.includeServer(), new RecipeGen(packOutput, lookupProvider));
        generators.addProvider(event.includeServer(), new FlyTypeData(packOutput));
        generators.addProvider(event.includeServer(), new ToiletTypeData(packOutput));

        DatapackGen datapackProvider = new DatapackGen(packOutput, lookupProvider);
        generators.addProvider(event.includeServer(), new PaintingVariantTagsGen(packOutput, datapackProvider.getRegistryProvider(), existingFileHelper));
        DamageTypeTagsGen damageTypeTagsProvider = new DamageTypeTagsGen(packOutput, datapackProvider.getRegistryProvider(), existingFileHelper);

        generators.addProvider(event.includeServer(), datapackProvider);
        generators.addProvider(event.includeServer(), damageTypeTagsProvider);

        generators.addProvider(event.includeClient(), new BlockStateGen(packOutput, existingFileHelper));
        generators.addProvider(event.includeClient(), new ItemModelGen(packOutput, existingFileHelper));
        generators.addProvider(event.includeClient(), new SoundGen(packOutput, existingFileHelper));

        // Compat
        generators.addProvider(event.includeServer(), new PDigestingRecipeGen(packOutput, lookupProvider));
        generators.addProvider(event.includeServer(), new PWashingRecipeGen(packOutput, lookupProvider));
        generators.addProvider(event.includeServer(), new PHauntingRecipeGen(packOutput, lookupProvider));
    }
}
