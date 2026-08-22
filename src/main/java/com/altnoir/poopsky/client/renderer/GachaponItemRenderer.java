package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.renderer.GachaponRenderer;
import com.altnoir.poopsky.content.item.p.GachaponItem;
import com.altnoir.poopsky.init.PoComponents;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v1.resource.BedrockModelResourceSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GachaponItemRenderer extends BlockEntityWithoutLevelRenderer {
    public GachaponItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (displayContext != ItemDisplayContext.GROUND) {
            return;
        }

        BedrockModelResourceSet resourceSet = BedrockModelResourceSet.getInstance();
        if (resourceSet == null) {
            return;
        }

        BedrockModel model = resourceSet.getModel(PoBedrockModelResources.GACHAPON);
        if (model == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(getTexture(stack)));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static ResourceLocation getTexture(ItemStack stack) {
        String color = stack.get(PoComponents.GACHAPON_COLOR.get());
        return color != null ? GachaponRenderer.getTexture(color) : GachaponRenderer.getTexture(GachaponItem.PINK);
    }
}
