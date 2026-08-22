package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.p.GachaponEntity;
import com.altnoir.poopsky.content.item.p.GachaponItem;
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

public class GachaponRenderer extends EntityRenderer<GachaponEntity> {
    private static final String[] COLORS = {
            GachaponItem.PINK,
            GachaponItem.RED,
            GachaponItem.YELLOW,
            GachaponItem.BLUE
    };

    public GachaponRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.35F;
    }

    @Override
    public ResourceLocation getTextureLocation(GachaponEntity entity) {
        return getTexture(COLORS[Math.floorMod(entity.getVariant(), COLORS.length)]);
    }

    public static ResourceLocation getTexture(String color) {
        return switch (color) {
            case GachaponItem.BLUE -> PoopSky.loc("textures/entity/gachapon/gachapon_blue.png");
            case GachaponItem.YELLOW -> PoopSky.loc("textures/entity/gachapon/gachapon_yellow.png");
            case GachaponItem.RED -> PoopSky.loc("textures/entity/gachapon/gachapon_red.png");
            default -> PoopSky.loc("textures/entity/gachapon/gachapon_pink.png");
        };
    }

    @Override
    public void render(GachaponEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        BedrockModelResourceSet resourceSet = BedrockModelResourceSet.getInstance();
        if (resourceSet != null) {
            BedrockModel model = resourceSet.getModel(PoBedrockModelResources.GACHAPON);
            if (model != null) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot())));
                VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
                model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
