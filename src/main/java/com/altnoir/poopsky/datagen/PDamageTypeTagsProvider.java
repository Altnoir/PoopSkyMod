package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.tag.PDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PDamageTypeTagsProvider extends TagsProvider<DamageType> {
    protected PDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(PDamageTypeTags.BYPASSES_BLEEDING)
                .addTags(
                        DamageTypeTags.IS_FIRE,
                        DamageTypeTags.IS_FREEZING,
                        DamageTypeTags.IS_DROWNING,
                        DamageTypeTags.IS_LIGHTNING,
                        DamageTypeTags.IS_FALL,
                        DamageTypeTags.BYPASSES_INVULNERABILITY,
                        DamageTypeTags.BYPASSES_ENCHANTMENTS
                )
                .add(
                        NeoForgeMod.POISON_DAMAGE,
                        DamageTypes.INDIRECT_MAGIC,
                        DamageTypes.MAGIC,
                        DamageTypes.WITHER,
                        DamageTypes.DRAGON_BREATH
                );
    }
}
