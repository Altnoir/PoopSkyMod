package com.altnoir.poopsky.effect;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class PSEffect {

    public static final RegistryEntry<StatusEffect> FECAL_INCONTINENCE = registerStatusEffect("fecal_incontinence",
            new FecalIncontinenceEffect(StatusEffectCategory.HARMFUL, 0x47311A)
                    .addAttributeModifier(EntityAttributes.GENERIC_JUMP_STRENGTH,
                            Identifier.of(PoopSky.MOD_ID, "fecal_incontinence"), -0.1F,
                            EntityAttributeModifier.Operation.ADD_VALUE)
    );

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(PoopSky.MOD_ID, name), statusEffect);
    }
    public static void registerEffects() {
        PoopSky.LOGGER.info("Registering Effects for " + PoopSky.MOD_ID);
    }
}
