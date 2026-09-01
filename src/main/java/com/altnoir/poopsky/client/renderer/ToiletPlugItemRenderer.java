package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ToiletPlugItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation TEXTURE = PoopSky.loc("textures/entity/toilet_plug.png");
    private static final float HAND_Y = 4.0F / 16.0F + 0.5F;
    private static final float HAND_Z = 8.0F / 16.0F - 1.0F;
    private static final float PIVOT_Y = -3.0F / 16.0F;
    private static final float GEO_HAND_ORIGIN_Y = 1.0F;

    private BakedBedrockModel model;
    private BakedModelInstance instance;

    public ToiletPlugItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
                             int packedLight, int packedOverlay) {
        this.refreshResources();
        if (this.instance == null) {
            return;
        }

        this.instance.resetPose();
        poseStack.pushPose();
        this.applyDisplayTransform(displayContext, poseStack);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        this.instance.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
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

    private void applyDisplayTransform(ItemDisplayContext context, PoseStack poseStack) {
        switch (context) {
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND, HEAD,
                 FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                poseStack.translate(0.5F, HAND_Y, HAND_Z);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.translate(0.0F, PIVOT_Y, 0.0F);
                poseStack.translate(0.0F, GEO_HAND_ORIGIN_Y, 0.0F);
            }
            default -> poseStack.translate(0.5F, 0.5F, 0.5F);
        }
    }
}
