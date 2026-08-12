package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

public class GinkgoBoatRenderer extends BoatRenderer {
    public static final ModelLayerLocation BOAT_LAYER = new ModelLayerLocation(PoopSky.loc("boat/ginkgo"), "main");
    public static final ModelLayerLocation CHEST_BOAT_LAYER = new ModelLayerLocation(PoopSky.loc("chest_boat/ginkgo"), "main");
    private GinkgoBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context, chestBoat ? CHEST_BOAT_LAYER : BOAT_LAYER);
    }

    public static <T extends AbstractBoat> NonNullFunction<
            EntityRendererProvider.Context,
            EntityRenderer<? super T, ?>> provider(boolean chestBoat) {
        return context -> new GinkgoBoatRenderer(context, chestBoat);
    }
}
