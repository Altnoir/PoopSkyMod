package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.block.p.GachaBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenItem extends Item {
    private static final Map<UUID, BlockPos> ACTIVE_TARGETS = new HashMap<>();

    public TokenItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof GachaBlock)) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        if (player != null) {
            if (!context.getLevel().isClientSide) {
                ACTIVE_TARGETS.put(player.getUUID(), context.getClickedPos());
            }
            player.startUsingItem(context.getHand());
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 24;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) {
            livingEntity.releaseUsingItem();
            return;
        }

        HitResult hitResult = calculateHitResult(player);
        if (!(hitResult instanceof BlockHitResult blockHitResult)
                || !(level.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof GachaBlock gacha)) {
            livingEntity.releaseUsingItem();
            return;
        }

        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos pos = blockHitResult.getBlockPos();
        ACTIVE_TARGETS.put(player.getUUID(), pos);
        int useTicks = getUseDuration(stack, livingEntity) - remainingUseDuration + 1;
        if (useTicks % 3 != 0) {
            return;
        }
        if (gacha.advanceTokenSpin(serverLevel, pos)) {
            stack.consume(1, player);
            gacha.completeTokenSpin(serverLevel, pos);
            livingEntity.releaseUsingItem();
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!level.isClientSide && livingEntity instanceof Player player) {
            BlockPos pos = ACTIVE_TARGETS.remove(player.getUUID());
            if (pos != null && level.getBlockState(pos).getBlock() instanceof GachaBlock gacha) {
                gacha.resetTokenSpin(level, pos);
            }
        }
    }

    private static HitResult calculateHitResult(Player player) {
        return ProjectileUtil.getHitResultOnViewVector(
                player,
                entity -> !entity.isSpectator() && entity.isPickable(),
                player.blockInteractionRange()
        );
    }
}
