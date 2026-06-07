package com.altnoir.poopsky.component;

import com.altnoir.poopsky.effect.PSEffects;
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
    public static final FoodProperties GOLDEN_POOP = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 1.0F)
            .build();

    public static final FoodProperties MAGGOTS_SEEDS = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.4F).fast()
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.5F)
            .build();
    public static final FoodProperties ROUNDWORM = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.8F).fast()
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.5F)
            .build();
    public static final FoodProperties BAKED_MAGGOTS = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.8F).fast().build();
    public static final FoodProperties SAPLING_BALL = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.2F)
            .alwaysEdible()
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0), 1.0F)
            .build();

    public static final FoodProperties POOP_BREAD = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.4F)
            .effect(new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.POISON, 100, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), 1.0F)
            .build();

    public static final FoodProperties POOP_DUMPLINGS = new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.6F)
            .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 1800, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.POISON, 100, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), 1.0F)
            .build();

    public static final FoodProperties POOP_SOUP = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.6F)
            .effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.GLOWING, 2400, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), 1.0F)
            .build();

    public static final FoodProperties POOP_VEGETABLE_STICKS = new FoodProperties.Builder()
            .nutrition(5).saturationModifier(0.2F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), 1.0F)
            .build();

    public static final FoodProperties POOBURGER_MEAT = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.8F)
            .effect(new MobEffectInstance(MobEffects.POISON, 200, 0), 0.1F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 1200, 0), 0.75F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0), 1.0F)
            .build();

    public static final FoodProperties POOBURGER = new FoodProperties.Builder()
            .nutrition(18).saturationModifier(0.8F)
            .effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 1200, 1), 0.5F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0), 1.0F)
            .build();

    public static final FoodProperties POOP_PASTA = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.8F)
            .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.5F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), 1.0F)
            .build();

    public static final FoodProperties POODDING = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.1F)
            .effect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), 1.0F)
            .build();

    public static final FoodProperties DRAGON_BREATH_CHILI = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.1F).alwaysEdible()
            .effect(new MobEffectInstance(PSEffects.INTESTINAL_SPASM, 9600, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.GLOWING, 200, 0), 1.0F)
            .build();

    public static final FoodProperties URINE_BOTTLE = new FoodProperties.Builder().build();
}
