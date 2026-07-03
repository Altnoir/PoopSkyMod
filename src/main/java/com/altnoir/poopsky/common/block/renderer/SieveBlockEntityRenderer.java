package com.altnoir.poopsky.common.block.renderer;

import com.altnoir.poopsky.common.block.entity.SieveBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SieveBlockEntityRenderer implements BlockEntityRenderer<SieveBlockEntity> {
    private static final float ITEM_SCALE = 1.5F;
    private static final float START_Y = 1.12F;
    private static final float END_Y = 0.62F;

    public SieveBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SieveBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = blockEntity.getRenderStack();
        if (stack.isEmpty()) {
            return;
        }

        float progress = blockEntity.getRenderProgress(partialTick);
        float y = START_Y + (END_Y - START_Y) * progress;

        poseStack.pushPose();
        poseStack.translate(0.5D, y, 0.5D);
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                0
        );

        poseStack.popPose();
    }
}
