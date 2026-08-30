package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.effect.*;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PoEffects {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<MobEffect, MobEffect> FECAL_INCONTINENCE = registerMobEffect(
            "fecal_incontinence",
            () -> new FecalIncontinenceEffect(MobEffectCategory.HARMFUL, 0x47311A)
                    .addAttributeModifier(Attributes.GRAVITY, PoopSky.loc("fecal_incontinence"),
                            -0.0125f,
                            AttributeModifier.Operation.ADD_VALUE)
    );
    public static final RegistryEntry<MobEffect, MobEffect> INTESTINAL_SPASM = registerMobEffect(
            "intestinal_spasm",
            () -> new PMobEffect(MobEffectCategory.HARMFUL, 0x8B0000)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, PoopSky.loc("intestinal_spasm"),
                            -0.025F,
                            AttributeModifier.Operation.ADD_VALUE)
    );
    public static final RegistryEntry<MobEffect, MobEffect> ON_THE_VERGE = registerMobEffect(
            "on_the_verge",
            () -> new OnTheVergeEffect(MobEffectCategory.BENEFICIAL, 0x8B0000)
    );
    public static final RegistryEntry<MobEffect, MobEffect> OMENER = registerMobEffect(
            "omener",
            () -> new PMobEffect(MobEffectCategory.BENEFICIAL, 0x47311A)
                    .addAttributeModifier(Attributes.WATER_MOVEMENT_EFFICIENCY, PoopSky.loc("omener"), 1.0F,
                            AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.MOVEMENT_EFFICIENCY, PoopSky.loc("omener_block"), 1.0F,
                            AttributeModifier.Operation.ADD_VALUE)
    );
    public static final RegistryEntry<MobEffect, MobEffect> SEEDBED_CURSE = registerMobEffect(
            "seedbed_curse",
            () -> new PMobEffect(MobEffectCategory.HARMFUL, 0x6B2020)
                    .addAttributeModifier(Attributes.MAX_HEALTH, PoopSky.loc("seedbed_curse"),
                            -0.5F,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final RegistryEntry<MobEffect, MobEffect> BLEEDING = registerMobEffect(
            "bleeding",
            () -> new PMobEffect(MobEffectCategory.HARMFUL, 0x8B0000)
    );
    public static final RegistryEntry<MobEffect, MobEffect> MOMENT_OF_PTYME = registerMobEffect(
            "moment_of_ptyme",
            () -> new MomentOfPtymeEffect(MobEffectCategory.NEUTRAL, 0xFFD700)
    );
    public static final RegistryEntry<MobEffect, MobEffect> INFESTATION = registerMobEffect(
            "infestation",
            () -> new InfestationEffect(MobEffectCategory.HARMFUL, 0xF7DFBA)
    );
    public static final RegistryEntry<MobEffect, MobEffect> DEATH_BLIGHT = registerMobEffect(
            "death_blight",
            DeathBlightEffect::new
    );

    private static RegistryEntry<MobEffect, MobEffect> registerMobEffect(String name, NonNullSupplier<MobEffect> effect) {
        return REGISTRATE.simple(name, Registries.MOB_EFFECT, effect);
    }

    public static void register() {
    }
}
