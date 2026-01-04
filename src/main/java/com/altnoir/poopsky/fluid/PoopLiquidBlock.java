package com.altnoir.poopsky.fluid;

import com.altnoir.poopsky.block.PSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class PoopLiquidBlock extends LiquidBlock {
    public PoopLiquidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    protected void check(Level level, BlockPos pos) {
        hasBlockNeighbor(level, pos, Blocks.NETHERRACK, Blocks.MAGMA_BLOCK, Blocks.LAVA);
        hasBlockNeighbor(level, pos, PSBlocks.POOPLIME_BLOCK.get(), PSBlocks.POOP_BLOCK.get(), Blocks.WATER);
        hasBlockNeighbor(level, pos, Blocks.SOUL_SAND, Blocks.SAND);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        check(level, pos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        check(level, pos);
    }

    private void hasBlockNeighbor(Level level, BlockPos pos, Block block, Block blockDown, Block liquid) {
        if (lavaNeighbor(level, pos, Direction.UP, liquid)) {
            level.levelEvent(1501, pos, 0);
            level.setBlockAndUpdate(pos, blockDown.defaultBlockState());
        } else {
            hasBlockNeighbor(level, pos, block, liquid);
        }
    }

    private void hasBlockNeighbor(Level level, BlockPos pos, Block block, Block liquid) {
        BlockPos neighborPos = getLavaNeighborPos(level, pos, liquid);
        if (neighborPos != null) {
            level.levelEvent(1501, pos, 0);
            level.setBlockAndUpdate(neighborPos, block.defaultBlockState());
        }
    }

    private BlockPos getLavaNeighborPos(Level level, BlockPos pos, Block liquid) {
        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP) continue;
            if (lavaNeighbor(level, pos, direction, liquid)) {
                return pos.relative(direction);
            }
        }
        return null;
    }

    private boolean lavaNeighbor(Level level, BlockPos pos, Direction direction, Block liquid) {
        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);
        return neighborState.getBlock() == liquid;
    }
}
