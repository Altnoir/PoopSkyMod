package com.altnoir.poopsky.client;

import com.altnoir.poopsky.PoopSky;
import com.github.mcmodderanchor.simplebedrockmodel.v1.event.RegisterBedrockModelEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v1.resource.RawResourceLoaders;
import net.minecraft.resources.ResourceLocation;

public final class PoBedrockModelResources {
    public static final ResourceLocation FLUSH_TOILET_CART = PoopSky.loc("flush_toilet_cart.geo");
    public static final ResourceLocation GACHAPON = PoopSky.loc("gachapon.geo");

    private PoBedrockModelResources() {
    }

    public static void onRegisterBedrockModels(RegisterBedrockModelEvent event) {
        event.register(FLUSH_TOILET_CART, RawResourceLoaders.COMMON_LOADER);
        event.register(GACHAPON, RawResourceLoaders.COMMON_LOADER);
    }
}