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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.WeakHashMap;

public class GachaMachineBlockEntityRenderer implements BlockEntityRenderer<GachaMachineBlockEntity> {
    private static final int CAPSULE_COLUMNS = 4;
    private static final float INTERIOR_BALL_SCALE = 0.34F;
    private static final float EJECTED_BALL_SCALE = 0.34F;
    private static final ItemStack DISPLAY_BALL = new ItemStack(PoItems.GACHA_BALL.get());
    private static final Map<GachaMachineBlockEntity, CapsulePosition[]> INTERIOR_POSITION_CACHE = new WeakHashMap<>();

    private record CapsulePosition(float right, float y, float depth, float rotation) {
    }
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
        CapsulePosition[] capsules = interiorPositions(blockEntity);

        int selectedBall = blockEntity.isActive()
                ? Mth.clamp(blockEntity.selectedBallIndex(), 0, GachaMachineBlockEntity.CAPSULE_COUNT - 1)
                : -1;
        renderInteriorBalls(facing, poseStack, bufferSource, packedLight, blockEntity.getLevel(), capsules, selectedBall);
        if (!blockEntity.isActive()) {
            return;
        }

        float fallProgress = Mth.clamp(progress / 0.72F, 0.0F, 1.0F);
        float eject = Mth.clamp((progress - 0.72F) / 0.28F, 0.0F, 1.0F);
        float wobble = Mth.sin(progress * Mth.PI * 8.0F) * 0.12F * (1.0F - eject);
        CapsulePosition selectedPosition = capsules[selectedBall];
        float horizontal = Mth.lerp(fallProgress, selectedPosition.right(), 0.0F);
        float dropY = Mth.lerp(fallProgress, selectedPosition.y(), 0.52F);
        float depth = Mth.lerp(eject, selectedPosition.depth(), 0.70F);
        float ballScale = Mth.lerp(fallProgress, INTERIOR_BALL_SCALE, EJECTED_BALL_SCALE);
        float rightX = -facing.getStepZ();
        float rightZ = facing.getStepX();

        poseStack.pushPose();
        poseStack.translate(
                0.5D + rightX * horizontal + facing.getStepX() * depth,
                dropY + wobble - eject * 0.10D,
                0.5D + rightZ * horizontal + facing.getStepZ() * depth);
        poseStack.mulPose(Axis.YP.rotationDegrees(selectedPosition.rotation() + progress * 1080.0F));
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
                                            int packedLight, Level level, CapsulePosition[] capsules, int selectedBall) {
        float rightX = -facing.getStepZ();
        float rightZ = facing.getStepX();
        for (int index = 0; index < capsules.length; index++) {
            if (index == selectedBall) {
                continue;
            }
            CapsulePosition ball = capsules[index];
            poseStack.pushPose();
            poseStack.translate(
                    0.5D + rightX * ball.right() + facing.getStepX() * ball.depth(),
                    ball.y(),
                    0.5D + rightZ * ball.right() + facing.getStepZ() * ball.depth());
            poseStack.mulPose(Axis.YP.rotationDegrees(ball.rotation()));
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

    private static CapsulePosition[] interiorPositions(GachaMachineBlockEntity blockEntity) {
        return INTERIOR_POSITION_CACHE.computeIfAbsent(blockEntity, ignored -> {
            RandomSource random = RandomSource.create(blockEntity.getBlockPos().asLong() ^ 0x5DEECE66DL);
            CapsulePosition[] capsules = new CapsulePosition[GachaMachineBlockEntity.CAPSULE_COUNT];
            int rows = (capsules.length + CAPSULE_COLUMNS - 1) / CAPSULE_COLUMNS;
            for (int index = 0; index < capsules.length; index++) {
                int row = index / CAPSULE_COLUMNS;
                int column = index % CAPSULE_COLUMNS;
                float horizontalProgress = column / (float) (CAPSULE_COLUMNS - 1);
                float verticalProgress = row / (float) (rows - 1);
                capsules[index] = new CapsulePosition(
                        Mth.lerp(horizontalProgress, -0.18F, 0.18F)
                                + Mth.lerp(random.nextFloat(), -0.006F, 0.006F),
                        Mth.lerp(verticalProgress, 1.14F, 1.80F)
                                + Mth.lerp(random.nextFloat(), -0.006F, 0.006F),
                        0.24F + Mth.lerp(random.nextFloat(), -0.04F, 0.04F),
                        random.nextFloat() * 360.0F);
            }
            return capsules;
        });
    }

    @Override
    public boolean shouldRenderOffScreen(GachaMachineBlockEntity blockEntity) {
        return true;
    }
}
