package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.content.block.entity.GachaMachineBlockEntity;
import com.altnoir.poopsky.content.block.p.GachaMachineBlock;
import com.altnoir.poopsky.init.PoItems;
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
import net.minecraft.world.level.Level;

public class GachaMachineBlockEntityRenderer implements BlockEntityRenderer<GachaMachineBlockEntity> {
    private static final ItemStack DISPLAY_BALL = new ItemStack(PoItems.GACHA_BALL.get());
    private static final float[][] INTERIOR_BALLS = {
            {-0.24F, 1.32F, 0.00F}, {-0.08F, 1.32F, 0.04F}, {0.08F, 1.32F, 0.00F}, {0.24F, 1.32F, 0.04F},
            {-0.18F, 1.47F, 0.04F}, {0.00F, 1.47F, 0.00F}, {0.18F, 1.47F, 0.04F},
            {-0.18F, 1.62F, 0.00F}, {0.18F, 1.62F, 0.00F}, {0.00F, 1.62F, 0.04F}
    };
    public GachaMachineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(GachaMachineBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = blockEntity.renderStack();
        if (stack.isEmpty()) {
            stack = DISPLAY_BALL;
        }
        float progress = blockEntity.animationProgress(partialTick);
        Direction facing = blockEntity.getBlockState().getValue(GachaMachineBlock.FACING);

        renderInteriorBalls(facing, poseStack, bufferSource, packedLight, blockEntity.getLevel(), blockEntity.isActive());
        if (!blockEntity.isActive()) {
            return;
        }

        float fallProgress = Mth.clamp(progress / 0.72F, 0.0F, 1.0F);
        float eject = Mth.clamp((progress - 0.72F) / 0.28F, 0.0F, 1.0F);
        float wobble = Mth.sin(progress * Mth.PI * 8.0F) * 0.12F * (1.0F - eject);
        float dropY = Mth.lerp(fallProgress, 1.62F, 0.52F);
        float depth = Mth.lerp(eject, 0.28F, 0.70F);

        poseStack.pushPose();
        poseStack.translate(
                0.5D + facing.getStepX() * depth,
                dropY + wobble - eject * 0.10D,
                0.5D + facing.getStepZ() * depth);
        poseStack.mulPose(Axis.YP.rotationDegrees(progress * 1080.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(progress * 360.0F));
        poseStack.scale(0.62F, 0.62F, 0.62F);
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

    private static void renderInteriorBalls(Direction facing, PoseStack poseStack, MultiBufferSource bufferSource,
                                            int packedLight, Level level, boolean hideTopBall) {
        float rightX = -facing.getStepZ();
        float rightZ = facing.getStepX();
        for (int index = 0; index < INTERIOR_BALLS.length; index++) {
            if (hideTopBall && index == INTERIOR_BALLS.length - 1) {
                continue;
            }
            float[] ball = INTERIOR_BALLS[index];
            poseStack.pushPose();
            poseStack.translate(
                    0.5D + rightX * ball[0] + facing.getStepX() * (0.24D + ball[2]),
                    ball[1],
                    0.5D + rightZ * ball[0] + facing.getStepZ() * (0.24D + ball[2]));
            poseStack.mulPose(Axis.YP.rotationDegrees(index * 37.0F));
            poseStack.scale(0.22F, 0.22F, 0.22F);
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    DISPLAY_BALL,
                    ItemDisplayContext.FIXED,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    level,
                    index);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(GachaMachineBlockEntity blockEntity) {
        return true;
    }
}
