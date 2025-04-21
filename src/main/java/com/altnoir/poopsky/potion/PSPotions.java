package com.altnoir.poopsky.potion;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.effect.PSEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class PSPotions {

    public static final RegistryEntry<Potion> FECAL_INCONTINENCE_POTION = registerPotion("fecal_incontinence_potion",
            new Potion(new StatusEffectInstance(PSEffect.FECAL_INCONTINENCE, 3600, 0))
    );
    private static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(PoopSky.MOD_ID, name), potion);
    }
    public static void registerPotions() {
        PoopSky.LOGGER.info("Registering Potions for " + PoopSky.MOD_ID);
    }
}
