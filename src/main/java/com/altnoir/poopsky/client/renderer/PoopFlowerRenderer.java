package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.content.block.entity.PoopFlowerBlockEntity;
import com.altnoir.poopsky.content.block.p.ShitBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;

public class PoopFlowerRenderer implements BlockEntityRenderer<PoopFlowerBlockEntity> {
    private final BlockRenderDispatcher blocks;

    public PoopFlowerRenderer(BlockEntityRendererProvider.Context context) {
        blocks = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(PoopFlowerBlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        if (!(entity.getFlower().getItem() instanceof BlockItem item)) return;
        pose.pushPose();
        pose.translate(0.5, 0, 0.5);
        float rotation = switch (entity.getBlockState().getValue(ShitBlock.FACING)) {
            case EAST -> -90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 90.0F;
            default -> 0.0F;
        };
        pose.mulPose(Axis.YP.rotationDegrees(rotation));
        pose.translate(-0.5, 0, -0.5);
        pose.translate(3.5 / 16.0, 10.5 / 16.0, 3.5 / 16.0);
        pose.scale(0.5F, 0.5F, 0.5F);
        blocks.renderSingleBlock(item.getBlock().defaultBlockState(), pose, buffers, light, overlay);
        pose.popPose();
    }
}
