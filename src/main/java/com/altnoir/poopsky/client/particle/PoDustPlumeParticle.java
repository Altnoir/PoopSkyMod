package com.altnoir.poopsky.client.particle;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoVanillaBackport;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PoopSky.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PoDustPlumeParticle extends BaseAshSmokeParticle {
    public PoDustPlumeParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, 0.7F, 0.6F, 0.7F, dx, dy + 0.15F, dz, 1, sprites, 0.5F, 7, 0.5F, false);
        float shade = random.nextFloat() * 0.2F;
        rCol = ((12235202 >> 16) & 255) / 255.0F - shade;
        gCol = ((12235202 >> 8) & 255) / 255.0F - shade;
        bCol = (12235202 & 255) / 255.0F - shade;
    }
    @Override public void tick() { gravity *= 0.88F; friction *= 0.92F; super.tick(); }
    @SubscribeEvent public static void register(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(PoVanillaBackport.DUST_PLUME.get(), sprites -> (type, level, x, y, z, dx, dy, dz) -> new PoDustPlumeParticle(level, x, y, z, dx, dy, dz, sprites));
    }
}
