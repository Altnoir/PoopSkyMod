package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.particle.LeavesParticleOptions;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.mojang.serialization.MapCodec;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class PoParticles {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<ParticleType<?>, SimpleParticleType> POOP_PARTICLE = registerSimpleParticle(
            "poop_particle");
    public static final RegistryEntry<ParticleType<?>, SimpleParticleType> TOILET_PARTICLE = registerSimpleParticle(
            "toilet_particle");

    public static final RegistryEntry<ParticleType<?>, ParticleType<LeavesParticleOptions>> LEAVES_PARTICLE = registerParticle(
            "leaves_particle", PoParticles::createLeavesParticleType);

    private static RegistryEntry<ParticleType<?>, SimpleParticleType> registerSimpleParticle(String name) {
        return REGISTRATE.simple(name, Registries.PARTICLE_TYPE, () -> new SimpleParticleType(false));
    }

    private static <T extends ParticleType<?>> RegistryEntry<ParticleType<?>, T> registerParticle(String name, NonNullSupplier<T> type) {
        return REGISTRATE.simple(name, Registries.PARTICLE_TYPE, type);
    }

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