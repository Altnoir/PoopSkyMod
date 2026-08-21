package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.renderer.GashaponRenderer;
import com.altnoir.poopsky.content.item.p.GashaponItem;
import com.altnoir.poopsky.init.PoComponents;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.BedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResourceSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GashaponItemRenderer extends BlockEntityWithoutLevelRenderer {
    public GashaponItemRenderer() {
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

        BedrockModel model = resourceSet.getModel(PoBedrockModelResources.GASHAPON);
        if (model == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(getTexture(stack)));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static Identifier getTexture(ItemStack stack) {
        String color = stack.get(PoComponents.GASHAPON_COLOR.get());
        return color != null ? GashaponRenderer.getTexture(color) : GashaponRenderer.getTexture(GashaponItem.PINK);
    }
}