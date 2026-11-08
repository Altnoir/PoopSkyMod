package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.model.FlyModel;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FlyRenderer extends MobRenderer<FlyEntity, FlyModel<FlyEntity>> {


    public FlyRenderer(EntityRendererProvider.Context context) {
        super(context, new FlyModel<>(
                        context.bakeLayer(FlyModel.LAYER_LOCATION),
                        context.bakeLayer(FlyModel.MAGGOT_LAYER_LOCATION).getChild("maggot")
                ), 0.4F
        );
    }

    @Override
    public ResourceLocation getTextureLocation(FlyEntity entity) {
        return entity.isBaby()
                ? PoopSky.loc("textures/entity/maggot.png")
                : PoopSky.loc("textures/entity/fly.png");
    }
}