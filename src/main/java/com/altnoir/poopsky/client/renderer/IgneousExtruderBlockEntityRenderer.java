package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.content.block.entity.IgneousExtruderBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IgneousExtruderBlockEntityRenderer implements BlockEntityRenderer<IgneousExtruderBlockEntity> {
    private final ItemRenderer itemRenderer;

    public IgneousExtruderBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    private static int getOpeningLight(Level level, BlockPos pos, int packedLight) {
        // Light reaches the display through the side openings in the model.
        int blockLight = LightTexture.block(packedLight);
        int skyLight = LightTexture.sky(packedLight);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int sideLight = LevelRenderer.getLightColor(level, pos.relative(direction));
            blockLight = Math.max(blockLight, LightTexture.block(sideLight));
            skyLight = Math.max(skyLight, LightTexture.sky(sideLight));
        }
        return LightTexture.pack(blockLight, skyLight);
    }

    @Override
    public void render(IgneousExtruderBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        ItemStack stack = blockEntity.getDisplayStack();
        if (level == null || stack.isEmpty()) {
            return;
        }

        double time = level.getGameTime() + (double) partialTick;
        float spin = (float) (time / 40.0D % Mth.TWO_PI);
        var model = itemRenderer.getModel(stack, level, null, 0);
        var offset = model.getTransforms().getTransform(ItemDisplayContext.GROUND).translation;
        int light = getOpeningLight(level, blockEntity.getBlockPos(), packedLight);

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.mulPose(Axis.YP.rotation(spin));
        poseStack.translate(-offset.x(), -offset.y(), -offset.z());
        itemRenderer.render(stack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, light, OverlayTexture.NO_OVERLAY, model);
        poseStack.popPose();
    }
}
