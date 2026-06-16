package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, PoopSky.MOD_ID);

    public static final Holder<MobEffect> FECAL_INCONTINENCE = MOB_EFFECTS.register("fecal_incontinence", () ->
            new FecalIncontinenceEffect(MobEffectCategory.HARMFUL, 0x47311A)
                    .addAttributeModifier(Attributes.GRAVITY, PoopSky.loc("fecal_incontinence"),
                            -0.0125f,
                            AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<MobEffect> INTESTINAL_SPASM = MOB_EFFECTS.register("intestinal_spasm", () ->
            new IntestinalSpasmEffect(MobEffectCategory.HARMFUL, 0x8B0000)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, PoopSky.loc("intestinal_spasm"),
                            -0.025F,
                            AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<MobEffect> ON_THE_VERGE = MOB_EFFECTS.register("on_the_verge", () ->
            new OnTheVergeEffect(MobEffectCategory.BENEFICIAL, 0x8B0000)
    );
    public static final Holder<MobEffect> OMENER = MOB_EFFECTS.register("omener", () ->
            new OmenerEffect(MobEffectCategory.BENEFICIAL, 0x47311A)
    );
;
    public static final Holder<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding", () ->
            new BleedingEffect(MobEffectCategory.HARMFUL, 0x8B0000)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
