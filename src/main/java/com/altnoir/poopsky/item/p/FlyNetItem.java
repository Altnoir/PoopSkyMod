package com.altnoir.poopsky.item.p;

import com.altnoir.poopsky.entity.p.FlyEntity;
import com.altnoir.poopsky.init.PFlyTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 捕蝇网：右击苍蝇实体将其变为物品形式。
 */
public class FlyNetItem extends Item {
    public FlyNetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof FlyEntity fly && fly.isAlive()) {
            if (!player.level().isClientSide) {
                PFlyTypes.FlyType type = getFlyTypeFromEntity(fly);
                ItemStack flyItem = FlyItem.withType(type);

                fly.spawnAtLocation(flyItem);

                player.level().playSound(null, fly.getX(), fly.getY(), fly.getZ(),
                        SoundEvents.BEEHIVE_EXIT, SoundSource.NEUTRAL, 1.0F, 1.0F);

                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                }

                fly.discard();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static PFlyTypes.FlyType getFlyTypeFromEntity(FlyEntity fly) {
        return PFlyTypes.NORMAL;
    }
}
