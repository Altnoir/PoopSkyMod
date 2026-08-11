package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoParticles;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.data.ParticleDescriptionProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ParticleGen extends ParticleDescriptionProvider {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private ParticleGen(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    public static void register() {
        REGISTRATE.addDataGenerator(
                ProviderType.GENERIC_CLIENT,
                provider -> provider.add(data -> new ParticleGen(data.output(), data.existingFileHelper())));
    }

    @Override
    protected void addDescriptions() {
        spriteSet(PoParticles.POOP_PARTICLE.get(), PoopSky.loc("poop_particle"));
        spriteSet(PoParticles.TOILET_PARTICLE.get(), PoopSky.loc("poop_particle"));
        spriteSet(PoParticles.LEAVES_PARTICLE.get(), PoopSky.loc("leaves"), 12, false);
    }
}
