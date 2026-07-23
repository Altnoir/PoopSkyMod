package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class PoPotions {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<Potion, Potion> FECAL_INCONTINENCE_POTION = registerPotion(
            "fecal_incontinence_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.holder(PoEffects.FECAL_INCONTINENCE), 3600, 0))
    );
    public static final RegistryEntry<Potion, Potion> LONG_FECAL_INCONTINENCE_POTION = registerPotion(
            "long_fecal_incontinence_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.holder(PoEffects.FECAL_INCONTINENCE), 9600, 0))
    );
    public static final RegistryEntry<Potion, Potion> STRONG_FECAL_INCONTINENCE_POTION = registerPotion(
            "strong_fecal_incontinence_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.holder(PoEffects.FECAL_INCONTINENCE), 1800, 1))
    );
    public static final RegistryEntry<Potion, Potion> SUPER_FECAL_INCONTINENCE_POTION = registerPotion(
            "super_fecal_incontinence_potion",
            () -> new Potion(
                    new MobEffectInstance(PoEffects.holder(PoEffects.FECAL_INCONTINENCE), 1800, 3),
                    new MobEffectInstance(MobEffects.WEAKNESS, 1800, 1))
    );

    public static final RegistryEntry<Potion, Potion> ON_THE_VGE_POTION = registerPotion(
            "on_the_verge_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.holder(PoEffects.ON_THE_VERGE), 3600, 0))
    );
    public static final RegistryEntry<Potion, Potion> LONG_ON_THE_VGE_POTION = registerPotion(
            "long_on_the_verge_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.holder(PoEffects.ON_THE_VERGE), 9600, 0))
    );
    public static final RegistryEntry<Potion, Potion> STRONG_ON_THE_VGE_POTION = registerPotion(
            "strong_on_the_verge_potion",
            () -> new Potion(new MobEffectInstance(PoEffects.holder(PoEffects.ON_THE_VERGE), 1800, 1))
    );

    private static RegistryEntry<Potion, Potion> registerPotion(String name, NonNullSupplier<Potion> potion) {
        return REGISTRATE.simple(name, Registries.POTION, potion);
    }

    public static void register() {
    }
}
