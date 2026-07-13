package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
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
        provider.addTag(PoTags.EntityTypes.IGNORES_BLEEDING)
                .add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM);

        provider.addTag(PoTags.EntityTypes.RETAIN_IN_SUB_LEVEL)
                .add(PoEntityType.STOOL.get(), PoEntityType.TOILET.get());
        provider.addTag(PoTags.EntityTypes.DESTROY_WITH_SUB_LEVEL)
                .add(PoEntityType.STOOL.get(), PoEntityType.TOILET.get());

        provider.addTag(EntityTypeTags.IGNORES_POISON_AND_REGEN)
                .add(PoEntityType.POOLIME.get(), PoEntityType.FLY.get());
    }
}
