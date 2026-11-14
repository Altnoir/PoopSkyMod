package com.altnoir.poopsky.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class FloatingItemRenderer {
    private FloatingItemRenderer() {
    }

    public static void render(ItemRenderer itemRenderer, ItemStack stack, Level level, PoseStack poses,
                              MultiBufferSource buffers, int light, Vec3 position, double time, float scale) {
        if (stack.isEmpty()) {
            return;
        }
        var model = itemRenderer.getModel(stack, level, null, 0);
        var offset = model.getTransforms().getTransform(ItemDisplayContext.GROUND).translation;
        poses.pushPose();
        poses.translate(position.x, position.y, position.z);
        poses.mulPose(Axis.YP.rotation((float) (time / 20.0D % Mth.TWO_PI)));
        if (scale != 1.0F) {
            poses.scale(scale, scale, scale);
        }
        poses.translate(-offset.x(), -offset.y(), -offset.z());
        itemRenderer.render(stack, ItemDisplayContext.GROUND, false, poses, buffers, light,
                OverlayTexture.NO_OVERLAY, model);
        poses.popPose();
    }
}
