package com.altnoir.poopsky.component;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class PFoods {
    public static final FoodProperties POOP = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.2F)
            .effect(new MobEffectInstance(MobEffects.POISON, 100, 0), 0.1F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0), 1.0F)
            .build();

    public static final FoodProperties POOP_DUMPLINGS = new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.6F)
            .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 1800, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.POISON, 100, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), 1.0F)
            .build();

    public static final FoodProperties URINE_BOTTLE = new FoodProperties.Builder().build();
}
