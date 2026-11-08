package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class PoPainting {
    private static final DeferredRegister<PaintingVariant> VARIANTS = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, PoopSky.MOD_ID);
    public static final RegistryObject<PaintingVariant> POOP = VARIANTS.register("poop", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> POOP_KING = VARIANTS.register("poop_king", () -> new PaintingVariant(32, 32));
    public static final RegistryObject<PaintingVariant> TOILET = VARIANTS.register("toilet", () -> new PaintingVariant(64, 48));
    public static final RegistryObject<PaintingVariant> VIP = VARIANTS.register("vip", () -> new PaintingVariant(48, 32));
    public static void register(IEventBus bus) { VARIANTS.register(bus); }
}
