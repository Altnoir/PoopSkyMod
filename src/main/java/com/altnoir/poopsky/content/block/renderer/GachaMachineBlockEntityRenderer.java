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
    private static final float INTERIOR_BALL_SCALE = 0.13F;
    private static final float EJECTED_BALL_SCALE = 0.30F;
    private static final ItemStack DISPLAY_BALL = new ItemStack(PoItems.GACHA_BALL.get());
    private static final float[][] INTERIOR_BALLS = {
            {-0.22F, 1.31F, 0.00F}, {-0.11F, 1.31F, 0.04F}, {0.00F, 1.31F, 0.00F},
            {0.11F, 1.31F, 0.04F}, {0.22F, 1.31F, 0.00F},
            {-0.17F, 1.42F, 0.04F}, {-0.06F, 1.42F, 0.00F}, {0.06F, 1.42F, 0.04F},
            {0.17F, 1.42F, 0.00F},
            {-0.22F, 1.53F, 0.00F}, {-0.11F, 1.53F, 0.04F}, {0.00F, 1.53F, 0.00F},
            {0.11F, 1.53F, 0.04F}, {0.22F, 1.53F, 0.00F},
            {-0.17F, 1.64F, 0.04F}, {-0.06F, 1.64F, 0.00F}, {0.06F, 1.64F, 0.04F},
            {0.17F, 1.64F, 0.00F}
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

        int selectedBall = blockEntity.isActive()
                ? Mth.clamp(blockEntity.selectedBallIndex(), 0, INTERIOR_BALLS.length - 1)
                : -1;
        renderInteriorBalls(facing, poseStack, bufferSource, packedLight, blockEntity.getLevel(), selectedBall);
        if (!blockEntity.isActive()) {
            return;
        }

        float fallProgress = Mth.clamp(progress / 0.72F, 0.0F, 1.0F);
        float eject = Mth.clamp((progress - 0.72F) / 0.28F, 0.0F, 1.0F);
        float wobble = Mth.sin(progress * Mth.PI * 8.0F) * 0.12F * (1.0F - eject);
        float[] selectedPosition = INTERIOR_BALLS[selectedBall];
        float horizontal = Mth.lerp(fallProgress, selectedPosition[0], 0.0F);
        float dropY = Mth.lerp(fallProgress, selectedPosition[1], 0.52F);
        float depth = Mth.lerp(eject, 0.24F + selectedPosition[2], 0.70F);
        float ballScale = Mth.lerp(fallProgress, INTERIOR_BALL_SCALE, EJECTED_BALL_SCALE);
        float rightX = -facing.getStepZ();
        float rightZ = facing.getStepX();

        poseStack.pushPose();
        poseStack.translate(
                0.5D + rightX * horizontal + facing.getStepX() * depth,
                dropY + wobble - eject * 0.10D,
                0.5D + rightZ * horizontal + facing.getStepZ() * depth);
        poseStack.mulPose(Axis.YP.rotationDegrees(selectedBall * 37.0F + progress * 1080.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(progress * 360.0F));
        poseStack.scale(ballScale, ballScale, ballScale);
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
                                            int packedLight, Level level, int selectedBall) {
        float rightX = -facing.getStepZ();
        float rightZ = facing.getStepX();
        for (int index = 0; index < INTERIOR_BALLS.length; index++) {
            if (index == selectedBall) {
                continue;
            }
            float[] ball = INTERIOR_BALLS[index];
            poseStack.pushPose();
            poseStack.translate(
                    0.5D + rightX * ball[0] + facing.getStepX() * (0.24D + ball[2]),
                    ball[1],
                    0.5D + rightZ * ball[0] + facing.getStepZ() * (0.24D + ball[2]));
            poseStack.mulPose(Axis.YP.rotationDegrees(index * 37.0F));
            poseStack.scale(INTERIOR_BALL_SCALE, INTERIOR_BALL_SCALE, INTERIOR_BALL_SCALE);
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
