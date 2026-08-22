package com.altnoir.poopsky.content.item;

import com.altnoir.poopsky.init.PoEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PFoods {
    private static final Map<FoodProperties, FoodMetadata> METADATA = new IdentityHashMap<>();

    public static Item.Properties apply(Item.Properties properties, FoodProperties food) {
        FoodMetadata metadata = METADATA.get(food);
        properties.food(food, metadata == null ? Consumable.builder().build() : metadata.consumable());
        if (metadata != null && metadata.remainder() != null) {
            properties.usingConvertsTo(metadata.remainder());
        }
        return properties;
    }
    public static final FoodProperties POOP = new Builder()
            .nutrition(2)
            .saturationModifier(0.2F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 100), 0.1F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 300), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .build();
    public static final FoodProperties GOLDEN_POOP = new Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 300), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200), 1.0F)
            .build();

    public static final FoodProperties MAGGOTS_SEEDS = new Builder()
            .nutrition(1).saturationModifier(0.4F).fast()
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 200), 0.5F)
            .build();
    public static final FoodProperties ROUNDWORM = new Builder()
            .nutrition(1).saturationModifier(0.8F).fast()
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 200), 0.5F)
            .build();
    public static final FoodProperties BAKED_MAGGOTS = new Builder()
            .nutrition(1).saturationModifier(0.8F).fast().build();
    public static final FoodProperties SAPLING_BALL = new Builder()
            .nutrition(2)
            .saturationModifier(0.2F)
            .alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 300), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .build();

    public static final FoodProperties POOP_BREAD = new Builder()
            .nutrition(6).saturationModifier(0.4F)
            .effect(() -> new MobEffectInstance(MobEffects.HASTE, 1800, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 100), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 200), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 60), 1.0F)
            .build();

    public static final FoodProperties POOP_DUMPLINGS = new Builder()
            .nutrition(4).saturationModifier(0.6F)
            .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 1800), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 100), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 200), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 60), 1.0F)
            .build();
    public static final FoodProperties POOP_MOONCAKE = new Builder()
            .nutrition(4).saturationModifier(0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 200), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 60), 1.0F)
            .build();
    public static final FoodProperties CHILI_POOP_MOONCAKE = new Builder()
            .nutrition(4).saturationModifier(0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2400), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 200), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 60), 1.0F)
            .build();
    public static final FoodProperties GOLDEN_POOP_MOONCAKE = new Builder()
            .nutrition(4).saturationModifier(0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 200), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 60), 1.0F)
            .build();

    public static final FoodProperties POOP_SOUP = new Builder()
            .nutrition(6).saturationModifier(0.6F)
            .usingConvertsTo(Items.BOWL)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.GLOWING, 2400), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 200), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 60), 1.0F)
            .build();

    public static final FoodProperties POOP_VEGETABLE_STICKS = new Builder()
            .nutrition(5).saturationModifier(0.2F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 200), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 60), 1.0F)
            .build();

    public static final FoodProperties POOBURGER_MEAT = new Builder()
            .nutrition(3).saturationModifier(0.4F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200), 0.1F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 1200), 0.75F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 40), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 120), 1.0F)
            .build();

    public static final FoodProperties POOBURGER = new Builder()
            .nutrition(8).saturationModifier(0.8F)
            .effect(() -> new MobEffectInstance(PoEffects.SEEDBED_CURSE, 3600), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.RESISTANCE, 2400), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 1200, 1), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 40), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 120), 1.0F)
            .build();

    public static final FoodProperties POOPSICLE = new Builder()
            .nutrition(4).saturationModifier(0.6F)
            .effect(() -> new MobEffectInstance(PoEffects.SEEDBED_CURSE, 3600), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 1200, 1), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 40), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 120), 1.0F)
            .build();

    public static final FoodProperties POOP_PASTA = new Builder()
            .nutrition(2).saturationModifier(0.4F)
            .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 200), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.NAUSEA, 200), 0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 60), 1.0F)
            .build();

    public static final FoodProperties POODDING = new Builder()
            .nutrition(3).saturationModifier(0.5F)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 20), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 60), 1.0F)
            .build();

    public static final FoodProperties DRAGON_BREATH_CHILI = new Builder()
            .nutrition(2).saturationModifier(0.1F).alwaysEdible()
            .effect(() -> new MobEffectInstance(PoEffects.INTESTINAL_SPASM, 9600), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.GLOWING, 200), 1.0F)
            .build();
    public static final FoodProperties KING_OF_DRAGON_FRUIT = new Builder()
            .nutrition(4).saturationModifier(0.3F).fast().alwaysEdible().build();
    public static final FoodProperties FASTING_PILL = new Builder()
            .nutrition(0)
            .saturationModifier(0.0F)
            .alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 24000), 1.0F)
            .build();

    public static final FoodProperties URINE_BOTTLE = new Builder().build();

    private record FoodMetadata(Consumable consumable, Item remainder) {
    }

    private static final class Builder {
        private final FoodProperties.Builder food = new FoodProperties.Builder();
        private final List<ApplyStatusEffectsConsumeEffect> effects = new ArrayList<>();
        private float consumeSeconds = 1.6F;
        private Item remainder;

        private Builder nutrition(int nutrition) {
            food.nutrition(nutrition);
            return this;
        }

        private Builder saturationModifier(float modifier) {
            food.saturationModifier(modifier);
            return this;
        }

        private Builder alwaysEdible() {
            food.alwaysEdible();
            return this;
        }

        private Builder fast() {
            consumeSeconds = 0.8F;
            return this;
        }

        private Builder effect(Supplier<MobEffectInstance> effect, float probability) {
            effects.add(new ApplyStatusEffectsConsumeEffect(effect.get(), probability));
            return this;
        }

        private Builder usingConvertsTo(Item item) {
            remainder = item;
            return this;
        }

        private FoodProperties build() {
            FoodProperties result = food.build();
            Consumable.Builder consumable = Consumable.builder().consumeSeconds(consumeSeconds);
            effects.forEach(consumable::onConsume);
            METADATA.put(result, new FoodMetadata(consumable.build(), remainder));
            return result;
        }
    }
}
