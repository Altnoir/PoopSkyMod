package com.altnoir.poopsky.common.item.p;

import com.altnoir.poopsky.init.PEffects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DragonFruitRItem extends Item {
    public DragonFruitRItem(Properties properties) {
        super(properties);

    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);

        if (livingEntity instanceof ServerPlayer player) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            player.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide) {
            if (!livingEntity.hasEffect(PEffects.ON_THE_VERGE)) {
                livingEntity.addEffect(new MobEffectInstance(PEffects.ON_THE_VERGE, 200));
            } else {
                int amplifier = livingEntity.getEffect(PEffects.ON_THE_VERGE).getAmplifier() + 1;
                livingEntity.addEffect(new MobEffectInstance(PEffects.ON_THE_VERGE, 200, amplifier));
            }
            livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
        }
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }
}
