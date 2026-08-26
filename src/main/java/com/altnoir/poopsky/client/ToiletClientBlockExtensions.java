package com.altnoir.poopsky.client;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class ToiletClientBlockExtensions {
    private static final ThreadLocal<ResourceLocation> DESTROY_PARTICLE_TEXTURE = new ThreadLocal<>();

    private ToiletClientBlockExtensions() {
    }

    public static BlockState getParticleState(BlockState state, Level level, BlockPos pos) {
        if (state.getBlock() instanceof AbstractToiletBlock toilet) {
            return toilet.getParticleState(state, level, pos);
        }
        return state;
    }

    public static void beginDestroyParticles(BlockState state, Level level, BlockPos pos) {
        if (!(state.getBlock() instanceof AbstractToiletBlock toilet)) return;

        ToiletType type = toilet.getToiletTypeOrDefault(level, pos);
        String texture = type.texture();
        if (texture == null) return;

        Block sourceBlock = type.sourceBlock();
        String namespace = sourceBlock == null
                ? PoopSky.MOD_ID
                : BuiltInRegistries.BLOCK.getKey(sourceBlock).getNamespace();
        DESTROY_PARTICLE_TEXTURE.set(ResourceLocation.fromNamespaceAndPath(namespace, "block/" + texture));
    }

    public static void endDestroyParticles() {
        DESTROY_PARTICLE_TEXTURE.remove();
    }

    public static @Nullable ResourceLocation getDestroyParticleTexture() {
        return DESTROY_PARTICLE_TEXTURE.get();
    }
}
