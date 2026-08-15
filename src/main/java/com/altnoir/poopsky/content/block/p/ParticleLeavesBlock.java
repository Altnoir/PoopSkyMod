package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.client.particle.LeavesParticleOptions;
import com.altnoir.poopsky.impl.util.FireSafetyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ParticleLeavesBlock extends LeavesBlock {
    private final LeavesParticleOptions particleOption;

    public ParticleLeavesBlock(int particleColor, Properties properties) {
        super(properties);
        this.particleOption = new LeavesParticleOptions(particleColor);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(10) == 0) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (!isFaceFull(blockstate.getCollisionShape(level, blockpos), Direction.UP)) {
                ParticleUtils.spawnParticleBelow(level, pos, random, this.particleOption);
            }
        }
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return FireSafetyUtil.isFlammable(state);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return FireSafetyUtil.getFlammability(state, 60);
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return FireSafetyUtil.getFireSpreadSpeed(state, 30);
    }
}