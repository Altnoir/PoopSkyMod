package com.altnoir.poopsky.impl;

import com.altnoir.poopsky.compat.create.data.PDigestingRecipeGen;
import com.altnoir.poopsky.compat.create.data.PHauntingRecipeGen;
import com.altnoir.poopsky.compat.create.data.PWashingRecipeGen;
import com.altnoir.poopsky.data.BlockStateGen;
import com.altnoir.poopsky.data.ItemModelGen;
import com.altnoir.poopsky.data.PaintingVariantTagsGen;
import com.altnoir.poopsky.data.recipe.RecipeGen;
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
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generators = event.getGenerator();
        PackOutput packOutput = generators.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generators.addProvider(event.includeServer(), new RecipeGen(packOutput, lookupProvider));
        generators.addProvider(event.includeServer(), new FlyTypeData(packOutput));
        generators.addProvider(event.includeServer(), new ToiletTypeData(packOutput));

        DatapackGen datapackProvider = new DatapackGen(packOutput, lookupProvider);
        generators.addProvider(event.includeServer(), new PaintingVariantTagsGen(packOutput, datapackProvider.getRegistryProvider()));
        DamageTypeTagsGen damageTypeTagsProvider = new DamageTypeTagsGen(packOutput, datapackProvider.getRegistryProvider());

        generators.addProvider(event.includeServer(), datapackProvider);
        generators.addProvider(event.includeServer(), damageTypeTagsProvider);

        generators.addProvider(event.includeClient(), new BlockStateGen(packOutput));
        generators.addProvider(event.includeClient(), new ItemModelGen(packOutput));
        generators.addProvider(event.includeClient(), new SoundGen(packOutput));

        // Compat
        generators.addProvider(event.includeServer(), new PDigestingRecipeGen(packOutput, lookupProvider));
        generators.addProvider(event.includeServer(), new PWashingRecipeGen(packOutput, lookupProvider));
        generators.addProvider(event.includeServer(), new PHauntingRecipeGen(packOutput, lookupProvider));
    }
}
