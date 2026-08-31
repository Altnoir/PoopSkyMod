package com.altnoir.poopsky.client;

import com.altnoir.poopsky.PoopSky;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterV2BedrockResourcesEvent;
import net.minecraft.resources.ResourceLocation;

public final class PoBedrockModelResources {
    public static final ResourceLocation FLUSH_TOILET_CART = PoopSky.loc("flush_toilet_cart.geo");
    public static final ResourceLocation GACHAPON = PoopSky.loc("gachapon.geo");
    public static final ResourceLocation BASILISK_MODEL = PoopSky.loc("basilisk.geo");
    public static final ResourceLocation BASILISK_ANIMATION = PoopSky.loc("basilisk.animation");
    public static final ResourceLocation SNAIL_MODEL = PoopSky.loc("snail.geo");
    public static final ResourceLocation SNAIL_ANIMATION = PoopSky.loc("snail.animation");

    private PoBedrockModelResources() {
    }

    public static void onRegisterV2BedrockResources(RegisterV2BedrockResourcesEvent event) {
        event.treeModel(FLUSH_TOILET_CART)
                .register();
        event.bakedModel(GACHAPON)
                .register();
        event.bakedModel(BASILISK_MODEL)
                .animation(BASILISK_ANIMATION)
                .register();
        event.bakedModel(SNAIL_MODEL)
                .animation(SNAIL_ANIMATION)
                .register();
    }
}
