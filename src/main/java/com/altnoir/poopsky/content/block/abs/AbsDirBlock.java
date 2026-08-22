package com.altnoir.poopsky.content.block.abs;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class AbsDirBlock extends AbsFacingBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public AbsDirBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected DirectionProperty getFacingProperty() {
        return FACING;
    }

    @Override
    protected Direction getPlacementDirection(BlockPlaceContext context) {
        return context.getNearestLookingDirection().getOpposite();
    }
}
