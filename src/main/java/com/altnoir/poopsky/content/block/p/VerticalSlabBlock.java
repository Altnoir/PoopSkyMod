package com.altnoir.poopsky.content.block.p;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VerticalSlabBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");
    public static final MapCodec<VerticalSlabBlock> CODEC = simpleCodec(VerticalSlabBlock::new);

    public VerticalSlabBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(DOUBLE, false)
        );
    }

    @Override
    public MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, DOUBLE);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return !state.getValue(DOUBLE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(DOUBLE)) {
            return Shapes.block();
        }

        Direction dir = state.getValue(FACING);
        return switch (dir) {
            case NORTH -> Shapes.box(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
            case SOUTH -> Shapes.box(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f);
            case EAST -> Shapes.box(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            case WEST -> Shapes.box(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
            default -> Shapes.block();
        };
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        if (state.is(this)) {
            return state.setValue(DOUBLE, true).setValue(WATERLOGGED, false);
        }

        Player player = context.getPlayer();
        boolean isSneaking = player != null && player.isShiftKeyDown();
        boolean waterlogged = context.getLevel().getFluidState(pos).getType() == Fluids.WATER;
        Direction facing = getFacingForPlacement(context, pos);
        if (isSneaking) {
            facing = facing.getOpposite();
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(WATERLOGGED, waterlogged);
    }

    private Direction getFacingForPlacement(BlockPlaceContext context, BlockPos pos) {
        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isHorizontal()) {
            return clickedFace.getOpposite();
        }

        Direction playerFacing = context.getHorizontalDirection();
        if (playerFacing.getAxis() == Direction.Axis.X) {
            return context.getClickLocation().x - pos.getX() < 0.5 ? Direction.WEST : Direction.EAST;
        }
        return context.getClickLocation().z - pos.getZ() < 0.5 ? Direction.NORTH : Direction.SOUTH;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        if (state.getValue(DOUBLE) || !stack.is(this.asItem())) {
            return false;
        }
        if (!context.replacingClickedOnBlock()) {
            return true;
        }

        Direction facing = state.getValue(FACING);
        Direction clickedFace = context.getClickedFace();
        if (clickedFace == facing.getOpposite()) {
            return true;
        }
        if (clickedFace == facing) {
            return false;
        }

        double localX = context.getClickLocation().x - context.getClickedPos().getX();
        double localZ = context.getClickLocation().z - context.getClickedPos().getZ();
        return switch (facing) {
            case NORTH -> localZ > 0.5;
            case SOUTH -> localZ < 0.5;
            case WEST -> localX > 0.5;
            case EAST -> localX < 0.5;
            default -> false;
        };
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return !state.getValue(DOUBLE) && SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity entity, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return !state.getValue(DOUBLE) && SimpleWaterloggedBlock.super.canPlaceLiquid(entity, level, pos, state, fluid);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
