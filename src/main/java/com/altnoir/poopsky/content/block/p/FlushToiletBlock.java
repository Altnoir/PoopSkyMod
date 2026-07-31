package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.entity.FlushToiletBlockEntity;
import com.altnoir.poopsky.content.entity.p.FlushToiletEntity;
import com.altnoir.poopsky.data.sound.PoSoundEvents;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import com.altnoir.poopsky.init.PoEntityType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class FlushToiletBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    public static final MapCodec<FlushToiletBlock> CODEC = simpleCodec(FlushToiletBlock::new);

    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(4, 0, 3, 12, 5, 13),
            Block.box(4, 5, 2, 12, 8, 13),
            Block.box(4, 6, 12, 12, 16, 16)
    );

    private static final Map<Direction, VoxelShape> SHAPES = computeShapes(NORTH_SHAPE);

    public FlushToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CLOSED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FlushToiletBlockEntity(blockPos, blockState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(CLOSED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide) {
            if (ToiletUtil.tryTeleportFromFall(level, pos, entity, fallDistance)) {
                return;
            }
        }
        super.fallOn(level, state, pos, entity, fallDistance);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(CLOSED) && player.isShiftKeyDown()) {
            if (level.getBlockEntity(pos) instanceof FlushToiletBlockEntity be) {
                player.openMenu(be);
            }
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(CLOSED) || hitResult.getLocation().y - pos.getY() >= 0.51) {
            boolean closed = !state.getValue(CLOSED);
            level.playSound(null, pos,
                    closed ? PoSoundEvents.BLOCK_FLUSH_TOILET_CLOSE.get() : PoSoundEvents.BLOCK_FLUSH_TOILET_OPEN.get(),
                    SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            level.setBlock(pos, state.setValue(CLOSED, closed), Block.UPDATE_CLIENTS);
            return InteractionResult.SUCCESS;
        }
        if (!state.getValue(CLOSED) && !level.getEntities(PoEntityType.FLUSH_TOILET.get(), new AABB(pos), e -> !e.getPassengers().isEmpty()).isEmpty()) {
            if (level.getBlockEntity(pos) instanceof FlushToiletBlockEntity be) {
                player.openMenu(be);
            }
            return InteractionResult.SUCCESS;
        }

        FlushToiletEntity entity = getOrCreateFlushToiletEntity((ServerLevel) level, pos, state);
        if (entity != null) {
            player.startRiding(entity);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    private static FlushToiletEntity getOrCreateFlushToiletEntity(ServerLevel level, BlockPos pos, BlockState state) {
        return level.getEntities(PoEntityType.FLUSH_TOILET.get(), new AABB(pos), e -> true)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    FlushToiletEntity entity = PoEntityType.FLUSH_TOILET.get().spawn(level, pos, MobSpawnType.TRIGGERED);
                    if (entity != null) {
                        Direction facing = state.getValue(FACING);
                        float v = (float) 1 / 16;
                        double offsetX = facing.getStepX() * v;
                        double offsetZ = facing.getStepZ() * v;
                        entity.setPos(entity.getX() + offsetX, entity.getY(), entity.getZ() + offsetZ);
                    }
                    return entity;
                });
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, moved);
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(CLOSED)) {
            if (powered) {
                if (level.getBlockEntity(pos) instanceof FlushToiletBlockEntity be) {
                    be.clearContents();
                }
                level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.25F, 1.0F);
            }
            level.playSound(null, pos, powered ? PoSoundEvents.BLOCK_FLUSH_TOILET_CLOSE.get() : PoSoundEvents.BLOCK_FLUSH_TOILET_OPEN.get(), SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            level.setBlock(pos, state.setValue(CLOSED, powered), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof FlushToiletBlockEntity be) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.getItemHandler().getStackInSlot(0));
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CLOSED);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    private static Map<Direction, VoxelShape> computeShapes(VoxelShape northShape) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        for (Direction direction : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
            map.put(direction, rotateShape(northShape, direction));
        }
        return map;
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
        Rotation rotation = switch (direction) {
            case EAST -> Rotation.COUNTERCLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.CLOCKWISE_90;
            default -> Rotation.NONE;
        };
        if (rotation == Rotation.NONE) return shape;
        return shape.toAabbs().stream()
                .map(aabb -> rotateAABB(aabb, rotation))
                .reduce(Shapes.empty(), Shapes::or);
    }

    private static VoxelShape rotateAABB(AABB aabb, Rotation rotation) {
        double minX = aabb.minX * 16;
        double minY = aabb.minY * 16;
        double minZ = aabb.minZ * 16;
        double maxX = aabb.maxX * 16;
        double maxY = aabb.maxY * 16;
        double maxZ = aabb.maxZ * 16;

        return switch (rotation) {
            case CLOCKWISE_90 -> Block.box(minZ, minY, 16 - maxX, maxZ, maxY, 16 - minX);
            case CLOCKWISE_180 -> Block.box(16 - maxX, minY, 16 - maxZ, 16 - minX, maxY, 16 - minZ);
            case COUNTERCLOCKWISE_90 -> Block.box(16 - maxZ, minY, minX, 16 - minZ, maxY, maxX);
            default -> Block.box(minX, minY, minZ, maxX, maxY, maxZ);
        };
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}