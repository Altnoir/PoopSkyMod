package com.altnoir.poopsky.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BoneMealItem;

public class Poop extends BoneMealItem {
    public Poop(Settings settings) {
        super(settings);
    }
    public static final FoodComponent POOP = new FoodComponent.Builder()
            .nutrition(4)
            .saturationModifier(0.2F)
            .snack()
            .alwaysEdible()
            .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0), 0.1F)
            .build();
}