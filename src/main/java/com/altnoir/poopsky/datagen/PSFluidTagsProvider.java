package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PFluids;
import com.altnoir.poopsky.tag.PFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PSFluidTagsProvider extends FluidTagsProvider {
    public PSFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(FluidTags.WATER)
                .add(PFluids.URINE.get())
                .add(PFluids.FLOWING_URINE.get());

        tag(PFluidTags.FAN_PROCESSING_CATALYSTS_DIGESTING)
                .add(PFluids.URINE.get())
                .add(PFluids.FLOWING_URINE.get());
    }
}