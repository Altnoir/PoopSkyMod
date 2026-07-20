package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

import java.util.ArrayList;
import java.util.List;

public class PoPainting {
    public static final List<ResourceKey<PaintingVariant>> PAINTING_VARIANTS = new ArrayList<>();

    public static final ResourceKey<PaintingVariant> POOP = create("poop");

    public static void bootstrap(BootstrapContext<PaintingVariant> context) {
        register(context, POOP, 2, 2);
    }

    private static void register(BootstrapContext<PaintingVariant> context, ResourceKey<PaintingVariant> key, int width, int height) {
        PAINTING_VARIANTS.add(key);
        context.register(key, new PaintingVariant(width, height, key.location()));
    }

    private static ResourceKey<PaintingVariant> create(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, PoopSky.loc(name));
    }
}
