package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.content.block.entity.MaggotsChunkLoaderBlockEntity;
import com.altnoir.poopsky.content.block.p.MaggotsChunkLoaderBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class MaggotsChunkLoaderBlockEntityRenderer implements BlockEntityRenderer<MaggotsChunkLoaderBlockEntity> {
    private static final float GLOW_SMOOTHING = 0.25F;
    private static final Map<GlowKey, GlowState> GLOW_STATES = new HashMap<>();

    public MaggotsChunkLoaderBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MaggotsChunkLoaderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        boolean powered = blockEntity.getBlockState().getValue(MaggotsChunkLoaderBlock.POWERED);
        int radius = powered ? blockEntity.getLoadedRadius() : -1;
        float time = level.getGameTime() + partialTick;
        BlockPos pos = blockEntity.getBlockPos();
        GlowKey key = new GlowKey(level.dimension(), pos);
        GlowState state = GLOW_STATES.computeIfAbsent(key, p -> new GlowState());
        state.update(time, radius < 0 ? 0.0F : 0.1F + radius * 0.2F, radius < 0 ? 0.0F : 1.0F);

        float glow = state.scale;
        float alphaFactor = state.alpha;
        if (glow <= 0.001F || alphaFactor <= 0.001F) {
            if (radius < 0) {
                GLOW_STATES.remove(key);
            }
            return;
        }

        float pulse = (Mth.sin((level.getGameTime() + partialTick) * 0.22F) + 1.0F) * 0.5F;
        float alpha = alphaFactor * (0.10F + 0.06F * pulse);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lightning());

        renderGlowBox(poseStack, consumer, -glow, 1.0F + glow, alpha * 0.25F);
        renderGlowBox(poseStack, consumer, -glow * 0.75F, 1.0F + glow * 0.75F, alpha * 0.5F);
        renderGlowBox(poseStack, consumer, -glow * 0.5F, 1.0F + glow * 0.5F, alpha * 0.75F);
        renderGlowBox(poseStack, consumer, -glow * 0.25F, 1.0F + glow * 0.25F, alpha);
    }

    public static void clearGlowStates() {
        GLOW_STATES.clear();
    }

    private record GlowKey(ResourceKey<Level> dimension, BlockPos pos) {
    }

    private static final class GlowState {
        private float scale;
        private float alpha;
        private float lastTime = -1.0F;

        private void update(float time, float targetScale, float targetAlpha) {
            if (lastTime < 0.0F) {
                lastTime = time;
                return;
            }

            float delta = Math.max(0.0F, time - lastTime);
            lastTime = time;
            if (delta <= 0.0F) {
                return;
            }

            float factor = 1.0F - (float) Math.exp(-delta * GLOW_SMOOTHING);
            scale += (targetScale - scale) * factor;
            alpha += (targetAlpha - alpha) * factor;
        }
    }

    private static void renderGlowBox(PoseStack poseStack, VertexConsumer consumer, float min, float max, float alpha) {
        PoseStack.Pose pose = poseStack.last();

        renderQuad(pose, consumer, min, min, min, max, min, min, max, min, max, min, min, max, alpha);
        renderQuad(pose, consumer, min, max, max, max, max, max, max, max, min, min, max, min, alpha);
        renderQuad(pose, consumer, min, min, min, min, min, max, min, max, max, min, max, min, alpha);
        renderQuad(pose, consumer, max, min, max, max, min, min, max, max, min, max, max, max, alpha);
        renderQuad(pose, consumer, max, min, min, min, min, min, min, max, min, max, max, min, alpha);
        renderQuad(pose, consumer, min, min, max, max, min, max, max, max, max, min, max, max, alpha);
    }

    private static void renderQuad(PoseStack.Pose pose, VertexConsumer consumer,
                                   float x1, float y1, float z1,
                                   float x2, float y2, float z2,
                                   float x3, float y3, float z3,
                                   float x4, float y4, float z4,
                                   float alpha) {
        consumer.addVertex(pose, x1, y1, z1).setColor(1.0F, 0.45F, 0.12F, alpha);
        consumer.addVertex(pose, x2, y2, z2).setColor(1.0F, 0.45F, 0.12F, alpha);
        consumer.addVertex(pose, x3, y3, z3).setColor(1.0F, 0.45F, 0.12F, alpha);
        consumer.addVertex(pose, x4, y4, z4).setColor(1.0F, 0.45F, 0.12F, alpha);
    }
}
