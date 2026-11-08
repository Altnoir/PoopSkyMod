package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.init.PoEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class MilosSwordItem extends SwordItem {
    public static final ResourceLocation BASE_INTERACTION_RANGE_ID = PoopSky.mcloc("base_interaction_range");

    public MilosSwordItem(Tier tier, Properties properties) {
        super(tier, 1, -3.2F, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.getType().is(PoTags.EntityTypes.IGNORES_BLEEDING)) {
            if (attacker instanceof Player player && player.getAttackStrengthScale(0.0F) >= 1.0F) {
                if (!target.hasEffect(PoEffects.BLEEDING.get())) {
                    target.addEffect(new MobEffectInstance(PoEffects.BLEEDING.get(), 200));
                } else {
                    int duration = target.getEffect(PoEffects.BLEEDING.get()).getDuration() + 200;
                    int amplifier = target.getEffect(PoEffects.BLEEDING.get()).getAmplifier() + 1;

                    if (amplifier % 5 == 0) {
                        float baseDamage = (0.1F + 0.1F * (amplifier / 5.0F)) / (1.0F + 0.1F * amplifier);
                        float damage = target.getMaxHealth() * baseDamage;
                        target.hurt(target.damageSources().mobAttack(attacker), damage);
                    }
                    target.addEffect(new MobEffectInstance(PoEffects.BLEEDING.get(), duration, amplifier));
                }
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public com.google.common.collect.Multimap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier> getDefaultAttributeModifiers(net.minecraft.world.entity.EquipmentSlot slot) {
        if (slot != net.minecraft.world.entity.EquipmentSlot.MAINHAND) return super.getDefaultAttributeModifiers(slot);
        return com.google.common.collect.ImmutableMultimap.<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier>builder()
            .putAll(super.getDefaultAttributeModifiers(slot))
            .put(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get(), new AttributeModifier(java.util.UUID.nameUUIDFromBytes(BASE_INTERACTION_RANGE_ID.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)), "PoopSky weapon reach", 2.0D, AttributeModifier.Operation.ADDITION))
            .build();
    }
}
