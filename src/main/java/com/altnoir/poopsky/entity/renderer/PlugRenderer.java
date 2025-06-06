package com.altnoir.poopsky.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.model.PlugModel;
import com.altnoir.poopsky.entity.p.PlugEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class PlugRenderer extends EntityRenderer<PlugEntity> {
    private final EntityModel<PlugEntity> plugModel;

    public PlugRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.plugModel = new PlugModel<>(context.bakeLayer(PlugModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(PlugEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "textures/entity/toilet_plug.png");
    }

    @Override
    public void render(PlugEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        var y = -entity.getViewYRot(partialTick);
        var x = entity.getViewXRot(partialTick);
        var z = entity.getFloatingValue(partialTick);

        poseStack.translate(0.0D, z-1.0D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(y));
        poseStack.mulPose(Axis.XP.rotationDegrees(x));

        VertexConsumer vertexConsumer = bufferSource.getBuffer(plugModel.renderType(this.getTextureLocation(entity)));
        
        plugModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
