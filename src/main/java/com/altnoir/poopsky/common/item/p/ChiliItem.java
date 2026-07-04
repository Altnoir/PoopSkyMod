package com.altnoir.poopsky.common.item.p;

import com.altnoir.poopsky.common.entity.p.FlyEntity;
import com.altnoir.poopsky.common.item.IFeedable;
import com.altnoir.poopsky.common.item.PFlyTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ChiliItem extends Item implements IFeedable {
    public ChiliItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide) {
            livingEntity.hurt(livingEntity.damageSources().inFire(), 1.0F);
            livingEntity.igniteForSeconds(3);
        }
        return stack;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        InteractionResult feedResult = tryFeedToPlayer(stack, player, target);
        if (feedResult.consumesAction()) return feedResult;
        if (target instanceof FlyEntity fly && fly.isAlive()) {
            if (!player.level().isClientSide) {
                ItemStack redFlyItem = FlyItem.withType(PFlyTypes.RED.get());
                fly.spawnAtLocation(redFlyItem);
                fly.kill();
                stack.consume(1, player);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}