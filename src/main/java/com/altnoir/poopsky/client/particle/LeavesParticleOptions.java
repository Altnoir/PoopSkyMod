package com.altnoir.poopsky.client.particle;

import com.altnoir.poopsky.init.PParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LeavesParticleOptions(int color) implements ParticleOptions {
    public static final MapCodec<LeavesParticleOptions> CODEC = Codec.INT
            .fieldOf("color")
            .xmap(LeavesParticleOptions::new, LeavesParticleOptions::color);
    public static final StreamCodec<? super RegistryFriendlyByteBuf, LeavesParticleOptions> STREAM_CODEC = ByteBufCodecs.INT
            .map(LeavesParticleOptions::new, LeavesParticleOptions::color);

    @Override
    public ParticleType<LeavesParticleOptions> getType() {
        return PParticles.LEAVES_PARTICLE.get();
    }
}
