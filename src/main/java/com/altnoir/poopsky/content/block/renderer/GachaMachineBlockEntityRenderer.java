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
import org.joml.Vector3f;

import java.util.Map;
import java.util.WeakHashMap;

public class GachaMachineBlockEntityRenderer implements BlockEntityRenderer<GachaMachineBlockEntity> {
    private static final int CAPSULE_COLUMNS = 4;
    private static final float INTERIOR_BALL_SCALE = 0.26F;
    private static final float EJECTED_BALL_SCALE = 0.34F;
    private static final float ROLL_RADIUS = 0.085F;
    private static final float FALL_END = 0.30F;
    private static final float CHUTE_END = 0.70F;
    private static final float CHUTE_ENTRY_Y = 0.40F;
    private static final float CHUTE_DEPTH = 0.44F;
    private static final float OUTLET_Y = 0.28F;
    private static final float OUTLET_DEPTH = 0.60F;
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

        CapsulePosition selectedPosition = capsules[selectedBall];
        float fallProgress = Mth.clamp(progress / FALL_END, 0.0F, 1.0F);
        float chuteProgress = Mth.clamp((progress - FALL_END) / (CHUTE_END - FALL_END), 0.0F, 1.0F);
        float outletProgress = Mth.clamp((progress - CHUTE_END) / (1.0F - CHUTE_END), 0.0F, 1.0F);
        float fallDistance = fallProgress * fallProgress;
        float chuteDistance = chuteProgress * chuteProgress;
        float horizontal = Mth.lerp(chuteDistance, selectedPosition.right(), 0.0F);
        float dropY = Mth.lerp(fallDistance, selectedPosition.y(), CHUTE_ENTRY_Y);
        dropY = Mth.lerp(chuteDistance, dropY, OUTLET_Y);
        float depth = Mth.lerp(chuteDistance, selectedPosition.depth(), CHUTE_DEPTH);
        depth = Mth.lerp(outletProgress, depth, OUTLET_DEPTH);
        float ballScale = Mth.lerp(progress, INTERIOR_BALL_SCALE, EJECTED_BALL_SCALE);
        float rightX = -facing.getStepZ();
        float rightZ = facing.getStepX();
        float chuteRight = -selectedPosition.right();
        float chuteForward = CHUTE_DEPTH - selectedPosition.depth();
        float chuteHorizontalDistance = Mth.sqrt(chuteRight * chuteRight + chuteForward * chuteForward);
        float chutePathLength = Mth.sqrt(chuteHorizontalDistance * chuteHorizontalDistance
                + (CHUTE_ENTRY_Y - OUTLET_Y) * (CHUTE_ENTRY_Y - OUTLET_Y));
        float chuteMoveX = rightX * chuteRight + facing.getStepX() * chuteForward;
        float chuteMoveZ = rightZ * chuteRight + facing.getStepZ() * chuteForward;
        Vector3f chuteRollAxis = new Vector3f(-chuteMoveZ, 0.0F, chuteMoveX).normalize();
        float chuteRollDegrees = -chuteDistance * chutePathLength / ROLL_RADIUS * 57.29578F;
        float outletRollDegrees = -outletProgress * (OUTLET_DEPTH - CHUTE_DEPTH) / ROLL_RADIUS * 57.29578F;

        poseStack.pushPose();
        poseStack.translate(
                0.5D + rightX * horizontal + facing.getStepX() * depth,
                dropY,
                0.5D + rightZ * horizontal + facing.getStepZ() * depth);
        poseStack.mulPose(Axis.of(chuteRollAxis).rotationDegrees(chuteRollDegrees));
        poseStack.mulPose(Axis.of(new Vector3f(rightX, 0.0F, rightZ)).rotationDegrees(outletRollDegrees));
        poseStack.mulPose(Axis.YP.rotationDegrees(selectedPosition.rotation()));
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
                        Mth.lerp(verticalProgress, 0.55F, 0.80F)
                                + Mth.lerp(random.nextFloat(), -0.006F, 0.006F),
                        0.32F + Mth.lerp(random.nextFloat(), -0.015F, 0.015F),
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
