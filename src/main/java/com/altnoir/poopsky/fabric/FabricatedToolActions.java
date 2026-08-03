package com.altnoir.poopsky.fabric;

import com.altnoir.poopsky.fabric.port.extension.IBlockExtension;
import com.altnoir.poopsky.fabric.port.util.ItemAbilities;
import com.altnoir.poopsky.fabric.port.util.ItemAbility;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
public final class FabricatedToolActions {
    private static final ItemAbility[] BLOCK_TRANSFORM_ACTIONS = {
            ItemAbilities.AXE_STRIP,
            ItemAbilities.SHOVEL_FLATTEN,
            ItemAbilities.HOE_TILL
    };

    private FabricatedToolActions() {
    }

    public static void register() {
        UseBlockCallback.EVENT.register(FabricatedToolActions::onUseBlock);
    }

    private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();
        ItemStack stack = player.getItemInHand(hand);
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hitResult.getDirection(), stack)) {
            return InteractionResult.PASS;
        }

        BlockState state = level.getBlockState(pos);
        IBlockExtension extension = state.getBlock();

        UseOnContext context = new UseOnContext(player, hand, hitResult);
        for (ItemAbility action : BLOCK_TRANSFORM_ACTIONS) {
            if (!stack.canPerformAction(action)) {
                continue;
            }

            BlockState modifiedState = extension.getToolModifiedState(state, context, action, level.isClientSide);
            if (modifiedState != null) {
                applyModification(level, pos, player, hand, stack, modifiedState, soundFor(action));
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    private static void applyModification(Level level, BlockPos pos, Player player, InteractionHand hand,
                                          ItemStack stack, BlockState modifiedState, SoundEvent sound) {
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide) {
            level.setBlock(pos, modifiedState, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, modifiedState));
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
    }

    private static SoundEvent soundFor(ItemAbility action) {
        if (action == ItemAbilities.AXE_STRIP) {
            return SoundEvents.AXE_STRIP;
        }
        if (action == ItemAbilities.SHOVEL_FLATTEN) {
            return SoundEvents.SHOVEL_FLATTEN;
        }
        return SoundEvents.HOE_TILL;
    }
}
