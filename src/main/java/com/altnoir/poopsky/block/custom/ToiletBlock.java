package com.altnoir.poopsky.block.custom;

import com.altnoir.poopsky.block.AbstractToilet;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Objects;

public class ToiletBlock extends AbstractToilet {
    public static final MapCodec<ToiletBlock> CODEC = createCodec(ToiletBlock::new);
    public static final BooleanProperty FORWARD = BooleanProperty.of("forward");
    public static final BooleanProperty BACKWARD = BooleanProperty.of("backward");
    public ToiletBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FORWARD, false)
                .with(BACKWARD, false));
    }

    @Override
    protected MapCodec<ToiletBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();

        // 检测初始前后连接
        BlockPos forwardPos = pos.offset(facing);
        boolean forwardConnected = isValidNeighbor(world, forwardPos, facing);

        BlockPos backwardPos = pos.offset(facing.getOpposite());
        boolean backwardConnected = isValidNeighbor(world, backwardPos, facing);

        return Objects.requireNonNull(super.getPlacementState(ctx))
                .with(FACING, facing)
                .with(FORWARD, forwardConnected)
                .with(BACKWARD, backwardConnected);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FORWARD, BACKWARD);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

        Direction facing = state.get(FACING);

        BlockPos forwardPos = pos.offset(facing);
        boolean forwardConnected = isValidNeighbor(world, forwardPos, facing);

        BlockPos backwardPos = pos.offset(facing.getOpposite());
        boolean backwardConnected = isValidNeighbor(world, backwardPos, facing);

        world.setBlockState(pos, state.with(FORWARD, forwardConnected).with(BACKWARD, backwardConnected));
    }

    // 验证附近方块
    private boolean isValidNeighbor(World world, BlockPos pos, Direction facing) {
        return world.getBlockState(pos).getBlock() instanceof ToiletBlock && world.getBlockState(pos).get(FACING) == facing;
    }
}
