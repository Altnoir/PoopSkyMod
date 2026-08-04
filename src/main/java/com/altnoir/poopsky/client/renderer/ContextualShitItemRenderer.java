package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.client.model.BakedModelEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class ContextualShitItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final String modelPath;

    public ContextualShitItemRenderer(String modelPath) {
        this.modelPath = modelPath;
    }

    @Override
    public void render(
            ItemStack stack,
            ItemDisplayContext context,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        boolean useBlockModel = context == ItemDisplayContext.HEAD || context == ItemDisplayContext.GROUND;
        BakedModel model = BakedModelEventHandler.getContextualShitModel(modelPath, useBlockModel);
        if (model == null) {
            return;
        }

        boolean leftHand = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        Minecraft.getInstance().getItemRenderer().render(
                stack,
                context,
                leftHand,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                model
        );
    }
}
