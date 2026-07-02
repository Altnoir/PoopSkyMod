package com.altnoir.poopsky.common.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.entity.model.FlyModel;
import com.altnoir.poopsky.common.entity.p.FlyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FlyRenderer extends MobRenderer<FlyEntity, FlyModel<FlyEntity>> {


    public FlyRenderer(EntityRendererProvider.Context context) {
        super(context, new FlyModel<>(context.bakeLayer(FlyModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(FlyEntity entity) {
        return PoopSky.loc("textures/entity/fly.png");
    }
}
