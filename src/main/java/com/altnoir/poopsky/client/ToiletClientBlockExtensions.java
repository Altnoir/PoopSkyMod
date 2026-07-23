package com.altnoir.poopsky.client;

import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ToiletClientBlockExtensions {
    private ToiletClientBlockExtensions() {
    }

    public static BlockState getParticleState(BlockState state, Level level, BlockPos pos) {
        if (state.getBlock() instanceof AbstractToiletBlock toilet) {
            return toilet.getParticleState(state, level, pos);
        }
        return state;
    }
}
