package com.altnoir.poopsky.common.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IFeedable {
    default InteractionResult tryFeedToPlayer(ItemStack stack, Player feeder, LivingEntity target) {
        if (target instanceof Player targetPlayer && targetPlayer.canEat(false)) {
            if (!feeder.level().isClientSide) {
                ItemStack feedStack = stack.copy();
                feedStack.setCount(1);
                targetPlayer.eat(feeder.level(), feedStack);
                if (!feeder.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}