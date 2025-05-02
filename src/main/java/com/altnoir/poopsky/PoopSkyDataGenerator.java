package com.altnoir.poopsky;

import com.altnoir.poopsky.datagen.*;
import com.altnoir.poopsky.worldgen.PSConfigureFeatures;
import com.altnoir.poopsky.worldgen.PSPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class PoopSkyDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(PSItemTagProvider::new);
		pack.addProvider(PSBlockTagProvider::new);

		pack.addProvider(PSModelProvider::new);
		pack.addProvider(PSLootTableProvider::new);
		pack.addProvider(PSRecipeProvider::new);

		pack.addProvider(PSRegistryDataGenerator::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, PSConfigureFeatures::boostrap);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, PSPlacedFeatures::bootstrap);
	}
}
