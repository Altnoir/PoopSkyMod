package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PoPotions {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();
    private static final List<RegistryEntry<Potion, Potion>> ALL = new ArrayList<>();

    public static final RegistryEntry<Potion, Potion> FECAL_INCONTINENCE_POTION = registerPotion(
            "fecal_incontinence_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.FECAL_INCONTINENCE, 3600, 0))
    );
    public static final RegistryEntry<Potion, Potion> LONG_FECAL_INCONTINENCE_POTION = registerPotion(
            "long_fecal_incontinence_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.FECAL_INCONTINENCE, 9600, 0))
    );
    public static final RegistryEntry<Potion, Potion> STRONG_FECAL_INCONTINENCE_POTION = registerPotion(
            "strong_fecal_incontinence_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.FECAL_INCONTINENCE, 1800, 1))
    );
    public static final RegistryEntry<Potion, Potion> SUPER_FECAL_INCONTINENCE_POTION = registerPotion(
            "super_fecal_incontinence_potion",
            () -> new Potion(
                    new MobEffectInstance(PoEffects.FECAL_INCONTINENCE, 1800, 3),
                    new MobEffectInstance(MobEffects.WEAKNESS, 1800, 1))
    );

    public static final RegistryEntry<Potion, Potion> ON_THE_VGE_POTION = registerPotion(
            "on_the_verge_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.ON_THE_VERGE, 3600, 0))
    );
    public static final RegistryEntry<Potion, Potion> LONG_ON_THE_VGE_POTION = registerPotion(
            "long_on_the_verge_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.ON_THE_VERGE, 9600, 0))
    );
    public static final RegistryEntry<Potion, Potion> STRONG_ON_THE_VGE_POTION = registerPotion(
            "strong_on_the_verge_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.ON_THE_VERGE, 1800, 1))
    );

    public static final RegistryEntry<Potion, Potion> INFESTATION_POTION = registerPotion(
            "infestation_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.INFESTATION, 3600, 0))
    );
    public static final RegistryEntry<Potion, Potion> LONG_INFESTATION_POTION = registerPotion(
            "long_infestation_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.INFESTATION, 9600, 0))
    );
    public static final RegistryEntry<Potion, Potion> STRONG_INFESTATION_POTION = registerPotion(
            "strong_infestation_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.INFESTATION, 1800, 1))
    );

    private static RegistryEntry<Potion, Potion> registerPotion(String name, NonNullSupplier<Potion> potion) {
        RegistryEntry<Potion, Potion> entry = REGISTRATE.simple(name, Registries.POTION, potion);
        ALL.add(entry);
        return entry;
    }

    public static void register() {
    }

    public static List<RegistryEntry<Potion, Potion>> all() {
        return Collections.unmodifiableList(ALL);
    }
}
