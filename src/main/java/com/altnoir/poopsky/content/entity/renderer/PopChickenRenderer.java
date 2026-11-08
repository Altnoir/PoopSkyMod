package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.p.ExplosiveChickenEntity;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BoneState;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class PopChickenRenderer extends MobRenderer<ExplosiveChickenEntity, PopChickenRenderer.PopChickenModel> {
    private static final ResourceLocation TEXTURE = PoopSky.loc("textures/entity/pop_chicken.png");

    public PopChickenRenderer(EntityRendererProvider.Context context) {
        super(context, new PopChickenModel(), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(ExplosiveChickenEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(ExplosiveChickenEntity entity, PoseStack poseStack, float partialTick) {
        float swelling = entity.getSwelling(partialTick);
        float wobble = 1.0F + Mth.sin(swelling * 100.0F) * swelling * 0.01F;
        swelling = Mth.clamp(swelling, 0.0F, 1.0F);
        swelling *= swelling;
        swelling *= swelling;
        float horizontal = (1.0F + swelling * 0.4F) * wobble;
        float vertical = (1.0F + swelling * 0.1F) / wobble;
        poseStack.scale(horizontal, vertical, horizontal);
    }

    @Override
    protected float getWhiteOverlayProgress(ExplosiveChickenEntity entity, float partialTick) {
        float swelling = entity.getSwelling(partialTick);
        return (int) (swelling * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swelling, 0.5F, 1.0F);
    }

    static class PopChickenModel extends EntityModel<ExplosiveChickenEntity> {
        private BakedBedrockModel model;
        private BakedModelInstance instance;

        @Override
        public void setupAnim(ExplosiveChickenEntity entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
            BedrockModelResources resources = BedrockModelResources.getInstance();
            BakedBedrockModel loadedModel = resources == null ? null : resources.getBakedModel(PoBedrockModelResources.POP_CHICKEN_MODEL);
            if (loadedModel != this.model) {
                this.model = loadedModel;
                this.instance = loadedModel == null ? null : loadedModel.createInstance();
            }
            if (this.instance == null) {
                return;
            }
            this.instance.resetPose();
            float swing = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            BoneState left = this.instance.getBone("left");
            BoneState right = this.instance.getBone("right");
            if (left != null) {
                left.rotation.rotateX(-swing);
            }
            if (right != null) {
                right.rotation.rotateX(swing);
            }
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, int color) {
            if (this.instance == null) {
                return;
            }
            poseStack.pushPose();
            poseStack.translate(0.0F, 1.501F, 0.0F);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            if (this.young) {
                poseStack.scale(0.5F, 0.5F, 0.5F);
            }
            this.instance.renderToBuffer(poseStack, consumer, packedLight, packedOverlay,
                    FastColor.ARGB32.red(color) / 255.0F,
                    FastColor.ARGB32.green(color) / 255.0F,
                    FastColor.ARGB32.blue(color) / 255.0F,
                    FastColor.ARGB32.alpha(color) / 255.0F);
            poseStack.popPose();
        }
    }
}
