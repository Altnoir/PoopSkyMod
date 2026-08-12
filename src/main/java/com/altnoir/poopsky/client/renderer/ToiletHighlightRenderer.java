package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.content.block.ToiletComponent;
import com.altnoir.poopsky.content.block.p.FlushToiletBlock;
import com.altnoir.poopsky.content.item.p.ToiletLinkerItem;
import com.altnoir.poopsky.init.PoComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class ToiletHighlightRenderer {
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentBlocks event) {
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

        String currentDim = player.level().dimension().identifier().toString();
        if (!currentDim.equals(comp.level1())) {
            return;
        }

        BlockPos pos = new BlockPos(comp.x1(), comp.y1(), comp.z1());

        PoseStack poseStack = event.getPoseStack();
        CameraRenderState camera = event.getLevelRenderState().cameraRenderState;
        Vec3 cameraPos = camera.pos;

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypes.lines());

        BlockState state = player.level().getBlockState(pos);
        VoxelShape shape = state.getShape(player.level(), pos, CollisionContext.of(player));
        if (state.getBlock() instanceof FlushToiletBlock) {
            shape = Shapes.or(shape, getFlushToiletLidShape(state));
        }
        ShapeRenderer.renderShape(poseStack, vertexConsumer, shape,
                pos.getX() - cameraPos.x,
                pos.getY() - cameraPos.y,
                pos.getZ() - cameraPos.z,
                ARGB.color(255, 205, 133, 63), 1.0F);

        bufferSource.endBatch(RenderTypes.lines());
    }

    private static VoxelShape getFlushToiletLidShape(BlockState state) {
        Direction facing = state.getValue(FlushToiletBlock.FACING);
        if (state.getValue(FlushToiletBlock.CLOSED)) {
            return switch (facing) {
                case EAST -> Block.box(4, 8, 4, 14, 9, 12);
                case SOUTH -> Block.box(4, 8, 4, 12, 9, 14);
                case WEST -> Block.box(2, 8, 4, 12, 9, 12);
                default -> Block.box(4, 8, 2, 12, 9, 12);
            };
        }
        return switch (facing) {
            case EAST -> Block.box(4, 8, 4, 5, 18, 12);
            case SOUTH -> Block.box(4, 8, 4, 12, 18, 5);
            case WEST -> Block.box(11, 8, 4, 12, 18, 12);
            default -> Block.box(4, 8, 11, 12, 18, 12);
        };
    }
}
