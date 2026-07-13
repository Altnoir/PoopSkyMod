package com.altnoir.poopsky.impl.type.damageType;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.ArrayList;
import java.util.List;

public class PoDamageTypes {
    private static final List<ResourceKey<DamageType>> KEYS = new ArrayList<>();

    public static final ResourceKey<DamageType> ROUNDWORM = create("roundworm");
    public static final ResourceKey<DamageType> POOP_BALL = create("poop_ball");

    private static ResourceKey<DamageType> create(String path) {
        var key = ResourceKey.create(Registries.DAMAGE_TYPE, PoopSky.loc(path));
        KEYS.add(key);
        return key;
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        for (var key : KEYS) {
            context.register(key, new DamageType(key.location().getPath(), 0.1F));
        }
    }
}