package com.altnoir.poopsky.common.item.p;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ChiliPoopItem extends Item {
    public ChiliPoopItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide) {
            livingEntity.hurt(livingEntity.damageSources().inFire(), 1.0F);
        }
        return stack;
    }
}
