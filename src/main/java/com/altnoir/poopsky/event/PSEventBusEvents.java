package com.altnoir.poopsky.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.PSEntities;
import com.altnoir.poopsky.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.entity.p.PooplimeEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = PoopSky.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PSEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ToiletPlugModel.LAYER_LOCATION, ToiletPlugModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(PSEntities.POOPLIME.get(), PooplimeEntity.createAttributes().build());
    }
}
