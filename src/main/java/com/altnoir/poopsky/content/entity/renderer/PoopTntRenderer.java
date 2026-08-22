package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.content.entity.p.PoopTntEntity;
import com.altnoir.poopsky.init.PoBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;

public class PoopTntRenderer extends EntityRenderer<PoopTntEntity, TntRenderState> {
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private final BlockModelResolver blockModelResolver;

    public PoopTntRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockModelResolver = context.getBlockModelResolver();
    }

    @Override
    public TntRenderState createRenderState() {
        return new TntRenderState();
    }

    @Override
    public void extractRenderState(PoopTntEntity entity, TntRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.fuseRemainingInTicks = entity.getFuse() - partialTick + 1.0F;
        this.blockModelResolver.update(state.blockState, PoBlocks.POP.get().defaultBlockState(), BLOCK_DISPLAY_CONTEXT);
    }

    @Override
    public void submit(TntRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5F, 0.0F);
        float fuse = state.fuseRemainingInTicks;
        if (fuse < 10.0F) {
            float scale = 1.0F - fuse / 10.0F;
            scale = Mth.clamp(scale, 0.0F, 1.0F);
            scale *= scale;
            scale *= scale;
            scale = 1.0F + scale * 0.3F;
            poseStack.scale(scale, scale, scale);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        TntMinecartRenderer.submitWhiteSolidBlock(
                state.blockState,
                poseStack,
                collector,
                state.lightCoords,
                (int) fuse / 5 % 2 == 0,
                state.outlineColor
        );
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
