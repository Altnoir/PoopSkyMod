package com.altnoir.poopsky.content.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public interface IFeedable {
    default InteractionResult tryFeedToPlayer(ItemStack stack, Player feeder, LivingEntity target) {
        if (stack.isEmpty()) return InteractionResult.PASS;
        if (!(target instanceof Player targetPlayer)) return InteractionResult.PASS;
        if (!targetPlayer.canEat(false)) return InteractionResult.PASS;

        if (!feeder.level().isClientSide()) {
            ItemStack feedStack = stack.copy();
            feedStack.setCount(1);
            FoodProperties foodProperties = feedStack.get(DataComponents.FOOD);
            if (foodProperties != null) {
                targetPlayer.getFoodData().eat(foodProperties);
            }

            if (!feeder.getAbilities().instabuild) {
                stack.shrink(1);
            }

            Holder<SoundEvent> eatSound = feedStack.get(DataComponents.CONSUMABLE) != null
                    ? feedStack.get(DataComponents.CONSUMABLE).sound()
                    : SoundEvents.GENERIC_EAT;
            targetPlayer.level().playSound(null, targetPlayer, eatSound.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }
}