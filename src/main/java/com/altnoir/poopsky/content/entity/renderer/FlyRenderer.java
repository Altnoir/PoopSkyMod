package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.render.FlyRenderState;
import com.altnoir.poopsky.content.entity.model.FlyModel;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class FlyRenderer extends MobRenderer<FlyEntity, FlyRenderState, FlyModel> {


    public FlyRenderer(EntityRendererProvider.Context context) {
        super(context, new FlyModel(context.bakeLayer(FlyModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public Identifier getTextureLocation(FlyRenderState state) {
        return PoopSky.loc("textures/entity/fly.png");
    }

    @Override
    public FlyRenderState createRenderState() {
        return new FlyRenderState();
    }

    @Override
    public void extractRenderState(FlyEntity entity, FlyRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isStationaryOnGround = entity.onGround() && entity.getDeltaMovement().lengthSqr() < 1.0E-7;
    }
}
