package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.content.block.entity.MaggotsChunkLoaderBlockEntity;
import com.altnoir.poopsky.content.block.p.MaggotsChunkLoaderBlock;
import com.altnoir.poopsky.init.PoRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.HashMap;
import java.util.Map;

public class MaggotsChunkLoaderBlockEntityRenderer implements BlockEntityRenderer<MaggotsChunkLoaderBlockEntity, MaggotsChunkLoaderBlockEntityRenderer.RenderState> {
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
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(MaggotsChunkLoaderBlockEntity blockEntity, RenderState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        Level level = blockEntity.getLevel();
        if (level == null) {
            state.visible = false;
            return;
        }

        boolean powered = blockEntity.getBlockState().getValue(MaggotsChunkLoaderBlock.POWERED);
        int radius = powered ? blockEntity.getLoadedRadius() : -1;
        float time = level.getGameTime() + partialTick;
        GlowKey key = new GlowKey(level.dimension(), blockEntity.getBlockPos());
        GlowState glowState = GLOW_STATES.computeIfAbsent(key, ignored -> new GlowState());
        glowState.update(time, radius < 0 ? 0.0F : MIN_GLOW_SCALE + radius * 0.2F, radius < 0 ? 0.0F : 1.0F);

        state.glow = glowState.scale;
        state.alphaFactor = glowState.alpha;
        state.progress = Mth.clamp((state.glow - MIN_GLOW_SCALE) / (MAX_GLOW_SCALE - MIN_GLOW_SCALE), 0.0F, 1.0F);
        state.pulse = (Mth.sin(time * 0.22F) + 1.0F) * 0.5F;
        state.visible = state.glow > 0.001F && state.alphaFactor > 0.001F;
        if (!state.visible && radius < 0) {
            GLOW_STATES.remove(key);
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.visible) {
            return;
        }

        float alpha = state.alphaFactor * (0.25F + 0.15F * state.pulse);
        collector.submitCustomGeometry(poseStack, PoRenderTypes.chunkLoaderGlow(), (pose, consumer) -> {
            for (int layer = 0; layer < 4; layer++) {
                float layerScale = 1.0F - layer * 0.25F;
                float layerAlpha = alpha * (0.25F + layer * 0.25F);
                float saturation = 0.25F + layer * 0.25F;
                float pinkRed = Mth.lerp(saturation, 1.0F, 1.0F);
                float pinkGreen = Mth.lerp(saturation, 0.98F, 0.55F);
                float pinkBlue = Mth.lerp(saturation, 0.92F, 0.80F);
                float red = Mth.lerp(state.progress, pinkRed, 1.0F);
                float green = Mth.lerp(state.progress, pinkGreen, 0.98F);
                float blue = Mth.lerp(state.progress, pinkBlue, 0.92F);
                renderGlowBox(pose, consumer,
                        MODEL_MIN_X - state.glow * layerScale, MODEL_MAX_X + state.glow * layerScale,
                        MODEL_MIN_Y - state.glow * layerScale, MODEL_MAX_Y + state.glow * layerScale,
                        MODEL_MIN_Z - state.glow * layerScale, MODEL_MAX_Z + state.glow * layerScale,
                        layerAlpha, red, green, blue);
            }
        });
    }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel level) {
            GLOW_STATES.keySet().removeIf(key -> key.dimension().equals(level.dimension()));
        }
    }

    private record GlowKey(ResourceKey<Level> dimension, BlockPos pos) {
    }

    public static class RenderState extends BlockEntityRenderState {
        private boolean visible;
        private float glow;
        private float alphaFactor;
        private float progress;
        private float pulse;
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

    private static void renderGlowBox(PoseStack.Pose pose, VertexConsumer consumer,
                                      float minX, float maxX,
                                      float minY, float maxY,
                                      float minZ, float maxZ,
                                      float alpha, float red, float green, float blue) {
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
