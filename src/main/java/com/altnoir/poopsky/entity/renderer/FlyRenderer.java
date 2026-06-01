package com.altnoir.poopsky.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.model.FlyModel;
import com.altnoir.poopsky.entity.p.FlyEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FlyRenderer extends MobRenderer<FlyEntity, FlyModel<FlyEntity>> {


    public FlyRenderer(EntityRendererProvider.Context context) {
        super(context, new FlyModel<>(context.bakeLayer(ModelLayers.BEE)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(FlyEntity entity) {
        return PoopSky.loc("textures/entity/fly.png");
    }
}
