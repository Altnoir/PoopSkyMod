package com.altnoir.poopsky.component;

import com.altnoir.poopsky.effect.PSEffect;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;

public class PSFoodComponents {
    public static final FoodComponent POOP = new FoodComponent.Builder()
            .nutrition(2)
            .saturationModifier(0.2F)
            .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.1F)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20, 0), 1.0F)
            .build();

    public static final FoodComponent MAGGOTS_SEEDS = new FoodComponent.Builder()
            .nutrition(1).saturationModifier(0.1F).snack()
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0), 0.5F)
            .build();
    public static final FoodComponent BAKED_MAGGOTS = new FoodComponent.Builder()
            .nutrition(1).saturationModifier(0.4F).snack().build();
    public static final FoodComponent POOP_BREAD = new FoodComponent.Builder()
            .nutrition(6).saturationModifier(0.4F)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1800, 1), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60, 0), 1.0F)
            .build();
    public static final FoodComponent POOP_DUMPLINGS = new FoodComponent.Builder()
            .nutrition(4).saturationModifier(0.6F)
            .statusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 1800, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60, 0), 1.0F)
            .build();
    public static final FoodComponent POOP_SOUP = new FoodComponent.Builder()
            .nutrition(6).saturationModifier(0.6F)
            .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 3600, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2400, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60, 0), 1.0F)
            .usingConvertsTo(Items.BOWL)
            .build();
    public static final FoodComponent DRAGON_BREATH_CHILI = new FoodComponent.Builder()
            .nutrition(2).saturationModifier(0.1F).alwaysEdible()
            .statusEffect(new StatusEffectInstance(PSEffect.INTESTINAL_SPASM, 9600, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0), 1.0F)
            .build();

    public static final FoodComponent URINE_BOTTLE = new FoodComponent.Builder().build();
}
