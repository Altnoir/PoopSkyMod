package com.altnoir.poopsky.content.block.p;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PoopLogBlock extends LogBlock {
    public PoopLogBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        boolean allSolid = true;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (!neighborState.isSolidRender(level, neighborPos) || neighborState.is(BlockTags.LEAVES)) {
                allSolid = false;
                break;
            }
        }
        if (allSolid) {
            level.setBlockAndUpdate(pos, Blocks.COAL_BLOCK.defaultBlockState());
        }
    }
}