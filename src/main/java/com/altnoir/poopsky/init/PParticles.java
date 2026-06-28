package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.particle.LeavesParticleOptions;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, PoopSky.MOD_ID);

    public static final Supplier<SimpleParticleType> POOP_PARTICLE = PARTICLE_TYPES.register("poop_particle", () ->
            new SimpleParticleType(false));
    public static final Supplier<ParticleType<LeavesParticleOptions>> LEAVES_PARTICLE = PARTICLE_TYPES.register("leaves_particle", () ->
            new ParticleType<>(false) {
                @Override
                public MapCodec<LeavesParticleOptions> codec() {
                    return LeavesParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, LeavesParticleOptions> streamCodec() {
                    return LeavesParticleOptions.STREAM_CODEC;
                }
            });

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
