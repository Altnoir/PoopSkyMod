package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoEntityType;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public final class EntityTypeTagsGen {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private EntityTypeTagsGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, EntityTypeTagsGen::generate);
    }

    private static void generate(RegistrateTagsProvider.IntrinsicImpl<EntityType<?>> provider) {
        provider.tag(PoTags.EntityTypes.IGNORES_BLEEDING)
                .add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM);

        provider.tag(PoTags.EntityTypes.RETAIN_IN_SUB_LEVEL)
                .add(PoEntityType.STOOL.get(), PoEntityType.FLUSH_TOILET.get());
        provider.tag(PoTags.EntityTypes.DESTROY_WITH_SUB_LEVEL)
                .add(PoEntityType.STOOL.get(), PoEntityType.FLUSH_TOILET.get());

        provider.tag(EntityTypeTags.IGNORES_POISON_AND_REGEN)
                .add(PoEntityType.POOLIME.get(), PoEntityType.FLY.get());

        provider.tag(EntityTypeTags.ARTHROPOD)
                .add(PoEntityType.FLY.get());
    }
}
