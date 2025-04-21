package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.Fluid.PSFluids;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FluidTags;

import java.util.concurrent.CompletableFuture;

public class PSFluidTagProvider extends FabricTagProvider.FluidTagProvider {
    public PSFluidTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(FluidTags.WATER)
                .add(PSFluids.URINE)
                .add(PSFluids.FLOWING_URINE);

    }
}
