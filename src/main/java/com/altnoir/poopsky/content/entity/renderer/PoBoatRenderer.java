package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

public class PoBoatRenderer extends BoatRenderer {
    private PoBoatRenderer(EntityRendererProvider.Context context, String wood, boolean chestBoat) {
        super(context, layer(wood, chestBoat));
    }

    public static ModelLayerLocation layer(String wood, boolean chestBoat) {
        return new ModelLayerLocation(PoopSky.loc((chestBoat ? "chest_boat/" : "boat/") + wood), "main");
    }

    public static ModelLayerLocation boatLayer(String wood) {
        return layer(wood, false);
    }

    public static ModelLayerLocation chestBoatLayer(String wood) {
        return layer(wood, true);
    }

    public static <T extends AbstractBoat> NonNullFunction<
            EntityRendererProvider.Context,
            EntityRenderer<? super T, ?>> provider(String wood, boolean chestBoat) {
        return context -> new PoBoatRenderer(context, wood, chestBoat);
    }
}
