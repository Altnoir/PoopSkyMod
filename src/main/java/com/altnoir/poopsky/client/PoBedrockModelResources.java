package com.altnoir.poopsky.client;

import com.altnoir.poopsky.PoopSky;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterBedrockModelEvent;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.RawResourceLoaders;
import net.minecraft.resources.Identifier;

public final class PoBedrockModelResources {
    public static final Identifier FLUSH_TOILET_CART = PoopSky.loc("flush_toilet_cart.geo");

    private PoBedrockModelResources() {
    }

    public static void onRegisterBedrockModels(RegisterBedrockModelEvent event) {
        event.register(FLUSH_TOILET_CART, RawResourceLoaders.COMMON_LOADER);
    }
}
