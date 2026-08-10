package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.client.inventory.PortableToiletMenu;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;

public class PortableToiletBlock extends Block {
    public static final MapCodec<PortableToiletBlock> CODEC = simpleCodec(PortableToiletBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final Component CONTAINER_TITLE = Component.translatable("container.poopsky.portable_toilet");

    private static final Direction[] SHAPE_DIRECTIONS = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };
    private static final VoxelShape[] BOTTOM_CLOSED_SHAPES = createBottomShapes(false);
    private static final VoxelShape[] BOTTOM_OPEN_SHAPES = createBottomShapes(true);
    private static final VoxelShape[] TOP_CLOSED_SHAPES = createTopShapes(false);
    private static final VoxelShape[] TOP_OPEN_SHAPES = createTopShapes(true);

    public PortableToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    private static VoxelShape[] createBottomShapes(boolean open) {
        VoxelShape[] shapes = new VoxelShape[SHAPE_DIRECTIONS.length];
        for (int i = 0; i < SHAPE_DIRECTIONS.length; i++) {
            Direction facing = SHAPE_DIRECTIONS[i];
            VoxelShape shape = Shapes.or(
                    boxForFacing(facing, 0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
                    boxForFacing(facing, 1.0, 2.0, 6.0, 15.0, 5.0, 15.0),
                    boxForFacing(facing, 0.0, 2.0, 1.0, 1.0, 16.0, 15.0),
                    boxForFacing(facing, 15.0, 2.0, 1.0, 16.0, 16.0, 15.0),
                    boxForFacing(facing, 0.0, 2.0, 15.0, 16.0, 16.0, 16.0)
            );
            if (!open) {
                shape = Shapes.or(shape, boxForFacing(facing, 0.0, 2.0, 0.0, 16.0, 16.0, 1.0));
            }
            shapes[i] = shape.optimize();
        }
        return shapes;
    }

    private static VoxelShape[] createTopShapes(boolean open) {
        VoxelShape[] shapes = new VoxelShape[SHAPE_DIRECTIONS.length];
        for (int i = 0; i < SHAPE_DIRECTIONS.length; i++) {
            Direction facing = SHAPE_DIRECTIONS[i];
            VoxelShape shape = Shapes.or(
                    boxForFacing(facing, 0.0, 14.0, 0.0, 16.0, 16.0, 16.0),
                    boxForFacing(facing, 0.0, 0.0, 1.0, 1.0, 14.0, 15.0),
                    boxForFacing(facing, 15.0, 0.0, 1.0, 16.0, 14.0, 15.0),
                    boxForFacing(facing, 0.0, 0.0, 15.0, 16.0, 14.0, 16.0)
            );
            if (!open) {
                shape = Shapes.or(shape, boxForFacing(facing, 0.0, 0.0, 0.0, 16.0, 14.0, 1.0));
            }
            shapes[i] = shape.optimize();
        }
        return shapes;
    }

    private static VoxelShape boxForFacing(Direction facing, double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
        double x1 = fromX;
        double z1 = fromZ;
        double x2 = toX;
        double z2 = toZ;
        switch (facing) {
            case SOUTH -> {
                x1 = 16.0 - toX;
                z1 = 16.0 - toZ;
                x2 = 16.0 - fromX;
                z2 = 16.0 - fromZ;
            }
            case EAST -> {
                x1 = 16.0 - toZ;
                z1 = fromX;
                x2 = 16.0 - fromZ;
                z2 = toX;
            }
            case WEST -> {
                x1 = fromZ;
                z1 = 16.0 - toX;
                x2 = toZ;
                z2 = 16.0 - fromX;
            }
            default -> {
            }
        }
        return Block.box(x1, fromY, z1, x2, toY, z2);
    }

    private static VoxelShape getPortableToiletShape(BlockState state) {
        boolean upper = state.getValue(HALF) == DoubleBlockHalf.UPPER;
        boolean open = state.getValue(OPEN);
        VoxelShape[] shapes = upper
                ? (open ? TOP_OPEN_SHAPES : TOP_CLOSED_SHAPES)
                : (open ? BOTTOM_OPEN_SHAPES : BOTTOM_CLOSED_SHAPES);
        return switch (state.getValue(FACING)) {
            case EAST -> shapes[1];
            case SOUTH -> shapes[2];
            case WEST -> shapes[3];
            default -> shapes[0];
        };
    }

    private static boolean isInSeatArea(BlockState state, BlockPos pos, BlockHitResult hitResult) {
        double localX = (hitResult.getLocation().x - pos.getX()) * 16.0;
        double localY = (hitResult.getLocation().y - pos.getY()) * 16.0;
        double localZ = (hitResult.getLocation().z - pos.getZ()) * 16.0;
        double modelX;
        double modelZ;
        switch (state.getValue(FACING)) {
            case SOUTH -> {
                modelX = 16.0 - localX;
                modelZ = 16.0 - localZ;
            }
            case EAST -> {
                modelX = localZ;
                modelZ = 16.0 - localX;
            }
            case WEST -> {
                modelX = 16.0 - localZ;
                modelZ = localX;
            }
            default -> {
                modelX = localX;
                modelZ = localZ;
            }
        }
        return modelX >= 1.0 && modelX <= 15.0
                && localY >= 2.0 && localY <= 5.0
                && modelZ >= 6.0 && modelZ <= 15.0;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(OPEN, false)
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
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            ToiletUtil.portableToiletInside(level, pos, state, entity);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos respawnPos = getRespawnAnchorPos(state, pos);
        BlockState respawnState = level.getBlockState(respawnPos);
        if (player instanceof ServerPlayer serverPlayer && respawnState.is(this)
                && respawnState.getValue(HALF) == DoubleBlockHalf.UPPER && (serverPlayer.getRespawnDimension() != level.dimension()
                || !respawnPos.equals(serverPlayer.getRespawnPosition()))) {
            serverPlayer.setRespawnPosition(level.dimension(), respawnPos, player.getYRot(), false, true);
        }

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            if (isInSeatArea(state, pos, hitResult)) {
                if (!level.isClientSide) {
                    player.openMenu(state.getMenuProvider(level, pos));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        setOpen(level, pos, state, !state.getValue(OPEN));

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((containerId, inventory, player) ->
                new PortableToiletMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }

    private void setOpen(Level level, BlockPos pos, BlockState state, boolean open) {
        level.setBlock(pos, state.setValue(OPEN, open), 10);
        BlockPos otherPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
        BlockState otherState = level.getBlockState(otherPos);
        if (otherState.is(this) && otherState.getValue(HALF) != state.getValue(HALF)) {
            level.setBlock(otherPos, otherState.setValue(OPEN, open), 10);
        }
        level.playSound(null, pos, open ? SoundEvents.WOODEN_DOOR_OPEN : SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getPortableToiletShape(state);
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getPortableToiletShape(state);
    }

    @Override
    public Optional<ServerPlayer.RespawnPosAngle> getRespawnPosition(BlockState state, EntityType<?> type, LevelReader level, BlockPos pos, float orientation) {
        BlockPos respawnPos = getRespawnAnchorPos(state, pos);
        BlockState respawnState = level.getBlockState(respawnPos);
        if (!respawnState.is(this) || respawnState.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return Optional.empty();
        }
        return Optional.of(ServerPlayer.RespawnPosAngle.of(Vec3.atCenterOf(respawnPos), respawnPos));
    }

    private static BlockPos getRespawnAnchorPos(BlockState state, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();
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
}
