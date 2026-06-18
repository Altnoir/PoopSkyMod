package com.altnoir.poopsky.tag;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.PSMods;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class PEntityTypeTags {
    public static final TagKey<EntityType<?>> IGNORES_BLEEDING = create("ignore_bleeding");

    public static final TagKey<EntityType<?>> RETAIN_IN_SUB_LEVEL = createSable("retain_in_sub_level");
    public static final TagKey<EntityType<?>> DESTROY_WITH_SUB_LEVEL = createSable("destroy_with_sub_level");

    private static TagKey<EntityType<?>> create(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, PoopSky.loc(name));
    }

    private static TagKey<EntityType<?>> createSable(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, PSMods.SABLE.rl(name));
    }
}
