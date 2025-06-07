package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.AbstractToiletBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;
import java.util.Objects;

public class ToiletBlock extends AbstractToiletBlock {
    public static final BooleanProperty FORWARD = BooleanProperty.create("forward");
    public static final BooleanProperty BACKWARD = BooleanProperty.create("backward");

    public ToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FORWARD, false)
                .setValue(BACKWARD, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var level = ctx.getLevel();
        var pos = ctx.getClickedPos();
        Direction facing = ctx.getHorizontalDirection().getOpposite();

        var forwardPos = pos.relative(facing);
        var forwardConnected = isValidNeighbor(level, forwardPos, facing);

        var backwardPos = pos.relative(facing.getOpposite());
        var backwardConnected = isValidNeighbor(level, backwardPos, facing);

        return Objects.requireNonNull(super.getStateForPlacement(ctx))
                .setValue(FACING, facing)
                .setValue(FORWARD, forwardConnected)
                .setValue(BACKWARD, backwardConnected);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FORWARD, BACKWARD);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moved) {
        super.neighborChanged(state, level, pos, block, fromPos, moved);

        var facing = state.getValue(FACING);
        var forwardPos = pos.relative(facing);
        var backwardPos = pos.relative(facing.getOpposite());

        var forwardConnected = isValidNeighbor(level, forwardPos, facing);
        var backwardConnected = isValidNeighbor(level, backwardPos, facing);

        level.setBlock(pos, state.setValue(FORWARD, forwardConnected).setValue(BACKWARD, backwardConnected), 3);
    }

    private boolean isValidNeighbor(LevelReader level, BlockPos pos, Direction facing) {
        var neighbor = level.getBlockState(pos);
        return neighbor.getBlock() instanceof ToiletBlock && neighbor.getValue(FACING) == facing;
    }
}
