package com.altnoir.poopsky.content.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 让物品可以被右键喂食给其他玩家。
 * <p>
 * 实现类必须在 {@code interactLivingEntity} 中优先调用 {@link #tryFeedToPlayer}，
 * 如果返回 {@code consumesAction()} 则直接 return，否则继续执行原有逻辑：
 * <pre>{@code
 * @Override
 * public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
 *     InteractionResult feedResult = tryFeedToPlayer(stack, player, target);
 *     if (feedResult.consumesAction()) return feedResult;
 *     return super.interactLivingEntity(stack, player, target, hand);
 * }
 * }</pre>
 */
public interface IFeedable {
    /**
     * 尝试将手中的食物喂给目标玩家。
     * <p>
     * 自动防御处理：
     * <ul>
     *   <li>空物品栈 → PASS</li>
     *   <li>目标不是玩家 → PASS</li>
     *   <li>目标玩家饱食度已满 → PASS</li>
     *   <li>创造模式不消耗物品</li>
     * </ul>
     */
    default InteractionResult tryFeedToPlayer(ItemStack stack, Player feeder, LivingEntity target) {
        if (stack.isEmpty()) return InteractionResult.PASS;
        if (!(target instanceof Player targetPlayer)) return InteractionResult.PASS;
        if (!targetPlayer.canEat(false)) return InteractionResult.PASS;

        if (!feeder.level().isClientSide) {
            ItemStack feedStack = stack.copy();
            feedStack.setCount(1);
            targetPlayer.eat(feeder.level(), feedStack);

            if (!feeder.getAbilities().instabuild) {
                stack.shrink(1);
            }

            targetPlayer.level().playSound(null, targetPlayer,
                    targetPlayer.getEatingSound(feedStack),
                    SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }
}