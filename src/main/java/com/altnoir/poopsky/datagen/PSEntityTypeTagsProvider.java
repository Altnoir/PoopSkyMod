package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.tag.PTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PSEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public PSEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(PTags.EntityTypes.IGNORES_BLEEDING)
                .add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM);

        tag(PTags.EntityTypes.RETAIN_IN_SUB_LEVEL)
                .add(PEntityType.STOOL.get())
                .add(PEntityType.TOILET.get());
        tag(PTags.EntityTypes.DESTROY_WITH_SUB_LEVEL)
                .add(PEntityType.STOOL.get())
                .add(PEntityType.TOILET.get());

        tag(EntityTypeTags.IGNORES_POISON_AND_REGEN).add(PEntityType.POOLIME.get());
    }
}
