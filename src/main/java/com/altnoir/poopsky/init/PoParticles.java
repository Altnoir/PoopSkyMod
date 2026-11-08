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
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;

public final class PoParticles {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<SimpleParticleType> POOP_PARTICLE = registerSimpleParticle(
            "poop_particle");
    public static final RegistryEntry<SimpleParticleType> TOILET_PARTICLE = registerSimpleParticle(
            "toilet_particle");

    public static final RegistryEntry<ParticleType<LeavesParticleOptions>> LEAVES_PARTICLE = registerParticle(
            "leaves_particle", PoParticles::createLeavesParticleType);

    private static RegistryEntry<SimpleParticleType> registerSimpleParticle(String name) {
        return REGISTRATE.simple(name, Registries.PARTICLE_TYPE, () -> new SimpleParticleType(false));
    }

    private static <T extends ParticleType<?>> RegistryEntry<T> registerParticle(String name, NonNullSupplier<T> type) {
        return REGISTRATE.simple(name, Registries.PARTICLE_TYPE, type);
    }

    private static ParticleType<LeavesParticleOptions> createLeavesParticleType() {
        return new ParticleType<>(false, LeavesParticleOptions.DESERIALIZER) {
            @Override
            public com.mojang.serialization.Codec<LeavesParticleOptions> codec() {
                return LeavesParticleOptions.CODEC.codec();
            }
        };
    }

    public static void register() {
    }
}