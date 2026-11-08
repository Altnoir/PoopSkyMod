package com.altnoir.poopsky.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 1.21 的持物/默认交互拆分在 Forge 1.20.1 的单一 use 回调中执行。 */
public interface PoBlockInteraction {
    default InteractionResult poopsky$useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }
    default InteractionResult poopsky$useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.PASS;
    }
    default InteractionResult poopsky$interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = poopsky$useItemOn(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != InteractionResult.PASS) return result;
        return hand == InteractionHand.MAIN_HAND ? poopsky$useWithoutItem(state, level, pos, player, hit) : InteractionResult.PASS;
    }
}
