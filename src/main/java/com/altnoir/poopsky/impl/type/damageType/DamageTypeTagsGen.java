package com.altnoir.poopsky.impl.type.damageType;

import com.altnoir.poopsky.impl.PoTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DamageTypeTagsGen extends FabricTagProvider<DamageType> {
    private static final List<TagKey<DamageType>> BLEEDING_BYPASS_TAGS = List.of(
            DamageTypeTags.IS_FIRE,
            DamageTypeTags.IS_FREEZING,
            DamageTypeTags.IS_DROWNING,
            DamageTypeTags.IS_LIGHTNING,
            DamageTypeTags.IS_FALL,
            DamageTypeTags.BYPASSES_INVULNERABILITY,
            DamageTypeTags.BYPASSES_ENCHANTMENTS
    );
    private static final List<ResourceKey<DamageType>> BLEEDING_BYPASS_TYPES = List.of(
//            NeoForgeMod.POISON_DAMAGE,
            DamageTypes.INDIRECT_MAGIC,
            DamageTypes.MAGIC,
            DamageTypes.WITHER,
            DamageTypes.DRAGON_BREATH
    );

    public DamageTypeTagsGen(FabricDataOutput output, CompletableFuture<Provider> lookupProvider) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider);
    }

    @Override
    protected void addTags(@NotNull Provider provider) {
        var bypassesBleeding = tag(PoTags.DamageTypes.BYPASSES_BLEEDING);
        BLEEDING_BYPASS_TAGS.forEach(bypassesBleeding::forceAddTag);
        BLEEDING_BYPASS_TYPES.forEach(bypassesBleeding::add);

        tag(DamageTypeTags.IS_PROJECTILE)
                .add(PoDamageTypes.POOP_BALL);
    }
}
