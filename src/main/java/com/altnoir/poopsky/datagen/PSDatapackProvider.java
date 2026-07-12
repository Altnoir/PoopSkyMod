package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoDamageTypes;
import com.altnoir.poopsky.worldgen.PoConfigureFeatures;
import com.altnoir.poopsky.worldgen.PoPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PSDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUIDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, PoConfigureFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, PoPlacedFeatures::bootstrap)
            .add(Registries.DAMAGE_TYPE, PoDamageTypes::bootstrap);

    public PSDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUIDER, Set.of(PoopSky.MOD_ID));
    }
}