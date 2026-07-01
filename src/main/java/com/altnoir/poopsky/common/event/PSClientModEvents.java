package com.altnoir.poopsky.common.event;

import com.altnoir.poopsky.init.PBlockEntityType;
import com.altnoir.poopsky.block.entity.renderer.SieveBlockEntityRenderer;
import com.altnoir.poopsky.entity.model.FlyModel;
import com.altnoir.poopsky.entity.model.ToiletPlugModel;
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
