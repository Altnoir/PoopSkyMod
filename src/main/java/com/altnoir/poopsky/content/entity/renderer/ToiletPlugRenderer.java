package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ToiletPlugRenderer extends EntityRenderer<ToiletPlugEntity> {
    private static final ResourceLocation TEXTURE = PoopSky.loc("textures/entity/toilet_plug.png");

    private BakedBedrockModel model;
    private BakedModelInstance instance;

    public ToiletPlugRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ToiletPlugEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(ToiletPlugEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        this.refreshResources();
        if (this.instance == null) {
            return;
        }

        this.instance.resetPose();
        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getFloatingValue(partialTick), 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getViewYRot(partialTick)));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getViewXRot(partialTick)));
        float hurtTicks = entity.getHurtTime() - partialTick;
        float hurtDamage = Math.max(0.0F, entity.getDamage() - partialTick);
        if (hurtTicks > 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(hurtTicks) * hurtTicks * hurtDamage / 10.0F * entity.getHurtDir()));
        }
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        this.instance.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void refreshResources() {
        BedrockModelResources resources = BedrockModelResources.getInstance();
        if (resources == null) {
            return;
        }
        BakedBedrockModel loadedModel = resources.getBakedModel(PoBedrockModelResources.TOILET_PLUG_MODEL);
        if (loadedModel != this.model) {
            this.model = loadedModel;
            this.instance = loadedModel == null ? null : loadedModel.createInstance();
        }
    }
}
