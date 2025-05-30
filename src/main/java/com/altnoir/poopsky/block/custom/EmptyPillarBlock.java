package com.altnoir.poopsky.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class EmptyPillarBlock extends Block {
    public static final MapCodec<EmptyPillarBlock> CODEC = createCodec(EmptyPillarBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    private static final VoxelShape RAYCAST_SHAPE_1 = createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    private static final VoxelShape RAYCAST_SHAPE_2 = createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 16.0);
    private static final VoxelShape RAYCAST_SHAPE_3 = createCuboidShape(0.0, 2.0, 2.0, 16.0, 14.0, 14.0);
    private static final VoxelShape RAYCAST_SHAPE = VoxelShapes.fullCube();
    protected static final VoxelShape OUTLINE_SHAPE_1 = VoxelShapes.combineAndSimplify(
            RAYCAST_SHAPE,
            VoxelShapes.union(RAYCAST_SHAPE_1),
            BooleanBiFunction.ONLY_FIRST
    );
    protected static final VoxelShape OUTLINE_SHAPE_2 = VoxelShapes.combineAndSimplify(
            RAYCAST_SHAPE,
            VoxelShapes.union(RAYCAST_SHAPE_2),
            BooleanBiFunction.ONLY_FIRST
    );
    protected static final VoxelShape OUTLINE_SHAPE_3 = VoxelShapes.combineAndSimplify(
            RAYCAST_SHAPE,
            VoxelShapes.union(RAYCAST_SHAPE_3),
            BooleanBiFunction.ONLY_FIRST
    );
    @Override
    public MapCodec<? extends EmptyPillarBlock> getCodec() {
        return CODEC;
    }

    public EmptyPillarBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(AXIS, Direction.Axis.Y)
        );
    }
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction.Axis axis = state.get(AXIS);
        return switch (axis) {
            case X -> OUTLINE_SHAPE_3;
            case Z -> OUTLINE_SHAPE_2;
            default -> OUTLINE_SHAPE_1;
        };
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return changeRotation(state, rotation);
    }
    private static BlockState changeRotation(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.get(AXIS)) {
                case X -> state.with(AXIS, Direction.Axis.Z);
                case Z -> state.with(AXIS, Direction.Axis.X);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getSide();
        return this.getDefaultState().with(AXIS, facing.getAxis());
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}
