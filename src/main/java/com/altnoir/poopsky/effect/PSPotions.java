package com.altnoir.poopsky.effect;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PSPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, PoopSky.MOD_ID);

    public static final Holder<Potion> FECAL_INCONTINENCE_POTION = POTIONS.register("fecal_incontinence_potion",
            () -> new Potion(new MobEffectInstance(PSEffects.FECAL_INCONTINENCE, 3600, 0))
    );
    public static final Holder<Potion> LONG_FECAL_INCONTINENCE_POTION = POTIONS.register("long_fecal_incontinence_potion",
            () -> new Potion(new MobEffectInstance(PSEffects.FECAL_INCONTINENCE, 9600, 0))
    );
    public static final Holder<Potion> STRONG_FECAL_INCONTINENCE_POTION = POTIONS.register("strong_fecal_incontinence_potion",
            () -> new Potion(new MobEffectInstance(PSEffects.FECAL_INCONTINENCE, 1800, 1))
    );
    public static final Holder<Potion> SUPER_FECAL_INCONTINENCE_POTION = POTIONS.register("super_fecal_incontinence_potion",
            () -> new Potion(
                    new MobEffectInstance(PSEffects.FECAL_INCONTINENCE, 1800, 3),
                    new MobEffectInstance(MobEffects.WEAKNESS, 1800, 1))
    );

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
