package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PParticles;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

public class PSParticleProvider extends ParticleDescriptionProvider {
    protected PSParticleProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    @Override
    protected void addDescriptions() {
        spriteSet(PParticles.POOP_PARTICLE.get(), PoopSky.loc("poop_particle"));
        spriteSet(PParticles.LEAVES_PARTICLE_BROWN.get(), PoopSky.loc("leaves"), 12, false);
        spriteSet(PParticles.LEAVES_PARTICLE_WHITE.get(), PoopSky.loc("leaves"), 12, false);
        spriteSet(PParticles.LEAVES_PARTICLE_YELLOW.get(), PoopSky.loc("leaves"), 12, false);
    }
}