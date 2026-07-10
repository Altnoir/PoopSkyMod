package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.particle.LeavesParticleOptions;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.mojang.serialization.MapCodec;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class PParticles {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<ParticleType<?>, SimpleParticleType> POOP_PARTICLE = REGISTRATE.simple(
            "poop_particle",
            Registries.PARTICLE_TYPE,
            () -> new SimpleParticleType(false)
    );

    public static final RegistryEntry<ParticleType<?>, ParticleType<LeavesParticleOptions>> LEAVES_PARTICLE = REGISTRATE.simple(
            "leaves_particle",
            Registries.PARTICLE_TYPE,
            PParticles::createLeavesParticleType
    );

    private static ParticleType<LeavesParticleOptions> createLeavesParticleType() {
        return new ParticleType<>(false) {
            @Override
            public MapCodec<LeavesParticleOptions> codec() {
                return LeavesParticleOptions.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, LeavesParticleOptions> streamCodec() {
                return LeavesParticleOptions.STREAM_CODEC;
            }
        };
    }

    public static void register() {
    }
}
