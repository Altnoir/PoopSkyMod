package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.impl.util.FireSafetyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class FlamFenceGateBlock extends FenceGateBlock {
    public FlamFenceGateBlock(WoodType type, Properties properties) {
        super(type, properties);
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