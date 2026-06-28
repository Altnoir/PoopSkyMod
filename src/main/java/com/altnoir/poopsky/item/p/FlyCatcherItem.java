package com.altnoir.poopsky.item.p;

import com.altnoir.poopsky.entity.p.FlyEntity;
import com.altnoir.poopsky.init.PFlyTypes;
import com.altnoir.poopsky.init.PSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FlyCatcherItem extends Item {
    public FlyCatcherItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof FlyEntity fly && fly.isAlive() && !fly.isBaby()) {
            Level level = player.level();
            if (!level.isClientSide) {
                PFlyTypes.FlyType type = getFlyTypeFromEntity(fly);
                ItemStack flyItem = FlyItem.withType(type);

                fly.spawnAtLocation(flyItem);

                level.playSound(null, fly.getX(), fly.getY(), fly.getZ(),
                        PSoundEvents.ENTITY_FLY_CAPTURE.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);

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
