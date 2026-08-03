package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.item.IFeedable;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ChiliItem extends ItemNameBlockItem implements IFeedable {
    public ChiliItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide) {
            livingEntity.addEffect(new MobEffectInstance(PoEffects.holder(PoEffects.INTESTINAL_SPASM), 9600));
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
                ItemStack redFlyItem = FlyItem.withType(FlyTypes.RED.get());
                fly.spawnAtLocation(redFlyItem);
                fly.kill();
                stack.consume(1, player);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
