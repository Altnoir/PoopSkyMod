package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.PSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;

public class PoopCandleCakeBlock extends CandleCakeBlock {
    public PoopCandleCakeBlock(Block candle, Properties properties) {
        super(candle, properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(LIT) && isHittingCandle(hitResult, pos)) {
            extinguish(state, level, pos, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return PoopCakeBlock.eat(level, pos, PSBlocks.POOP_CAKE.get().defaultBlockState(), player);
    }

    private static boolean isHittingCandle(BlockHitResult hitResult, BlockPos pos) {
        Vec3 hitLocation = hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        return hitLocation.y > 0.5
                && hitLocation.x >= 7.0 / 16.0
                && hitLocation.x <= 9.0 / 16.0
                && hitLocation.z >= 7.0 / 16.0
                && hitLocation.z <= 9.0 / 16.0;
    }

    private static void extinguish(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlock(pos, state.setValue(LIT, false), 11);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return PSBlocks.POOP_CAKE.get().asItem().getDefaultInstance();
    }
}
