package com.altnoir.poopsky.impl;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.data.LegacyGeneratedResourcesGen;
import com.altnoir.poopsky.data.LootTableRandomSequenceGen;
import com.altnoir.poopsky.data.PaintingVariantTagsGen;
import com.altnoir.poopsky.data.ParticleGen;
import com.altnoir.poopsky.data.recipe.RecipeGen;
import com.altnoir.poopsky.data.sound.SoundGen;
import com.altnoir.poopsky.impl.olddata.BlockLootTableGen;
import com.altnoir.poopsky.impl.registrate.SpecialModelGen;
import com.altnoir.poopsky.impl.type.FlyTypeData;
import com.altnoir.poopsky.impl.type.ToiletTypeData;
import com.altnoir.poopsky.impl.type.damageType.DamageTypeTagsGen;
import com.tterrag.registrate.providers.RegistrateDataProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;

public class DataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider((output, registriesFuture) -> new RegistrateDataProvider(
                PoopSky.registrate(), PoopSky.MOD_ID, output, registriesFuture, false, true));
        pack.addProvider((output, registriesFuture) -> {
            return new LootTableProvider(output, Collections.emptySet(),
                    List.of(
                            new LootTableProvider.SubProviderEntry(BlockLootTableGen::new, LootContextParamSets.BLOCK)
                    ), registriesFuture);
        });
        pack.addProvider(RecipeGen::new);
        pack.addProvider((output, registriesFuture) -> new FlyTypeData(output));
        pack.addProvider((output, registriesFuture) -> new ToiletTypeData(output));
        DatapackGen datapackProvider = pack.addProvider(DatapackGen::new);
        DamageTypeTagsGen damageTypeTagsProvider = pack.addProvider((output, registriesFuture) -> {
            return new DamageTypeTagsGen(output, datapackProvider.getRegistryProvider());
        });
        pack.addProvider((output, registriesFuture) ->
                new PaintingVariantTagsGen(output, datapackProvider.getRegistryProvider()));

        pack.addProvider((output, registriesFuture) -> new SpecialModelGen(output));
        pack.addProvider(ParticleGen::new);
        pack.addProvider(SoundGen::new);
        pack.addProvider((output, registriesFuture) -> new LootTableRandomSequenceGen(output));
        pack.addProvider((output, registriesFuture) -> new LegacyGeneratedResourcesGen(output));

        // Compat
//        generators.addProvider(event.includeServer(), new PDigestingRecipeGen(packOutput, lookupProvider));
//        generators.addProvider(event.includeServer(), new PWashingRecipeGen(packOutput, lookupProvider));
//        generators.addProvider(event.includeServer(), new PHauntingRecipeGen(packOutput, lookupProvider));
    }
}
