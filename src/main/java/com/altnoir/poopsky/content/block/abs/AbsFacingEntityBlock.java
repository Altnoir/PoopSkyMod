package com.altnoir.poopsky.content.block.abs;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public abstract class AbsFacingEntityBlock extends BaseEntityBlock {
    protected AbsFacingEntityBlock(Properties properties) {
        super(properties);
    }

    /**
     * 子类需返回具体的朝向属性（全方向或水平方向）
     */
    protected abstract EnumProperty<Direction> getFacingProperty();

    /**
     * 子类需根据放置上下文决定初始朝向
     */
    protected abstract Direction getPlacementDirection(BlockPlaceContext context);

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var player = context.getPlayer();
        boolean isSneaking = player != null && player.isShiftKeyDown();
        Direction direction = getPlacementDirection(context);
        if (isSneaking) {
            direction = direction.getOpposite();
        }
        return defaultBlockState().setValue(getFacingProperty(), direction);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(getFacingProperty(), rotation.rotate(state.getValue(getFacingProperty())));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }
        return rotate(state, mirror.getRotation(state.getValue(getFacingProperty())));
    }
}
