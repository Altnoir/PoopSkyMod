package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class PPotions {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<Potion, Potion> FECAL_INCONTINENCE_POTION = REGISTRATE.simple(
            "fecal_incontinence_potion",
            Registries.POTION,
            () -> new Potion(new MobEffectInstance(PEffects.FECAL_INCONTINENCE, 3600, 0))
    );
    public static final RegistryEntry<Potion, Potion> LONG_FECAL_INCONTINENCE_POTION = REGISTRATE.simple(
            "long_fecal_incontinence_potion",
            Registries.POTION,
            () -> new Potion(new MobEffectInstance(PEffects.FECAL_INCONTINENCE, 9600, 0))
    );
    public static final RegistryEntry<Potion, Potion> STRONG_FECAL_INCONTINENCE_POTION = REGISTRATE.simple(
            "strong_fecal_incontinence_potion",
            Registries.POTION,
            () -> new Potion(new MobEffectInstance(PEffects.FECAL_INCONTINENCE, 1800, 1))
    );
    public static final RegistryEntry<Potion, Potion> SUPER_FECAL_INCONTINENCE_POTION = REGISTRATE.simple(
            "super_fecal_incontinence_potion",
            Registries.POTION,
            () -> new Potion(
                    new MobEffectInstance(PEffects.FECAL_INCONTINENCE, 1800, 3),
                    new MobEffectInstance(MobEffects.WEAKNESS, 1800, 1))
    );

    public static final RegistryEntry<Potion, Potion> ON_THE_VGE_POTION = REGISTRATE.simple(
            "on_the_verge_potion",
            Registries.POTION,
            () -> new Potion(new MobEffectInstance(PEffects.ON_THE_VERGE, 3600, 0))
    );
    public static final RegistryEntry<Potion, Potion> LONG_ON_THE_VGE_POTION = REGISTRATE.simple(
            "long_on_the_verge_potion",
            Registries.POTION,
            () -> new Potion(new MobEffectInstance(PEffects.ON_THE_VERGE, 9600, 0))
    );
    public static final RegistryEntry<Potion, Potion> STRONG_ON_THE_VGE_POTION = REGISTRATE.simple(
            "strong_on_the_verge_potion",
            Registries.POTION,
            () -> new Potion(new MobEffectInstance(PEffects.ON_THE_VERGE, 1800, 1))
    );

    public static void register() {
    }
}