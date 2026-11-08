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

    public static final RegistryEntry<MobEffect> FECAL_INCONTINENCE = registerMobEffect(
            "fecal_incontinence",
            () -> new FecalIncontinenceEffect(MobEffectCategory.HARMFUL, 0x47311A)
    );
    public static final RegistryEntry<MobEffect> INTESTINAL_SPASM = registerMobEffect(
            "intestinal_spasm",
            () -> new PMobEffect(MobEffectCategory.HARMFUL, 0x8B0000)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "482b2d80-22a1-367d-b9d8-7ed1b9c4fdf8",
                            -0.025F,
                            AttributeModifier.Operation.ADDITION)
    );
    public static final RegistryEntry<MobEffect> ON_THE_VERGE = registerMobEffect(
            "on_the_verge",
            () -> new OnTheVergeEffect(MobEffectCategory.BENEFICIAL, 0x8B0000)
    );
    public static final RegistryEntry<MobEffect> OMENER = registerMobEffect(
            "omener",
            () -> new PMobEffect(MobEffectCategory.BENEFICIAL, 0x47311A)
    );
    public static final RegistryEntry<MobEffect> SEEDBED_CURSE = registerMobEffect(
            "seedbed_curse",
            () -> new PMobEffect(MobEffectCategory.HARMFUL, 0x6B2020)
                    .addAttributeModifier(Attributes.MAX_HEALTH, "35c7e75a-4c26-3e6a-80f7-494662b64ed8",
                            -0.5F,
                            AttributeModifier.Operation.MULTIPLY_BASE)
    );
    public static final RegistryEntry<MobEffect> BLEEDING = registerMobEffect(
            "bleeding",
            () -> new PMobEffect(MobEffectCategory.HARMFUL, 0x8B0000)
    );
    public static final RegistryEntry<MobEffect> MOMENT_OF_PTYME = registerMobEffect(
            "moment_of_ptyme",
            () -> new MomentOfPtymeEffect(MobEffectCategory.NEUTRAL, 0xFFD700)
    );
    public static final RegistryEntry<MobEffect> INFESTATION = registerMobEffect(
            "infestation",
            () -> new InfestationEffect(MobEffectCategory.HARMFUL, 0xF7DFBA)
    );

    private static RegistryEntry<MobEffect> registerMobEffect(String name, NonNullSupplier<MobEffect> effect) {
        return REGISTRATE.simple(name, Registries.MOB_EFFECT, effect);
    }

    /** Registry order differs between loaders; attach non-vanilla attributes after all registries finish. */
    public static void attachAttributes() {
        FECAL_INCONTINENCE.get().addAttributeModifier(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get(),
                "f9299f0e-8fd6-3a5d-a6da-eeea77cfe319", -0.0125F, AttributeModifier.Operation.ADDITION);
        OMENER.get().addAttributeModifier(PoAttributes.WATER_MOVEMENT_EFFICIENCY.get(),
                "2602a0fe-3215-357c-a723-14bbe13f7ec9", 1.0F, AttributeModifier.Operation.ADDITION);
        OMENER.get().addAttributeModifier(PoAttributes.MOVEMENT_EFFICIENCY.get(),
                "44a9029b-668f-3651-a4e0-651866a06052", 1.0F, AttributeModifier.Operation.ADDITION);
    }

    public static void register() {
    }
}
