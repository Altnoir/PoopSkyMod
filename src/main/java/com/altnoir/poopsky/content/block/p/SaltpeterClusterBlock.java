package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SaltpeterClusterBlock extends AmethystClusterBlock implements BonemealableBlock {
    public SaltpeterClusterBlock(float height, float aabbOffset, Properties properties) {
        super(height, aabbOffset, properties);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide() && state.is(PoBlocks.SALTPETER_CLUSTER.get()) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, this, 4);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
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

    private BlockState getGrowthState(BlockState state) {
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