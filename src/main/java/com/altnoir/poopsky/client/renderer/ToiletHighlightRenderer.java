package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.content.block.ToiletComponent;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.content.item.p.ToiletLinkerItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public class ToiletHighlightRenderer {
    public static void onRenderLevel(WorldRenderContext context) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ToiletLinkerItem)) {
            stack = player.getOffhandItem();
            if (!(stack.getItem() instanceof ToiletLinkerItem)) {
                return;
            }
        }

        ToiletComponent comp = stack.get(PoComponents.TOILET_COMPONENT.get());
        if (comp == null || comp.level1().isEmpty() || !comp.level2().isEmpty()) {
            return;
        }

        String currentDim = player.level().dimension().location().toString();
        if (!currentDim.equals(comp.level1())) {
            return;
        }

        BlockPos pos = new BlockPos(comp.x1(), comp.y1(), comp.z1());

        PoseStack poseStack = context.matrixStack();
        if (poseStack == null) return;
        Camera camera = context.camera();
        Vec3 cameraPos = camera.getPosition();

        MultiBufferSource bufferSource = context.consumers();
        if (bufferSource == null) return;
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        AABB aabb = new AABB(pos).move(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        aabb = aabb.inflate(0.01D);

        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aabb, 0.804F, 0.522F, 0.247F, 1.0F);
    }
}
