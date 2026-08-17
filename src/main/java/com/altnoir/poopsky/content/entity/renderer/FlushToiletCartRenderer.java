package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.p.FlushToiletCartEntity;
import com.altnoir.poopsky.init.PoEntityType;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockBone;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v1.resource.BedrockModelResourceSet;
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
import org.jetbrains.annotations.NotNull;

public class FlushToiletCartRenderer extends EntityRenderer<FlushToiletCartEntity> {
    private static final ResourceLocation TEXTURE = PoopSky.loc("textures/block/toilet/flush_toilet_cart.png");
    private static final ResourceLocation GOLDEN_TEXTURE = PoopSky.loc("textures/block/toilet/golden_flush_toilet_cart.png");

    public FlushToiletCartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    @Override
    public ResourceLocation getTextureLocation(FlushToiletCartEntity entity) {
        return entity.getType().equals(PoEntityType.GOLDEN_FLUSH_TOILET_CART.get()) ? GOLDEN_TEXTURE : TEXTURE;
    }

    @Override
    public void render(FlushToiletCartEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        BedrockModelResourceSet resourceSet = BedrockModelResourceSet.getInstance();
        if (resourceSet != null) {
            BedrockModel model = resourceSet.getModel(PoBedrockModelResources.FLUSH_TOILET_CART);
            if (model != null) {
                BedrockBone wheelLeft = model.getBone("wheel_l");
                BedrockBone wheelRight = model.getBone("wheel_r");
                if (wheelLeft != null) {
                    wheelLeft.rotation.rotationX((float) -Math.toRadians(entity.getWheelLeftRotation(partialTick)));
                }
                if (wheelRight != null) {
                    wheelRight.rotation.rotationX((float) -Math.toRadians(entity.getWheelRightRotation(partialTick)));
                }

                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot())));
                float hurtTicks = (float) entity.getHurtTime() - partialTick;
                float hurtDamage = entity.getDamage() - partialTick;
                if (hurtDamage < 0.0F) {
                    hurtDamage = 0.0F;
                }
                if (hurtTicks > 0.0F) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurtTicks) * hurtTicks * hurtDamage / 10.0F * (float) entity.getHurtDir()));
                }

                VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
                model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();

                if (wheelLeft != null) {
                    wheelLeft.rotation.rotationX(0.0F);
                }
                if (wheelRight != null) {
                    wheelRight.rotation.rotationX(0.0F);
                }
            }
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
