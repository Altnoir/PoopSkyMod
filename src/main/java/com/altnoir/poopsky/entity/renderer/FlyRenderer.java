package com.altnoir.poopsky.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.model.FlyModel;
import com.altnoir.poopsky.entity.p.FlyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FlyRenderer extends MobRenderer<FlyEntity, FlyModel<FlyEntity>> {
    private static final ResourceLocation FLY_LOCATION = PoopSky.loc("textures/entity/fly/fly.png");
    private static final ResourceLocation FLY_ANGRY_LOCATION = PoopSky.loc("textures/entity/fly/fly_angry.png");
    private static final ResourceLocation FLY_NECTAR_LOCATION = PoopSky.loc("textures/entity/fly/fly_nectar.png");
    private static final ResourceLocation FLY_ANGRY_NECTAR_LOCATION = PoopSky.loc("textures/entity/fly/fly_angry_nectar.png");

    public FlyRenderer(EntityRendererProvider.Context context) {
        super(context, new FlyModel<>(context.bakeLayer(FlyModel.LAYER_LOCATION)), 0.25F);
    }

    @Override
    protected void scale(FlyEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.85F, 0.85F, 0.85F);
    }

    @Override
    public ResourceLocation getTextureLocation(FlyEntity entity) {
        if (entity.isAngry()) {
            return entity.hasNectar() ? FLY_ANGRY_NECTAR_LOCATION : FLY_ANGRY_LOCATION;
        }
        return entity.hasNectar() ? FLY_NECTAR_LOCATION : FLY_LOCATION;
    }
}
