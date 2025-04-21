package com.altnoir.poopsky.Fluid;

import com.altnoir.poopsky.block.PSBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class UrineFluid extends Urine{
    @Override
    public Fluid getStill() {
        return PSFluids.URINE;
    }

    @Override
    public Fluid getFlowing() {
        return PSFluids.FLOWING_URINE;
    }

    @Override
    public Item getBucketItem() {
        return PSFluids.URINE_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return PSBlocks.URINE_BLOCK.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
    }
    public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
        BlockPos blockPos = pos.up();
        if (world.getBlockState(blockPos).isAir() && !world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) {
            if (random.nextInt(100) == 0) {
                world.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, SoundCategory.BLOCKS, 1.0F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }
    }


    public static class Flowing extends UrineFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        protected boolean isInfinite(World world) {
            return false;
        }


        @Override
        protected int getMaxFlowDistance(WorldView world) {
            return 0;
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends UrineFluid {


        @Override
        protected boolean isInfinite(World world) {
            return false;
        }

        @Override
        protected int getMaxFlowDistance(WorldView world) {
            return 0;
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}
