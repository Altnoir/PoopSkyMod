package com.altnoir.poopsky.impl;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.worldgen.PoConfigureFeatures;
import com.altnoir.poopsky.content.worldgen.PoPlacedFeatures;
import com.altnoir.poopsky.impl.type.PoDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackGen extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUIDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, PoConfigureFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, PoPlacedFeatures::bootstrap)
            .add(Registries.DAMAGE_TYPE, PoDamageTypes::bootstrap);

    public DatapackGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUIDER, Set.of(PoopSky.MOD_ID));
    }
}