package com.altnoir.poopsky.content.block.abs;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class AbsHorDirEntityBlock extends AbsFacingEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected AbsHorDirEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected EnumProperty<Direction> getFacingProperty() {
        return FACING;
    }

    @Override
    protected Direction getPlacementDirection(BlockPlaceContext context) {
        return context.getHorizontalDirection().getOpposite();
    }
}
