package com.altnoir.poopsky.impl.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DispenseUtil {
    public static void spawnItem(Level level, ItemStack stack, double speed, Direction facing, BlockPos blockPos) {
        Vec3 spawnPos = Vec3.atCenterOf(blockPos).add(facing.getStepX() * 0.7, speed - 0.5, facing.getStepZ() * 0.7);
        ItemEntity itementity = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), stack);
        itementity.setDeltaMovement(facing.getStepX() * speed, speed, facing.getStepZ() * speed);
        itementity.setDefaultPickUpDelay();
        level.addFreshEntity(itementity);
        level.playSound(null, blockPos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.levelEvent(2000, blockPos, facing.get3DDataValue());
    }
}