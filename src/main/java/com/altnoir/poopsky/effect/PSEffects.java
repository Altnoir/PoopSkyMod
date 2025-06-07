package com.altnoir.poopsky.effect;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PSEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, PoopSky.MOD_ID);

    public static final Holder<MobEffect> FECAL_INCONTINENCE = MOB_EFFECTS.register("fecal_incontinence", () ->
                    new FecalIncontinenceEffect(
                            MobEffectCategory.HARMFUL,
                            0x47311A)
                            .addAttributeModifier(Attributes.GRAVITY,
                                    ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "fecal_incontinence"),
                                    -0.025f,
                                    AttributeModifier.Operation.ADD_VALUE)
    );

    public static final Holder<MobEffect> INTESTINAL_SPASM = MOB_EFFECTS.register("intestinal_spasm", () ->
                    new IntestinalSpasmEffect(
                            MobEffectCategory.HARMFUL,
                            0x8B0000)
                            .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                    ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "intestinal_spasm"),
                                    -0.025F,
                                    AttributeModifier.Operation.ADD_VALUE)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
