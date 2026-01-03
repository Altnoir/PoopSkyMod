package com.altnoir.poopsky.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

public abstract class AbstractCompooperBlock extends Block {
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 3;
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", MIN_LEVEL, MAX_LEVEL);

    public AbstractCompooperBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return blockState.getValue(LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, LEVEL);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        boolean hasPower = level.hasNeighborSignal(pos);
        if (hasPower != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, hasPower), Block.UPDATE_ALL);

            if (hasPower && !level.isClientSide) {
                var be = level.getBlockEntity(pos.below());

                if (!(be instanceof Container container)) return;

                for (int slot = 0; slot < container.getContainerSize(); slot++) {
                    var stack = container.getItem(slot);
                    if (stack.isEmpty()) continue;

                    var block = Block.byItem(stack.getItem());
                    if (block == null || block.defaultBlockState().isAir()) {
                        if (stack.getItem() instanceof BlockItem bi) {
                            block = bi.getBlock();
                        }
                    }

                    if (block == null || block.defaultBlockState().isAir()) continue;

                    var targetPos = pos.above();
                    var targetState = level.getBlockState(targetPos);

                    if (!level.isOutsideBuildHeight(targetPos) && targetState.canBeReplaced()) {
                        if (level.setBlock(targetPos, block.defaultBlockState(), Block.UPDATE_ALL)) {
                            try {
                                var placeSound = block.defaultBlockState().getSoundType().getPlaceSound();
                                level.playSound(null, targetPos, placeSound, SoundSource.BLOCKS, 1.0F, 1.0F);
                            } catch (Exception e) {
                                level.playSound(null, targetPos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                            }
                            container.removeItem(slot, 1);
                            break;
                        }
                    } else {
                        System.out.println(targetState.getBlock().getName().getString() + " False");
                        break;
                    }
                }
            }
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }


    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.2F;
    }
}
