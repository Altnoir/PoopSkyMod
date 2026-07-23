package com.altnoir.poopsky.impl;

//import com.altnoir.poopsky.impl.create.PDigestingRecipeGen;
//import com.altnoir.poopsky.impl.create.PHauntingRecipeGen;
//import com.altnoir.poopsky.impl.create.PWashingRecipeGen;
import com.altnoir.poopsky.impl.olddata.BlockLootTableGen;
import com.altnoir.poopsky.impl.olddata.FishingLootGen;
import com.altnoir.poopsky.impl.olddata.GlobalLootModifierGen;
import com.altnoir.poopsky.impl.olddata.ParticleGen;
import com.altnoir.poopsky.impl.lang.LangGen;
import com.altnoir.poopsky.impl.type.damageType.DamageTypeTagsGen;
import com.altnoir.poopsky.impl.recipe.RecipeGen;
import com.altnoir.poopsky.impl.registrate.*;
import com.altnoir.poopsky.impl.sound.SoundGen;
import com.altnoir.poopsky.impl.type.FlyTypeGen;
import com.altnoir.poopsky.impl.type.ToiletTypeGen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.neoforge.common.data.ExistingFileHelper;
//import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider((output, registriesFuture) -> {
            return new LootTableProvider(output, Collections.emptySet(),
                    List.of(
                            new LootTableProvider.SubProviderEntry(BlockLootTableGen::new, LootContextParamSets.BLOCK),
                            new LootTableProvider.SubProviderEntry(FishingLootGen::new, LootContextParamSets.FISHING)
                    ), registriesFuture);
        });
        pack.addProvider(RecipeGen::new);
        pack.addProvider((output, registriesFuture) -> new FlyTypeGen(output));
        pack.addProvider((output, registriesFuture) -> new ToiletTypeGen(output));
        DatapackGen datapackProvider = pack.addProvider(DatapackGen::new);
        DamageTypeTagsGen damageTypeTagsProvider = pack.addProvider((output, registriesFuture) -> {
            return new DamageTypeTagsGen(output, datapackProvider.getRegistryProvider());
        });

        pack.addProvider(BlockStateGen::new);
        pack.addProvider((output, registriesFuture) -> new SpecialModelGen(output));
        pack.addProvider(ParticleGen::new);
        pack.addProvider(SoundGen::new);
        pack.addProvider(LangGen::new);

        // Compat
//        generators.addProvider(event.includeServer(), new PDigestingRecipeGen(packOutput, lookupProvider));
//        generators.addProvider(event.includeServer(), new PWashingRecipeGen(packOutput, lookupProvider));
//        generators.addProvider(event.includeServer(), new PHauntingRecipeGen(packOutput, lookupProvider));
    }
}

