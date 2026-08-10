package com.altnoir.poopsky.content;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.List;

public final class GachaPool {
    private static final List<ResourceLocation> ENTITY_IDS = List.of(
            ResourceLocation.withDefaultNamespace("cow"),
            ResourceLocation.withDefaultNamespace("chicken"),
            ResourceLocation.withDefaultNamespace("pig"),
            PoopSky.loc("fly")
    );

    private GachaPool() {
    }

    public static ResourceLocation random(RandomSource random) {
        return ENTITY_IDS.get(random.nextInt(ENTITY_IDS.size()));
    }

    public static boolean contains(ResourceLocation id) {
        return ENTITY_IDS.contains(id);
    }
}
