package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.data.ArcadeLootGen;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class ArcadeBlock extends Block {
    public static final MapCodec<ArcadeBlock> CODEC = simpleCodec(ArcadeBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private static final VoxelShape TOP_SCREEN_CUTOUT = Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0);
    private static final VoxelShape BOTTOM_NORTH_SHAPE = Shapes.or(
            Block.box(0.0, 0.0, 1.0, 16.0, 14.0, 16.0),
            Block.box(0.0, 14.0, 8.0, 16.0, 16.0, 16.0)
    );
    private static final VoxelShape TOP_NORTH_SHAPE = Shapes.join(
            Shapes.or(
                    Block.box(0.0, 0.0, 8.0, 16.0, 12.0, 16.0),
                    Block.box(0.0, 12.0, 5.0, 16.0, 16.0, 16.0)
            ),
            TOP_SCREEN_CUTOUT,
            BooleanOp.ONLY_FIRST
    );
    private static final Map<Direction, VoxelShape> BOTTOM_SHAPES = computeShapes(BOTTOM_NORTH_SHAPE);
    private static final Map<Direction, VoxelShape> TOP_SHAPES = computeShapes(TOP_NORTH_SHAPE);

    public ArcadeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (facing.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (facing == Direction.UP)) {
            return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
        if (facingState.is(this) && facingState.getValue(HALF) != half) {
            return facingState.setValue(HALF, half);
        }
        return facingState.is(Blocks.MOVING_PISTON) || isMatchingMovingHalf(state, level, facingPos)
                ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))) {
            if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockPos lowerPos = pos.below();
                BlockState lowerState = level.getBlockState(lowerPos);
                if (lowerState.is(this) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    level.setBlock(lowerPos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, lowerPos, Block.getId(lowerState));
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return true;
        }
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.is(this) || belowState.is(Blocks.MOVING_PISTON) || isMatchingMovingHalf(state, level, below);
    }

    private static boolean isMatchingMovingHalf(BlockState state, BlockGetter level, BlockPos partnerPos) {
        if (level.getBlockEntity(partnerPos) instanceof PistonMovingBlockEntity moving) {
            BlockState moved = moving.getMovedState();
            return moved.is(state.getBlock()) && moved.getValue(HALF) != state.getValue(HALF);
        }
        return false;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getArcadeShape(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getArcadeShape(state);
    }

    private static VoxelShape getArcadeShape(BlockState state) {
        Map<Direction, VoxelShape> shapes = state.getValue(HALF) == DoubleBlockHalf.UPPER ? TOP_SHAPES : BOTTOM_SHAPES;
        return shapes.get(state.getValue(FACING));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }

        BlockPos ejectPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        BlockState ejectState = level.getBlockState(ejectPos);
        if (!ejectState.is(this) || ejectState.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return InteractionResult.PASS;
        }

        if (level instanceof ServerLevel serverLevel) {
            Direction facing = ejectState.getValue(FACING);
            var lootTable = serverLevel.getServer()
                    .reloadableRegistries()
                    .getLootTable(ArcadeLootGen.lootTableKey(ejectState.getBlock()));
            lootTable.getRandomItems(new LootParams.Builder(serverLevel).create(LootContextParamSets.EMPTY))
                    .forEach(stack -> spawnArcadeItem(serverLevel, ejectPos, facing, stack));
            serverLevel.levelEvent(1000, ejectPos, 0);
            serverLevel.levelEvent(2000, ejectPos, facing.get3DDataValue());
        }

        return InteractionResult.sidedSuccess(false);
    }

    private static void spawnArcadeItem(ServerLevel level, BlockPos pos, Direction facing, ItemStack stack) {
        Vec3 position = Vec3.atCenterOf(pos).add(
                facing.getStepX() * 0.7,
                0.0,
                facing.getStepZ() * 0.7
        );
        DefaultDispenseItemBehavior.spawnItem(level, stack, 6, facing, position);
    }

    @Override
    public boolean isStickyBlock(BlockState state) {
        return true;
    }

    @Override
    public boolean canStickTo(BlockState state, BlockState other) {
        if (state.is(this) && other.is(this) && state.getValue(HALF) != other.getValue(HALF)) {
            return true;
        }
        if (state.is(this) || other.is(this)) {
            return false;
        }
        return super.canStickTo(state, other);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    private static Map<Direction, VoxelShape> computeShapes(VoxelShape northShape) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        for (Direction direction : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
            map.put(direction, rotateShape(northShape, direction));
        }
        return map;
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
        if (direction == Direction.NORTH) {
            return shape;
        }
        return shape.toAabbs().stream()
                .map(aabb -> rotateAABB(aabb, direction))
                .reduce(Shapes.empty(), Shapes::or);
    }

    private static VoxelShape rotateAABB(AABB aabb, Direction direction) {
        double minX = aabb.minX * 16;
        double minY = aabb.minY * 16;
        double minZ = aabb.minZ * 16;
        double maxX = aabb.maxX * 16;
        double maxY = aabb.maxY * 16;
        double maxZ = aabb.maxZ * 16;

        return switch (direction) {
            case EAST -> Block.box(16.0 - maxZ, minY, minX, 16.0 - minZ, maxY, maxX);
            case SOUTH -> Block.box(16.0 - maxX, minY, 16.0 - maxZ, 16.0 - minX, maxY, 16.0 - minZ);
            case WEST -> Block.box(minZ, minY, 16.0 - maxX, maxZ, maxY, 16.0 - minX);
            default -> Block.box(minX, minY, minZ, maxX, maxY, maxZ);
        };
    }
}
