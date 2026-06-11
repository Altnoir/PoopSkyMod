package com.altnoir.poopsky.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class PoopLiquidBlock extends LiquidBlock {
    public PoopLiquidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    protected void check(Level level, BlockPos pos) {
        //hasBlockNeighbor(level, pos, Blocks.NETHERRACK, Blocks.MAGMA_BLOCK, Blocks.LAVA);
        //hasBlockNeighbor(level, pos, Blocks.CLAY, Blocks.CLAY, Blocks.WATER);
        hasBlockNeighbor(level, pos, Blocks.SOUL_SAND, Blocks.SAND);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        check(level, pos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        check(level, pos);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (entity instanceof LivingEntity livingEntity) {
            if (!livingEntity.hasEffect(MobEffects.POISON)) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 60));
            }
            if (livingEntity instanceof ServerPlayer) {
                MobEffectInstance confusionEffect = livingEntity.getEffect(MobEffects.CONFUSION);

                if (!livingEntity.hasEffect(MobEffects.CONFUSION) | (confusionEffect != null && confusionEffect.getDuration() <= 100)) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200));
                }
            }
        }
    }

    private void hasBlockNeighbor(Level level, BlockPos pos, Block block, Block blockDown, Block liquid) {
        if (liquidNeighbor(level, pos, Direction.UP, liquid)) {
            level.levelEvent(1501, pos, 0);
            level.setBlockAndUpdate(pos, blockDown.defaultBlockState());
        } else {
            hasBlockNeighbor(level, pos, block, liquid);
        }
    }

    private void hasBlockNeighbor(Level level, BlockPos pos, Block block, Block liquid) {
        BlockPos neighborPos = getLiquidNeighborPos(level, pos, liquid);
        if (neighborPos != null) {
            level.levelEvent(1501, neighborPos, 0);
            BlockState neighborState = level.getBlockState(neighborPos);
            Block resultBlock = liquid == Blocks.LAVA && neighborState.getFluidState().isSource()
                    ? Blocks.OBSIDIAN
                    : block;
            level.setBlockAndUpdate(neighborPos, resultBlock.defaultBlockState());
        }
    }

    private BlockPos getLiquidNeighborPos(Level level, BlockPos pos, Block liquid) {
        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP) continue;
            if (liquidNeighbor(level, pos, direction, liquid)) {
                return pos.relative(direction);
            }
        }
        return null;
    }

    private boolean liquidNeighbor(Level level, BlockPos pos, Direction direction, Block liquid) {
        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);
        return neighborState.getBlock() == liquid;
    }

}
