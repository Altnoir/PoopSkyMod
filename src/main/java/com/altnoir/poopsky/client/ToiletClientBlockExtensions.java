package com.altnoir.poopsky.client;

import com.altnoir.poopsky.content.block.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

import java.util.HashMap;
import java.util.Map;

public class ToiletClientBlockExtensions implements IClientBlockExtensions {
    public static final ToiletClientBlockExtensions INSTANCE = new ToiletClientBlockExtensions();
    private static final long CACHE_TTL = 20L;

    private final Map<BlockPos, BlockState> pendingDestroyParticles = new HashMap<>();
    private final Map<BlockPos, CachedToiletType> pendingToiletTypes = new HashMap<>();

    private ToiletClientBlockExtensions() {
    }

    @Override
    public boolean playBreakSound(BlockState state, Level level, BlockPos pos) {
        cacheToiletType(state, level, pos);
        BlockState particleState = getParticleState(state, level, pos);
        if (particleState != state) {
            pendingDestroyParticles.put(pos.immutable(), particleState);
        }
        return false;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        BlockState particleState = pendingDestroyParticles.remove(pos);
        if (particleState == null) {
            particleState = getParticleState(state, level, pos);
        }
        if (particleState == state) {
            return false;
        }
        manager.destroy(pos, particleState);
        return true;
    }

    public static ToiletType getCachedToiletType(Level level, BlockPos pos) {
        CachedToiletType cached = INSTANCE.pendingToiletTypes.get(pos);
        if (cached == null) {
            return null;
        }
        if (level.getGameTime() - cached.gameTime > CACHE_TTL) {
            INSTANCE.pendingToiletTypes.remove(pos);
            return null;
        }
        return cached.toiletType;
    }

    private void cacheToiletType(BlockState state, Level level, BlockPos pos) {
        if (state.getBlock() instanceof AbstractToiletBlock toilet) {
            pendingToiletTypes.put(pos.immutable(), new CachedToiletType(toilet.getToiletTypeOrDefault(level, pos), level.getGameTime()));
        }
    }

    private static BlockState getParticleState(BlockState state, Level level, BlockPos pos) {
        if (state.getBlock() instanceof AbstractToiletBlock toilet) {
            return toilet.getParticleState(state, level, pos);
        }
        return state;
    }

    private record CachedToiletType(ToiletType toiletType, long gameTime) {
    }
}
