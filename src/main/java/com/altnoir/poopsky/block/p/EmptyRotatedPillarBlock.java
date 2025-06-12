package com.altnoir.poopsky.block.p;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmptyRotatedPillarBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.create("axis", Direction.Axis.class);

    private static final VoxelShape RAYCAST_SHAPE_1 = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape RAYCAST_SHAPE_2 = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 16.0D);
    private static final VoxelShape RAYCAST_SHAPE_3 = Block.box(0.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
    private static final VoxelShape RAYCAST_SHAPE = Shapes.block();

    private static final VoxelShape OUTLINE_SHAPE_1 = Shapes.joinUnoptimized(
            RAYCAST_SHAPE,
            RAYCAST_SHAPE_1,
            BooleanOp.ONLY_FIRST
    );
    private static final VoxelShape OUTLINE_SHAPE_2 = Shapes.joinUnoptimized(
            RAYCAST_SHAPE,
            RAYCAST_SHAPE_2,
            BooleanOp.ONLY_FIRST
    );
    private static final VoxelShape OUTLINE_SHAPE_3 = Shapes.joinUnoptimized(
            RAYCAST_SHAPE,
            RAYCAST_SHAPE_3,
            BooleanOp.ONLY_FIRST
    );

    public EmptyRotatedPillarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        return this.defaultBlockState().setValue(AXIS, facing.getAxis());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case X -> OUTLINE_SHAPE_3;
            case Z -> OUTLINE_SHAPE_2;
            default -> OUTLINE_SHAPE_1;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return RAYCAST_SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        Direction.Axis axis = state.getValue(AXIS);
        switch (rotation) {
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> {
                if (axis == Direction.Axis.X) return state.setValue(AXIS, Direction.Axis.Z);
                else if (axis == Direction.Axis.Z) return state.setValue(AXIS, Direction.Axis.X);
            }
        }
        return state;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
