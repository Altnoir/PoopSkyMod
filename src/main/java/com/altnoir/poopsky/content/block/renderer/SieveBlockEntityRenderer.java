package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.content.block.entity.SieveBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class SieveBlockEntityRenderer implements BlockEntityRenderer<SieveBlockEntity, SieveBlockEntityRenderer.RenderState> {
    private static final float ITEM_SCALE = 1.5F;
    private static final float START_Y = 1.12F;
    private static final float END_Y = 0.62F;
    private final ItemModelResolver itemModelResolver;
    public SieveBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(SieveBlockEntity blockEntity, RenderState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        state.progress = blockEntity.getRenderProgress(partialTick);
        var stack = blockEntity.getRenderStack();
        this.itemModelResolver.updateForTopItem(state.item, stack, ItemDisplayContext.FIXED,
                blockEntity.getLevel(), null, 0
        );
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.item.isEmpty()) {
            return;
        }

        float y = START_Y + (END_Y - START_Y) * state.progress;
        poseStack.pushPose();
        poseStack.translate(0.5D, y, 0.5D);
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        private final ItemStackRenderState item = new ItemStackRenderState();
        private float progress;
    }
}
