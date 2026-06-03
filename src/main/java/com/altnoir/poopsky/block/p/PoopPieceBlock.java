package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.PSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PoopPieceBlock extends SnowLayerBlock {
    public PoopPieceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos.below());
        if (blockstate.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
            return false;
        } else {
            return blockstate.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)
                    || blockstate.is(PSBlocks.POOP_BLOCK.get())
                    || Block.isFaceFull(blockstate.getCollisionShape(level, pos.below()), Direction.UP)
                    || blockstate.is(this) && blockstate.getValue(LAYERS) == 8;
        }
    }
}
