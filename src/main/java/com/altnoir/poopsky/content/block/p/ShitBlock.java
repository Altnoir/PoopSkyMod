package com.altnoir.poopsky.content.block.p;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class ShitBlock extends Block implements Equipable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(3, 0, 3, 13, 3, 13),
            Block.box(4, 3, 4, 12, 6, 12),
            Block.box(5, 6, 5, 11, 8, 11),
            Block.box(6, 8, 6, 9, 11, 9)
    );

    private static final Map<Direction, VoxelShape> SHAPES = computeShapes(NORTH_SHAPE);

    public ShitBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    public static boolean isWearing(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ShitBlock;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    private static Map<Direction, VoxelShape> computeShapes(VoxelShape northShape) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        for (Direction direction : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
            map.put(direction, rotateShape(northShape, direction));
        }
        return map;
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
        if (direction == Direction.NORTH) return shape;
        return shape.toAabbs().stream()
                .map(aabb -> rotateAABB(aabb, direction))
                .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
                .orElse(Shapes.empty());
    }

    private static VoxelShape rotateAABB(AABB aabb, Direction direction) {
        double minX = aabb.minX * 16;
        double minY = aabb.minY * 16;
        double minZ = aabb.minZ * 16;
        double maxX = aabb.maxX * 16;
        double maxY = aabb.maxY * 16;
        double maxZ = aabb.maxZ * 16;

        return switch (direction) {
            case EAST -> Block.box(16 - maxZ, minY, minX, 16 - minZ, maxY, maxX);
            case SOUTH -> Block.box(16 - maxX, minY, 16 - maxZ, 16 - minX, maxY, 16 - minZ);
            case WEST -> Block.box(minZ, minY, 16 - maxX, maxZ, maxY, 16 - minX);
            default -> Block.box(minX, minY, minZ, maxX, maxY, maxZ);
        };
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
