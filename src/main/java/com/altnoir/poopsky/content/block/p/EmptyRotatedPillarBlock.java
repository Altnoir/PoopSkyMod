package com.altnoir.poopsky.content.block.p;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmptyRotatedPillarBlock extends DirectionalBlock {
    public static final MapCodec<EmptyRotatedPillarBlock> CODEC = simpleCodec(EmptyRotatedPillarBlock::new);

    private static final VoxelShape RAYCAST_SHAPE_1 = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape RAYCAST_SHAPE_2 = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 16.0D);
    private static final VoxelShape RAYCAST_SHAPE_3 = Block.box(0.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
    private static final VoxelShape RAYCAST_SHAPE = Shapes.block();

    private static final VoxelShape OUTLINE_SHAPE_1 = Shapes.join(
            RAYCAST_SHAPE,
            RAYCAST_SHAPE_1,
            BooleanOp.ONLY_FIRST
    );
    private static final VoxelShape OUTLINE_SHAPE_2 = Shapes.join(
            RAYCAST_SHAPE,
            RAYCAST_SHAPE_2,
            BooleanOp.ONLY_FIRST
    );
    private static final VoxelShape OUTLINE_SHAPE_3 = Shapes.join(
            RAYCAST_SHAPE,
            RAYCAST_SHAPE_3,
            BooleanOp.ONLY_FIRST
    );

    public EmptyRotatedPillarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING).getAxis()) {
            case X -> OUTLINE_SHAPE_3;
            case Z -> OUTLINE_SHAPE_2;
            default -> OUTLINE_SHAPE_1;
        };
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING).getAxis()) {
            case X -> OUTLINE_SHAPE_3;
            case Z -> OUTLINE_SHAPE_2;
            default -> OUTLINE_SHAPE_1;
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.2F;
    }
}
