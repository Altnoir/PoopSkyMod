package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.impl.util.FireSafetyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FlamPlanksBlock extends Block {
    public FlamPlanksBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return FireSafetyUtil.isFlammable(state);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return FireSafetyUtil.getFlammability(state, 20);
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return FireSafetyUtil.getFireSpreadSpeed(state, 5);
    }
}