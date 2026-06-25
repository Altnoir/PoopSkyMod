package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, PoopSky.MOD_ID);

    public static final Supplier<SimpleParticleType> POOP_PARTICLE = PARTICLE_TYPES.register("poop_particle", () ->
            new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> LEAVES_PARTICLE_BROWN = PARTICLE_TYPES.register("leaves_particle_brown", () ->
            new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> LEAVES_PARTICLE_WHITE = PARTICLE_TYPES.register("leaves_particle_white", () ->
            new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> LEAVES_PARTICLE_YELLOW = PARTICLE_TYPES.register("leaves_particle_yellow", () ->
            new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}