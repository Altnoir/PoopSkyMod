package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.init.PoEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class MilosSwordItem extends SwordItem {
    public static final ResourceLocation BASE_INTERACTION_RANGE_ID = PoopSky.mcloc("base_interaction_range");

    public MilosSwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.getType().is(PoTags.EntityTypes.IGNORES_BLEEDING)) {
            if (attacker instanceof Player player && player.getAttackStrengthScale(0.0F) >= 1.0F) {
                if (!target.hasEffect(PoEffects.BLEEDING)) {
                    target.addEffect(new MobEffectInstance(PoEffects.BLEEDING, 200));
                } else {
                    int duration = target.getEffect(PoEffects.BLEEDING).getDuration() + 200;
                    int amplifier = target.getEffect(PoEffects.BLEEDING).getAmplifier() + 1;

                    if (amplifier % 5 == 0) {
                        float baseDamage = (0.1F + 0.1F * (amplifier / 5.0F)) / (1.0F + 0.1F * amplifier);
                        float damage = target.getMaxHealth() * baseDamage;
                        target.hurt(target.damageSources().mobAttack(attacker), damage);
                    }
                    target.addEffect(new MobEffectInstance(PoEffects.BLEEDING, duration, amplifier));
                }
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, float attackRange, float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(BASE_INTERACTION_RANGE_ID, attackRange, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                ).add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
}
