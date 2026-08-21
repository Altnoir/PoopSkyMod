package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.content.block.p.ArcadeBlock;
import com.altnoir.poopsky.game.client.arcade.ArcadeWorldScreenRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ArcadeBlockEntityRenderer implements BlockEntityRenderer<ArcadeBlockEntity> {
    private static final float SCREEN_MIN_X = 1.0F / 16.0F;
    private static final float SCREEN_MAX_X = 15.0F / 16.0F;
    private static final float SCREEN_MIN_Y = 0.0F;
    private static final float SCREEN_MAX_Y = 10.0F / 16.0F;
    private static final float SCREEN_Z = 9.0F / 16.0F - 0.002F;

    public ArcadeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ArcadeBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof ArcadeBlock)) {
            return;
        }

        ItemStack cartridge = blockEntity.getCartridge();
        Identifier texture = ArcadeWorldScreenRenderer.getScreenTexture(blockEntity.getBlockPos(), cartridge);
        if (texture == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-horizontalRotation(state.getValue(ArcadeBlock.FACING))));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        PoseStack.Pose pose = poseStack.last();
        renderScreenQuad(consumer, pose, packedLight);

        poseStack.popPose();
    }

    private static void renderScreenQuad(VertexConsumer consumer, PoseStack.Pose pose, int packedLight) {
        consumer.addVertex(pose, SCREEN_MIN_X, SCREEN_MAX_Y, SCREEN_Z)
                .setColor(255, 255, 255, 255)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(pose, SCREEN_MAX_X, SCREEN_MAX_Y, SCREEN_Z)
                .setColor(255, 255, 255, 255)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(pose, SCREEN_MAX_X, SCREEN_MIN_Y, SCREEN_Z)
                .setColor(255, 255, 255, 255)
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(pose, SCREEN_MIN_X, SCREEN_MIN_Y, SCREEN_Z)
                .setColor(255, 255, 255, 255)
                .setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, -1.0F);
    }

    private static int horizontalRotation(Direction direction) {
        return switch (direction) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}
