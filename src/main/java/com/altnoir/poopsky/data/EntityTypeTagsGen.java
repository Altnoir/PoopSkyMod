package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.abysslib.reginth.Reginth;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.abysslib.reginth.providers.ProviderType;
import com.altnoir.abysslib.reginth.providers.ReginthTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public final class EntityTypeTagsGen {
    private static final Reginth REGISTRATE = PoopSky.registrate();

    private EntityTypeTagsGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, EntityTypeTagsGen::generate);
    }

    private static void generate(ReginthTagsProvider.IntrinsicImpl<EntityType<?>> provider) {
        provider.addTag(PoTags.EntityTypes.GACHAPON_MOB)
                .add(
                        EntityType.COW,
                        EntityType.MOOSHROOM,
                        EntityType.SHEEP,
                        EntityType.PIG,
                        EntityType.CHICKEN,
                        EntityType.PANDA,
                        EntityType.POLAR_BEAR,
                        EntityType.CAMEL,
                        EntityType.LLAMA,
                        EntityType.DONKEY,
                        EntityType.MULE,
                        EntityType.GOAT,
                        EntityType.HORSE,
                        EntityType.RABBIT,
                        EntityType.COD,
                        EntityType.SALMON,
                        EntityType.TROPICAL_FISH,
                        EntityType.PUFFERFISH,
                        EntityType.TURTLE,
                        EntityType.DOLPHIN,
                        EntityType.BEE,
                        EntityType.OCELOT,
                        EntityType.CAT,
                        EntityType.WOLF,
                        EntityType.FOX,
                        EntityType.FROG,
                        EntityType.ALLAY,
                        EntityType.PARROT,
                        EntityType.ARMADILLO,
                        EntityType.AXOLOTL
                );
        provider.addTag(PoTags.EntityTypes.IGNORES_BLEEDING)
                .add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM);

        provider.addTag(PoTags.EntityTypes.RETAIN_IN_SUB_LEVEL)
                .add(PoEntityType.STOOL.get(), PoEntityType.FLUSH_TOILET.get());
        provider.addTag(PoTags.EntityTypes.DESTROY_WITH_SUB_LEVEL)
                .add(PoEntityType.STOOL.get(), PoEntityType.FLUSH_TOILET.get());

        provider.addTag(EntityTypeTags.IGNORES_POISON_AND_REGEN)
                .add(PoEntityType.POOLIME.get(), PoEntityType.FLY.get());

        provider.addTag(EntityTypeTags.ARTHROPOD)
                .add(PoEntityType.FLY.get());
    }
}