package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.fabric.port.data.ParticleDescriptionProvider;
import com.altnoir.poopsky.init.PoParticles;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class ParticleGen extends ParticleDescriptionProvider {
    public ParticleGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addDescriptions() {
        spriteSet(PoParticles.POOP_PARTICLE.get(), PoopSky.loc("poop_particle"));
        spriteSet(PoParticles.TOILET_PARTICLE.get(), PoopSky.loc("poop_particle"));
        spriteSet(PoParticles.LEAVES_PARTICLE.get(), PoopSky.loc("leaves"), 12, false);
    }
}
