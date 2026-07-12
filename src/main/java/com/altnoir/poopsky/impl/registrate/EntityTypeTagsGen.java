package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.impl.PoTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagsGen extends EntityTypeTagsProvider {
    public EntityTypeTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(PoTags.EntityTypes.IGNORES_BLEEDING)
                .add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM);

        tag(PoTags.EntityTypes.RETAIN_IN_SUB_LEVEL)
                .add(PoEntityType.STOOL.get(), PoEntityType.TOILET.get());
        tag(PoTags.EntityTypes.DESTROY_WITH_SUB_LEVEL)
                .add(PoEntityType.STOOL.get(), PoEntityType.TOILET.get());

        tag(EntityTypeTags.IGNORES_POISON_AND_REGEN)
                .add(PoEntityType.POOLIME.get(), PoEntityType.FLY.get());
    }
}
