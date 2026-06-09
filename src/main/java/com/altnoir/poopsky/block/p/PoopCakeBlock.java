package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.init.PParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class PoopCakeBlock extends CakeBlock {
    public PoopCakeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockState candleCake = getCandleCakeState(stack, state);
        if (candleCake == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlockAndUpdate(pos, candleCake);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            stack.consume(1, player);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private static BlockState getCandleCakeState(ItemStack stack, BlockState cakeState) {
        if (cakeState.getValue(BITES) != 0 || !(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }

        Block candle = blockItem.getBlock();
        return candle instanceof CandleBlock candleBlock ? PSBlocks.getPoopCandleCake(candleBlock) : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return eat(level, pos, state, player);
    }

    protected static @NotNull InteractionResult eat(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            player.awardStat(Stats.EAT_CAKE_SLICE);
            player.getFoodData().eat(2, 0.1F);
            player.playSound(SoundEvents.GENERIC_EAT);
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(PParticles.POOP_PARTICLE.get(),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        8,
                        0.0, -0.1, 0.0,
                        3.0
                );
            }

            addOrExtendEffect(player, MobEffects.LUCK, 1800, 1);
            addOrExtendEffect(player, MobEffects.CONFUSION, 100, 0);
            addOrExtendEffect(player, MobEffects.BLINDNESS, 20, 0);
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));

            int i = state.getValue(BITES);
            level.gameEvent(player, GameEvent.EAT, pos);
            if (i < 6) {
                level.setBlock(pos, state.setValue(BITES, i + 1), 3);
            } else {
                level.removeBlock(pos, false);
                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }

            return InteractionResult.SUCCESS;
        }
    }

    private static void addOrExtendEffect(Player player, Holder<MobEffect> effect, int duration, int amplifier) {
    MobEffectInstance current = player.getEffect(effect);

        if (current == null) {
            player.addEffect(new MobEffectInstance(effect, duration, amplifier), player);
            return;
        }

        player.addEffect(new MobEffectInstance(
                effect,
                current.getDuration() + duration,
                Math.max(current.getAmplifier(), amplifier),
                current.isAmbient(),
                current.isVisible(),
                current.showIcon()
        ), player);
    }
}
