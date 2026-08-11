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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.vehicle.Boat;

public class GinkgoBoatRenderer extends BoatRenderer {
    public static final ModelLayerLocation BOAT_LAYER = new ModelLayerLocation(PoopSky.loc("boat/ginkgo"), "main");
    public static final ModelLayerLocation CHEST_BOAT_LAYER = new ModelLayerLocation(PoopSky.loc("chest_boat/ginkgo"), "main");
    private static final Identifier BOAT_TEXTURE = PoopSky.loc("textures/entity/boat/ginkgo.png");
    private static final Identifier CHEST_BOAT_TEXTURE = PoopSky.loc("textures/entity/chest_boat/ginkgo.png");

    private final Pair<Identifier, ListModel<Boat>> resource;

    private GinkgoBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context, chestBoat);
        this.resource = Pair.of(
                chestBoat ? CHEST_BOAT_TEXTURE : BOAT_TEXTURE,
                chestBoat
                        ? new ChestBoatModel(context.bakeLayer(CHEST_BOAT_LAYER))
                        : new BoatModel(context.bakeLayer(BOAT_LAYER))
        );
    }

    @Override
    public Pair<Identifier, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return resource;
    }

    public static <T extends Boat> NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>> provider(boolean chestBoat) {
        return context -> (EntityRenderer<? super T>) new GinkgoBoatRenderer(context, chestBoat);
    }
}
