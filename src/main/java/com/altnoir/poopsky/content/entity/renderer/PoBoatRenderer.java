package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class PoBoatRenderer extends BoatRenderer {
    private final Pair<ResourceLocation, ListModel<Boat>> resource;

    private PoBoatRenderer(EntityRendererProvider.Context context, String wood, boolean chestBoat) {
        super(context, chestBoat);
        ResourceLocation texture = PoopSky.loc("textures/entity/" + (chestBoat ? "chest_boat/" : "boat/") + wood + ".png");
        ModelLayerLocation layer = layer(wood, chestBoat);
        this.resource = Pair.of(
                texture,
                chestBoat
                        ? new ChestBoatModel(context.bakeLayer(layer))
                        : new BoatModel(context.bakeLayer(layer))
        );
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return resource;
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

    public static <T extends Boat> NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>> provider(String wood, boolean chestBoat) {
        return context -> (EntityRenderer<? super T>) new PoBoatRenderer(context, wood, chestBoat);
    }
}
