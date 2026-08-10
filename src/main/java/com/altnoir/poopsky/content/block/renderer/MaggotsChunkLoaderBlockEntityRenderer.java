package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.content.block.entity.MaggotsChunkLoaderBlockEntity;
import com.altnoir.poopsky.content.block.p.MaggotsChunkLoaderBlock;
import com.altnoir.poopsky.init.PoRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
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
    private static final float MODEL_MIN_X = 3.0F / 16.0F;
    private static final float MODEL_MIN_Y = 3.0F / 16.0F;
    private static final float MODEL_MIN_Z = 3.0F / 16.0F;
    private static final float MODEL_MAX_X = 13.0F / 16.0F;
    private static final float MODEL_MAX_Y = 14.0F / 16.0F;
    private static final float MODEL_MAX_Z = 13.0F / 16.0F;
    private static final float MIN_GLOW_SCALE = 0.1F;
    private static final float MAX_GLOW_SCALE = MIN_GLOW_SCALE + (MaggotsChunkLoaderBlockEntity.MAX_STRUCTURE_LEVEL - 1) * 0.2F;
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
        state.update(time, radius < 0 ? 0.0F : MIN_GLOW_SCALE + radius * 0.2F, radius < 0 ? 0.0F : 1.0F);

        float glow = state.scale;
        float alphaFactor = state.alpha;
        if (glow <= 0.001F || alphaFactor <= 0.001F) {
            if (radius < 0) {
                GLOW_STATES.remove(key);
            }
            return;
        }

        float pulse = (Mth.sin((level.getGameTime() + partialTick) * 0.22F) + 1.0F) * 0.5F;
        float alpha = alphaFactor * (0.25F + 0.15F * pulse);
        VertexConsumer consumer = bufferSource.getBuffer(PoRenderTypes.chunkLoaderGlow());
        float progress = Mth.clamp((glow - MIN_GLOW_SCALE) / (MAX_GLOW_SCALE - MIN_GLOW_SCALE), 0.0F, 1.0F);
        for (int layer = 0; layer < 4; layer++) {
            float layerScale = 1.0F - layer * 0.25F;
            float layerAlpha = alpha * (0.25F + layer * 0.25F);
            float saturation = 0.25F + layer * 0.25F;
            float pinkRed = Mth.lerp(saturation, 1.0F, 1.0F);
            float pinkGreen = Mth.lerp(saturation, 0.98F, 0.55F);
            float pinkBlue = Mth.lerp(saturation, 0.92F, 0.80F);
            float red = Mth.lerp(progress, pinkRed, 1.0F);
            float green = Mth.lerp(progress, pinkGreen, 0.98F);
            float blue = Mth.lerp(progress, pinkBlue, 0.92F);
            renderGlowBox(poseStack, consumer,
                    MODEL_MIN_X - glow * layerScale, MODEL_MAX_X + glow * layerScale,
                    MODEL_MIN_Y - glow * layerScale, MODEL_MAX_Y + glow * layerScale,
                    MODEL_MIN_Z - glow * layerScale, MODEL_MAX_Z + glow * layerScale,
                    layerAlpha, red, green, blue);
        }
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

    private static void renderGlowBox(PoseStack poseStack, VertexConsumer consumer,
                                      float minX, float maxX,
                                      float minY, float maxY,
                                      float minZ, float maxZ,
                                      float alpha, float red, float green, float blue) {
        PoseStack.Pose pose = poseStack.last();

        renderQuad(pose, consumer,
                minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ,
                alpha, red, green, blue);
        renderQuad(pose, consumer,
                minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ,
                alpha, red, green, blue);
        renderQuad(pose, consumer,
                minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ,
                alpha, red, green, blue);
        renderQuad(pose, consumer,
                maxX, minY, maxZ, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ,
                alpha, red, green, blue);
        renderQuad(pose, consumer,
                maxX, minY, minZ, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ,
                alpha, red, green, blue);
        renderQuad(pose, consumer,
                minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ,
                alpha, red, green, blue);
    }

    private static void renderQuad(PoseStack.Pose pose, VertexConsumer consumer,
                                   float x1, float y1, float z1,
                                   float x2, float y2, float z2,
                                   float x3, float y3, float z3,
                                   float x4, float y4, float z4,
                                   float alpha, float red, float green, float blue) {
        consumer.addVertex(pose, x1, y1, z1).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, x2, y2, z2).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, x3, y3, z3).setColor(red, green, blue, alpha);
        consumer.addVertex(pose, x4, y4, z4).setColor(red, green, blue, alpha);
    }
}
