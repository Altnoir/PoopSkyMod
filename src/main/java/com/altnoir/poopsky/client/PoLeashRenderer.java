package com.altnoir.poopsky.client;

import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/** 1.20.1 only renders leashes for mobs; provide the same braided ribbon for the plug. */
public final class PoLeashRenderer {
    public static void render(ToiletPlugEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffers) {
        var holder = entity.getLeashHolder();
        if (holder == null) return;
        Vec3 end = holder.getRopeHoldPosition(partialTick);
        Vec3 attachment = entity.getLeashOffset();
        double yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) * Mth.DEG_TO_RAD + Math.PI / 2;
        double ox = Math.cos(yaw) * attachment.z + Math.sin(yaw) * attachment.x;
        double oz = Math.sin(yaw) * attachment.z - Math.cos(yaw) * attachment.x;
        double x = Mth.lerp(partialTick, entity.xo, entity.getX()) + ox;
        double y = Mth.lerp(partialTick, entity.yo, entity.getY()) + attachment.y;
        double z = Mth.lerp(partialTick, entity.zo, entity.getZ()) + oz;
        float dx = (float) (end.x - x), dy = (float) (end.y - y), dz = (float) (end.z - z);
        float inv = Mth.invSqrt(dx * dx + dz * dz) * 0.0125F;
        float sideX = dz * inv, sideZ = dx * inv;
        BlockPos a = BlockPos.containing(entity.getEyePosition(partialTick));
        BlockPos b = BlockPos.containing(holder.getEyePosition(partialTick));
        int blockA = entity.level().getBrightness(LightLayer.BLOCK, a);
        int blockB = holder.isOnFire() ? 15 : entity.level().getBrightness(LightLayer.BLOCK, b);
        int skyA = entity.level().getBrightness(LightLayer.SKY, a), skyB = entity.level().getBrightness(LightLayer.SKY, b);
        pose.pushPose(); pose.translate(ox, attachment.y, oz);
        VertexConsumer out = buffers.getBuffer(RenderType.leash());
        Matrix4f matrix = pose.last().pose();
        for (int i = 0; i <= 24; i++) segment(out, matrix, dx, dy, dz, blockA, blockB, skyA, skyB, sideX, sideZ, i, false);
        for (int i = 24; i >= 0; i--) segment(out, matrix, dx, dy, dz, blockA, blockB, skyA, skyB, sideX, sideZ, i, true);
        pose.popPose();
    }
    private static void segment(VertexConsumer out, Matrix4f matrix, float dx, float dy, float dz, int ba, int bb, int sa, int sb, float ox, float oz, int index, boolean reverse) {
        float t = index / 24.0F;
        int light = LightTexture.pack((int) Mth.lerp(t, ba, bb), (int) Mth.lerp(t, sa, sb));
        float shade = index % 2 == (reverse ? 1 : 0) ? 0.7F : 1;
        float x = dx * t, y = dy > 0 ? dy * t * t : dy - dy * (1-t) * (1-t), z = dz * t;
        float top = reverse ? 0.025F : 0;
        out.vertex(matrix, x - ox, y + top, z + oz).color(0.5F * shade, 0.4F * shade, 0.3F * shade, 1).uv2(light).endVertex();
        out.vertex(matrix, x + ox, y + 0.025F - top, z - oz).color(0.5F * shade, 0.4F * shade, 0.3F * shade, 1).uv2(light).endVertex();
    }
    private PoLeashRenderer() {}
}
