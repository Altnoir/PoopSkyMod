package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoParticles;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

public class ParticleGen extends ParticleDescriptionProvider {
    public ParticleGen(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    @Override
    protected void addDescriptions() {
        spriteSet(PoParticles.POOP_PARTICLE.get(), PoopSky.loc("poop_particle"));
        spriteSet(PoParticles.LEAVES_PARTICLE.get(), PoopSky.loc("leaves"), 12, false);
    }
}
