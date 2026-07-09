package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.model.ToiletPlugModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ToiletPlugItemRenderer extends BlockEntityWithoutLevelRenderer {
    private ToiletPlugModel<?> plugModel;
    private static final ResourceLocation TEXTURE = PoopSky.loc("textures/entity/toilet_plug.png");
    private static final float HAND_Y = 4.0F / 16.0F + 0.5F;
    private static final float HAND_Z = 8.0F / 16.0F - 1.0F;
    private static final float PIVOT_Y = -3.0F / 16.0F;

    public ToiletPlugItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
                             int packedLight, int packedOverlay) {
        if (plugModel == null) {
            var entityModels = Minecraft.getInstance().getEntityModels();
            plugModel = new ToiletPlugModel<>(entityModels.bakeLayer(ToiletPlugModel.LAYER_LOCATION));
        }

        poseStack.pushPose();
        applyDisplayTransform(displayContext, poseStack);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(plugModel.renderType(TEXTURE));
        plugModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    private void applyDisplayTransform(ItemDisplayContext ctx, PoseStack poseStack) {
        switch (ctx) {
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND, HEAD,
                 FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                poseStack.translate(0.5F, HAND_Y, HAND_Z);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.translate(0.0F, PIVOT_Y, 0.0F);
            }
            default -> poseStack.translate(0.5F, 0.5F, 0.5F);
        }
    }
}