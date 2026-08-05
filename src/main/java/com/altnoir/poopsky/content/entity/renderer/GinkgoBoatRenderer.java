package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.mixin.BoatRendererAccessor;
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

import java.util.HashMap;
import java.util.Map;

public class GinkgoBoatRenderer extends BoatRenderer {
    public static final ModelLayerLocation BOAT_LAYER = new ModelLayerLocation(PoopSky.loc("boat/ginkgo"), "main");
    public static final ModelLayerLocation CHEST_BOAT_LAYER = new ModelLayerLocation(PoopSky.loc("chest_boat/ginkgo"), "main");
    private static final ResourceLocation BOAT_TEXTURE = PoopSky.loc("textures/entity/boat/ginkgo.png");
    private static final ResourceLocation CHEST_BOAT_TEXTURE = PoopSky.loc("textures/entity/chest_boat/ginkgo.png");

    private GinkgoBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context, chestBoat);
        Pair<ResourceLocation, ListModel<Boat>> resource = Pair.of(
                chestBoat ? CHEST_BOAT_TEXTURE : BOAT_TEXTURE,
                chestBoat
                        ? new ChestBoatModel(context.bakeLayer(CHEST_BOAT_LAYER))
                        : new BoatModel(context.bakeLayer(BOAT_LAYER))
        );
        BoatRendererAccessor accessor = (BoatRendererAccessor) this;
        Map<Boat.Type, Pair<ResourceLocation, ListModel<Boat>>> resources =
                new HashMap<>(accessor.poopsky$getBoatResources());
        resources.put(Boat.Type.OAK, resource);
        accessor.poopsky$setBoatResources(resources);
    }

    public static <T extends Boat> NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>> provider(boolean chestBoat) {
        return context -> (EntityRenderer<? super T>) new GinkgoBoatRenderer(context, chestBoat);
    }
}
