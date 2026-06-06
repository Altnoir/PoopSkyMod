package com.altnoir.poopsky.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlockEntities;
import com.altnoir.poopsky.block.entity.renderer.SieveBlockEntityRenderer;
import com.altnoir.poopsky.entity.model.FlyModel;
import com.altnoir.poopsky.entity.model.ToiletPlugModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = PoopSky.MOD_ID, value = Dist.CLIENT)
public class PSClientModEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ToiletPlugModel.LAYER_LOCATION, ToiletPlugModel::createBodyLayer);
        event.registerLayerDefinition(FlyModel.LAYER_LOCATION, FlyModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PSBlockEntities.SIEVE_BLOCK_ENTITY.get(), SieveBlockEntityRenderer::new);
    }
}
