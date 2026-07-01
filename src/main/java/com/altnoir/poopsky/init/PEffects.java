package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.effect.FecalIncontinenceEffect;
import com.altnoir.poopsky.common.effect.MomentOfPtymeEffect;
import com.altnoir.poopsky.common.effect.OnTheVergeEffect;
import com.altnoir.poopsky.common.effect.PMobEffect;
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
            new PMobEffect(MobEffectCategory.HARMFUL, 0x8B0000)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, PoopSky.loc("intestinal_spasm"),
                            -0.025F,
                            AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<MobEffect> ON_THE_VERGE = MOB_EFFECTS.register("on_the_verge", () ->
            new OnTheVergeEffect(MobEffectCategory.BENEFICIAL, 0x8B0000)
    );
    public static final Holder<MobEffect> OMENER = MOB_EFFECTS.register("omener", () ->
            new PMobEffect(MobEffectCategory.BENEFICIAL, 0x47311A)
    );
    public static final Holder<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding", () ->
            new PMobEffect(MobEffectCategory.HARMFUL, 0x8B0000)
    );
    public static final Holder<MobEffect> MOMENT_OF_PTYME = MOB_EFFECTS.register("moment_of_ptyme", () ->
            new MomentOfPtymeEffect(MobEffectCategory.NEUTRAL, 0xFFD700)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
