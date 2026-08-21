package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.content.block.p.ArcadeBlock;
import com.altnoir.poopsky.game.client.arcade.ArcadeWorldScreenRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class ArcadeBlockEntityRenderer implements BlockEntityRenderer<ArcadeBlockEntity, ArcadeBlockEntityRenderer.RenderState> {
    private static final float MIN_X = 1.0F / 16.0F, MAX_X = 15.0F / 16.0F;
    private static final float MIN_Y = 0.0F, MAX_Y = 10.0F / 16.0F, Z = 9.0F / 16.0F - 0.002F;

    public ArcadeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public RenderState createRenderState() { return new RenderState(); }

    @Override
    public void extractRenderState(ArcadeBlockEntity entity, RenderState state, float partialTick,
                                   Vec3 camera, ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, camera, overlay);
        state.texture = ArcadeWorldScreenRenderer.getScreenTexture(entity.getBlockPos(), entity.getCartridge());
        state.rotation = entity.getBlockState().getBlock() instanceof ArcadeBlock
                ? horizontalRotation(entity.getBlockState().getValue(ArcadeBlock.FACING)) : 0;
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.texture == null) return;
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.rotation));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(state.texture), (pose, consumer) -> {
            consumer.addVertex(pose, MIN_X, MAX_Y, Z).setColor(-1).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(state.lightCoords).setNormal(0, 0, -1);
            consumer.addVertex(pose, MAX_X, MAX_Y, Z).setColor(-1).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(state.lightCoords).setNormal(0, 0, -1);
            consumer.addVertex(pose, MAX_X, MIN_Y, Z).setColor(-1).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(state.lightCoords).setNormal(0, 0, -1);
            consumer.addVertex(pose, MIN_X, MIN_Y, Z).setColor(-1).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(state.lightCoords).setNormal(0, 0, -1);
        });
        poseStack.popPose();
    }

    private static int horizontalRotation(Direction direction) {
        return switch (direction) { case EAST -> 90; case SOUTH -> 180; case WEST -> 270; default -> 0; };
    }

    public static class RenderState extends BlockEntityRenderState {
        private Identifier texture;
        private int rotation;
    }
}
