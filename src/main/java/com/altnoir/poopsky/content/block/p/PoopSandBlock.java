package com.altnoir.poopsky.content.block.p;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class PoopSandBlock extends ColoredFallingBlock {
    public PoopSandBlock(ColorRGBA dustColor, Properties properties) {
        super(dustColor, properties);
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
        if (shouldSolidify(level, pos, state, replaceableState.getFluidState())) {
            setBlock(level, pos);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter blockgetter = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = blockgetter.getBlockState(blockpos);
        if (shouldSolidify(blockgetter, blockpos, blockstate)) {
            Level level = context.getLevel();
            level.levelEvent(1501, blockpos, 0);
            return Blocks.SOUL_SAND.defaultBlockState();
        } else {
            return super.getStateForPlacement(context);
        }
    }

    private static void setBlock(Level level, BlockPos pos) {
        level.levelEvent(1501, pos, 0);
        level.setBlock(pos, Blocks.SOUL_SAND.defaultBlockState(), 3);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return shouldSolidify(level, currentPos, state) ? Blocks.SOUL_SAND.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    private static boolean shouldSolidify(BlockGetter level, BlockPos pos, BlockState state) {
        return isLava(level.getFluidState(pos)) || touchesLava(level, pos);
    }

    private static boolean shouldSolidify(BlockGetter level, BlockPos pos, BlockState state, FluidState fluidState) {
        return isLava(fluidState) || touchesLava(level, pos);
    }

    private static boolean touchesLava(BlockGetter level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.relative(direction);
            if (isLava(level.getFluidState(neighbor))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLava(FluidState fluidState) {
        return fluidState.is(FluidTags.LAVA);
    }
}
