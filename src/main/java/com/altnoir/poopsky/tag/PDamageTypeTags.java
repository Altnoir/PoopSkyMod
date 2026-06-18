package com.altnoir.poopsky.tag;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public interface PDamageTypeTags {
    TagKey<DamageType> BYPASSES_BLEEDING = create("bypass_bleeding");

    private static TagKey<DamageType> create(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, PoopSky.loc(name));
    }
}
