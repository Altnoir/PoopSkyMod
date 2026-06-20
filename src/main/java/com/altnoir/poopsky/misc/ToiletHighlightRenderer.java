package com.altnoir.poopsky.misc;

import com.altnoir.poopsky.block.ToiletComponent;
import com.altnoir.poopsky.init.PComponents;
import com.altnoir.poopsky.item.p.ToiletLinkerItem;
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
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class ToiletHighlightRenderer {
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

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

        ToiletComponent comp = stack.get(PComponents.TOILET_COMPONENT.get());
        if (comp == null || comp.level1().isEmpty() || !comp.level2().isEmpty()) {
            return;
        }

        String currentDim = player.level().dimension().location().toString();
        if (!currentDim.equals(comp.level1())) {
            return;
        }

        BlockPos pos = new BlockPos(comp.x1(), comp.y1(), comp.z1());

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        AABB aabb = new AABB(pos).move(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        aabb = aabb.inflate(0.01D);

        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aabb, 0.804F, 0.522F, 0.247F, 1.0F);

        bufferSource.endBatch(RenderType.lines());
    }
}