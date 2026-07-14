package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class FecalFarmlandBlock extends FarmBlock {
    private static final int HARVEST_DELAY = 20;

    public FecalFarmlandBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, MAX_MOISTURE));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? PoBlocks.POOP_BLOCK.get().defaultBlockState()
                : super.getStateForPlacement(context);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.setBlockAndUpdate(pos, PoBlocks.POOP_BLOCK.get().defaultBlockState());
            return;
        }
        tryHarvestCrop(level, pos);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(MOISTURE) < MAX_MOISTURE) {
            level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
        }
        scheduleHarvest(level, pos);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.SHOVEL_FLATTEN) {
            BlockPos holePos = context.getClickedPos().below();
            BlockState holeState = context.getLevel().getBlockState(holePos);
            if (!holeState.isAir() && holeState.getDestroySpeed(context.getLevel(), holePos) < 0.0F) {
                return null;
            }
            if (!simulate && !context.getLevel().isClientSide) {
                if (!holeState.isAir()) {
                    context.getLevel().setBlock(holePos, Blocks.AIR.defaultBlockState(), 3);
                }
                scheduleHarvest(context.getLevel(), context.getClickedPos());
            }
            return state.setValue(MOISTURE, MAX_MOISTURE);
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    public static void tryHarvestCrop(ServerLevel level, BlockPos farmlandPos) {
        if (!level.getBlockState(farmlandPos.below()).isAir()) {
            return;
        }

        BlockPos cropPos = farmlandPos.above();
        BlockState cropState = level.getBlockState(cropPos);
        if (!(cropState.getBlock() instanceof CropBlock cropBlock) || !cropBlock.isMaxAge(cropState)) {
            return;
        }

        for (ItemStack drop : Block.getDrops(cropState, level, cropPos, null)) {
            Block.popResource(level, farmlandPos.below(), drop);
        }
        level.setBlock(cropPos, cropBlock.getStateForAge(0), 2);
    }

    public static void scheduleHarvest(Level level, BlockPos farmlandPos) {
        if (!level.isClientSide
                && level.getBlockState(farmlandPos).is(PoBlocks.FECAL_FARMLAND.get())
                && level.getBlockState(farmlandPos.below()).isAir()
                && isMatureCrop(level, farmlandPos.above())) {
            level.scheduleTick(farmlandPos, PoBlocks.FECAL_FARMLAND.get(), HARVEST_DELAY);
        }
    }

    private static boolean isMatureCrop(Level level, BlockPos cropPos) {
        BlockState cropState = level.getBlockState(cropPos);
        return cropState.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(cropState);
    }
}
