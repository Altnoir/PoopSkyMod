package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.worldgen.PSConfigureFeatures;
import com.altnoir.poopsky.worldgen.PSPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PSDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUIDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, PSConfigureFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, PSPlacedFeatures::bootstrap);

    public PSDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUIDER, Set.of(PoopSky.MOD_ID));
    }
}
