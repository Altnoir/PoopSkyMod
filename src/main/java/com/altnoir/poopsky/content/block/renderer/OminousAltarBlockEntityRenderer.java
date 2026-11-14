package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.client.renderer.FloatingItemRenderer;
import com.altnoir.poopsky.content.block.AltarRitualAnimation;
import com.altnoir.poopsky.content.block.entity.GlassJarBlockEntity;
import com.altnoir.poopsky.content.block.entity.OminousAltarBlockEntity;
import com.altnoir.poopsky.init.PoRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class OminousAltarBlockEntityRenderer implements BlockEntityRenderer<OminousAltarBlockEntity> {
    private static final float JAR_LINE_Y = 0.95F;
    private static final float ALTAR_LINE_Y = 0.875F;
    private static final int LINE_SEGMENTS = 32;
    private static final float LINE_GROWTH_PORTION = 0.125F;
    private static final float BEAM_HALF_WIDTH = 0.03F;
    private final ItemRenderer itemRenderer;

    public OminousAltarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    private static Vector3f point(float dx, float dz, float t, double time) {
        double wave = Math.sin(time * 0.2D + t * Math.PI * 6) * 0.04D * Math.sin(Math.PI * t);
        float height = Mth.lerp(t, JAR_LINE_Y, ALTAR_LINE_Y) + (float) Math.sin(Math.PI * t) * 0.9F;
        return new Vector3f(0.5F + dx * (1 - t) + (float) wave, height, 0.5F + dz * (1 - t) + (float) wave);
    }

    private static void beamQuad(VertexConsumer consumer, PoseStack.Pose pose, Vector3f from, Vector3f fromSide,
                                 Vector3f to, Vector3f toSide, int color) {
        quadVertex(consumer, pose, from.x - fromSide.x, from.y - fromSide.y, from.z - fromSide.z, color);
        quadVertex(consumer, pose, from.x + fromSide.x, from.y + fromSide.y, from.z + fromSide.z, color);
        quadVertex(consumer, pose, to.x + toSide.x, to.y + toSide.y, to.z + toSide.z, color);
        quadVertex(consumer, pose, to.x - toSide.x, to.y - toSide.y, to.z - toSide.z, color);
    }

    private static void quadVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, int color) {
        consumer.addVertex(pose, x, y, z)
                .setColor(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color), 255);
    }

    @Override
    public AABB getRenderBoundingBox(OminousAltarBlockEntity altar) {
        AABB bounds = new AABB(altar.getBlockPos()).expandTowards(0, 1, 0);
        return altar.getClientJars().isEmpty() ? bounds : bounds.inflate(3.0D, 0.1D, 3.0D);
    }

    @Override
    public void render(OminousAltarBlockEntity altar, float partialTick, PoseStack poses,
                       MultiBufferSource buffers, int light, int overlay) {
        Level level = altar.getLevel();
        if (level == null) return;
        ItemStack stack = (altar.getClientProgress() > 0 || altar.getOutput().isEmpty()) ? altar.getInput() : altar.getOutput();
        double time = level.getGameTime() + (double) partialTick;
        if (!stack.isEmpty()) {
            FloatingItemRenderer.render(itemRenderer, stack, level, poses, buffers, light,
                    new Vec3(0.5D, AltarRitualAnimation.altarItemY(time), 0.5D), time, 2.0F);
        }
        float progress = altar.getClientProgress(partialTick);
        if (progress <= 0.0F || altar.getClientJars().isEmpty()) return;
        float growth = Mth.clamp(progress / LINE_GROWTH_PORTION, 0.0F, 1.0F);
        BlockPos altarPos = altar.getBlockPos();
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vector3f camera = new Vector3f((float) (camPos.x - altarPos.getX()),
                (float) (camPos.y - altarPos.getY()), (float) (camPos.z - altarPos.getZ()));
        VertexConsumer beams = buffers.getBuffer(PoRenderTypes.altarBeam());
        for (BlockPos pos : altar.getClientJars()) {
            if (!level.hasChunkAt(pos) || !(level.getBlockEntity(pos) instanceof GlassJarBlockEntity jar)
                    || jar.getTank().isEmpty()) continue;
            int color = GlassJarBlockEntityRenderer.getFluidColor(jar.getTank().getFluid());
            float dx = pos.getX() - altarPos.getX();
            float dz = pos.getZ() - altarPos.getZ();
            Vector3f[] points = new Vector3f[LINE_SEGMENTS + 1];
            for (int i = 0; i <= LINE_SEGMENTS; i++) {
                points[i] = point(dx, dz, growth * i / LINE_SEGMENTS, time);
            }
            Vector3f[] sides = new Vector3f[LINE_SEGMENTS + 1];
            for (int i = 0; i <= LINE_SEGMENTS; i++) {
                Vector3f tangent = new Vector3f(points[Math.min(i + 1, LINE_SEGMENTS)])
                        .sub(points[Math.max(i - 1, 0)]);
                Vector3f side = tangent.cross(new Vector3f(camera).sub(points[i]));
                sides[i] = side.lengthSquared() < 1.0E-6F
                        ? new Vector3f(0.0F, BEAM_HALF_WIDTH, 0.0F)
                        : side.normalize(BEAM_HALF_WIDTH);
            }
            for (int i = 0; i < LINE_SEGMENTS; i++) {
                beamQuad(beams, poses.last(), points[i], sides[i], points[i + 1], sides[i + 1], color);
            }
        }
    }
}
