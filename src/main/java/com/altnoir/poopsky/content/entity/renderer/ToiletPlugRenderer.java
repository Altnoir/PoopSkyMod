package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class ToiletPlugRenderer extends EntityRenderer<ToiletPlugEntity> {
    private final EntityModel<ToiletPlugEntity> plugModel;

    public ToiletPlugRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.plugModel = new ToiletPlugModel<>(context.bakeLayer(ToiletPlugModel.LAYER_LOCATION));
    }

    @Override
    public Identifier getTextureLocation(ToiletPlugEntity entity) {
        return PoopSky.loc("textures/entity/toilet_plug.png");
    }

    @Override
    public void render(ToiletPlugEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        var y = -entity.getViewYRot(partialTick);
        var x = entity.getViewXRot(partialTick);
        var z = entity.getFloatingValue(partialTick);

        poseStack.translate(0.0D, z - 1.0D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(y));
        poseStack.mulPose(Axis.XP.rotationDegrees(x));
        float hurtTicks = (float) entity.getHurtTime() - partialTick;
        float hurtDamage = entity.getDamage() - partialTick;
        if (hurtDamage < 0.0F) {
            hurtDamage = 0.0F;
        }
        if (hurtTicks > 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(hurtTicks) * hurtTicks * hurtDamage / 10.0F * (float) entity.getHurtDir()));
        }

        VertexConsumer vertexConsumer = bufferSource.getBuffer(plugModel.renderType(this.getTextureLocation(entity)));

        plugModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
