package com.altnoir.poopsky.particle;

import com.altnoir.poopsky.PoopSky;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PSParticle {
   public static final SimpleParticleType POOP_PARTICLE = registerParticle("poop_particle", FabricParticleTypes.simple());

    private static SimpleParticleType registerParticle(String name, SimpleParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(PoopSky.MOD_ID, name), particleType);
    }
    public static void registerParticles() {
        PoopSky.LOGGER.info("Registering Particles for " + PoopSky.MOD_ID);
    }
}
