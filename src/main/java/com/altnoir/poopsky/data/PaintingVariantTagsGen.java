package com.altnoir.poopsky.data;

import com.altnoir.poopsky.init.PoPainting;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.tags.PaintingVariantTags;

import java.util.concurrent.CompletableFuture;

public class PaintingVariantTagsGen extends PaintingVariantTagsProvider {

    public PaintingVariantTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(PaintingVariantTags.PLACEABLE)
                .add(
                        PoPainting.POOP,
                        PoPainting.POOP_KING,
                        PoPainting.TOILET,
                        PoPainting.VIP
                );
    }
}
