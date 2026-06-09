package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.tag.PSSableTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PSEntityTypeTagProvider extends EntityTypeTagsProvider {
    public PSEntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(PSSableTags.RETAIN_IN_SUB_LEVEL)
                .add(PEntityType.TOILET.get());
        tag(PSSableTags.DESTROY_WITH_SUB_LEVEL)
                .add(PEntityType.TOILET.get());
    }
}
