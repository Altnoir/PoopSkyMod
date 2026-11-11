package com.altnoir.poopsky.content.block.p;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OverclockUnitBlock extends EndRodBlock {
    private static final MapCodec<EndRodBlock> CODEC = simpleCodec(OverclockUnitBlock::new);
    private static final VoxelShape Y_SHAPE = box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape X_SHAPE = box(0, 4, 4, 16, 12, 12);
    private static final VoxelShape Z_SHAPE = box(4, 4, 0, 12, 12, 16);

    public OverclockUnitBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<EndRodBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING).getAxis()) {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    }
}
