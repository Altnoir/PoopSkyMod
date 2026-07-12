package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoParticles;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

public class PSParticleProvider extends ParticleDescriptionProvider {
    protected PSParticleProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    @Override
    protected void addDescriptions() {
        spriteSet(PoParticles.POOP_PARTICLE.get(), PoopSky.loc("poop_particle"));
        spriteSet(PoParticles.LEAVES_PARTICLE.get(), PoopSky.loc("leaves"), 12, false);
    }
}
