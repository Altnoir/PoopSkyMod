package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class SaltpeterClusterBlock extends AmethystClusterBlock implements BonemealableBlock {
    public SaltpeterClusterBlock(float height, float aabbOffset, Properties properties) {
        super(height, aabbOffset, properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.is(PoBlocks.SALTPETER_CLUSTER.get()) || !state.canSurvive(level, pos)) {
            return;
        }
        level.setBlock(pos, getGrowthState(state), 2);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (level.getBlockState(pos.relative(facing.getOpposite())).is(BlockTags.ICE)) {
            level.setBlockAndUpdate(pos, Blocks.POWDER_SNOW.defaultBlockState());
        } else {
            level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (level instanceof Level concreteLevel) {
            isDone(concreteLevel, pos, state);
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        isDone(level, pos, state);
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    private void isDone(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide() && state.is(PoBlocks.SALTPETER_CLUSTER.get()) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, this, 4);
        }
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        BlockPos pos = hit.getBlockPos();
        if (level instanceof ServerLevel serverLevel && projectile.mayInteract(serverLevel, pos)) {
            level.playSound(null, pos, PoSoundEvents.BLOCK_SALTPETER_CHIME.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return this != PoBlocks.SALTPETER_CLUSTER.get();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        BlockState nextState = getGrowthState(blockState);
        serverLevel.setBlockAndUpdate(blockPos, nextState);
    }

    private static BlockState getGrowthState(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        boolean waterlogged = state.getValue(BlockStateProperties.WATERLOGGED);
        if (state.is(PoBlocks.SMALL_SALTPETER_BUD.get())) {
            return PoBlocks.MEDIUM_SALTPETER_BUD.get().defaultBlockState()
                    .setValue(BlockStateProperties.FACING, facing)
                    .setValue(BlockStateProperties.WATERLOGGED, waterlogged);
        } else if (state.is(PoBlocks.MEDIUM_SALTPETER_BUD.get())) {
            return PoBlocks.LARGE_SALTPETER_BUD.get().defaultBlockState()
                    .setValue(BlockStateProperties.FACING, facing)
                    .setValue(BlockStateProperties.WATERLOGGED, waterlogged);
        } else if (state.is(PoBlocks.LARGE_SALTPETER_BUD.get())) {
            return PoBlocks.SALTPETER_CLUSTER.get().defaultBlockState()
                    .setValue(BlockStateProperties.FACING, facing)
                    .setValue(BlockStateProperties.WATERLOGGED, waterlogged);
        }
        return state;
    }
}
