package com.altnoir.poopsky.content.block.p;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class FlushToiletBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<FlushToiletBlock> CODEC = simpleCodec(FlushToiletBlock::new);

    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(4, 0, 3, 12, 5, 13),
            Block.box(4, 5, 2, 12, 8, 13),
            Block.box(4, 6, 12, 12, 16, 16),
            Block.box(4, 8, 11, 12, 18, 12)
    );
    private static final Map<Direction, VoxelShape> SHAPES = computeShapes(NORTH_SHAPE);

    public FlushToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
        return null;
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
}