package com.altnoir.poopsky.block.p;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class PoopLeavesBlock extends LeavesBlock {
    private final Supplier<? extends ParticleOptions> particleOption;

    public PoopLeavesBlock(Supplier<? extends ParticleOptions> particleOption, Properties properties) {
        super(properties);
        this.particleOption = particleOption;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(10) == 0) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (!isFaceFull(blockstate.getCollisionShape(level, blockpos), Direction.UP)) {
                ParticleUtils.spawnParticleBelow(level, pos, random, this.particleOption.get());
            }
        }
    }
}