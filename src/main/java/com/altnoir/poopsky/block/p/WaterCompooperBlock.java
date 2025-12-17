package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.AbstractCompooperBlock;
import net.minecraft.world.level.block.state.StateDefinition;

public class WaterCompooperBlock extends AbstractCompooperBlock {
    public WaterCompooperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.state.BlockState> builder) {
        builder.add(LEVEL);
    }
}