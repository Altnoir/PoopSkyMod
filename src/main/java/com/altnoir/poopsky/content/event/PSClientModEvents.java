package com.altnoir.poopsky.content.event;

import com.altnoir.poopsky.content.block.renderer.SieveBlockEntityRenderer;
import com.altnoir.poopsky.content.entity.model.FlyModel;
import com.altnoir.poopsky.content.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.init.PBlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class PSClientModEvents {
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ToiletPlugModel.LAYER_LOCATION, ToiletPlugModel::createBodyLayer);
        event.registerLayerDefinition(FlyModel.LAYER_LOCATION, FlyModel::createBodyLayer);
    }

    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PBlockEntityType.SIEVE_BLOCK_ENTITY.get(), SieveBlockEntityRenderer::new);
    }
}