package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.content.block.entity.GachaMachineBlockEntity;
import com.altnoir.poopsky.content.block.p.GachaMachineBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GachaMachineBlockEntityRenderer implements BlockEntityRenderer<GachaMachineBlockEntity> {
    public GachaMachineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(GachaMachineBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = blockEntity.renderStack();
        if (stack.isEmpty()) {
            return;
        }
        float progress = blockEntity.animationProgress(partialTick);
        float eject = Mth.clamp((progress - 0.72F) / 0.28F, 0.0F, 1.0F);
        Direction facing = blockEntity.getBlockState().getValue(GachaMachineBlock.FACING);
        float wobble = Mth.sin(progress * Mth.PI * 8.0F) * 0.12F * (1.0F - eject);

        poseStack.pushPose();
        poseStack.translate(
                0.5D + facing.getStepX() * (0.56D + eject * 0.7D),
                1.25D + wobble - eject * 0.8D,
                0.5D + facing.getStepZ() * (0.56D + eject * 0.7D));
        poseStack.mulPose(Axis.YP.rotationDegrees(progress * 1080.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(progress * 360.0F));
        poseStack.scale(0.65F, 0.65F, 0.65F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                0);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(GachaMachineBlockEntity blockEntity) {
        return blockEntity.isActive();
    }
}
