package com.altnoir.poopsky.common.item.p;

import com.altnoir.poopsky.common.entity.p.FlyEntity;
import com.altnoir.poopsky.common.item.PFlyTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChiliItem extends Item {
    public ChiliItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof FlyEntity fly && fly.isAlive()) {
            if (!player.level().isClientSide) {
                ItemStack redFlyItem = FlyItem.withType(PFlyTypes.RED.get());
                fly.spawnAtLocation(redFlyItem);
                fly.kill();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}