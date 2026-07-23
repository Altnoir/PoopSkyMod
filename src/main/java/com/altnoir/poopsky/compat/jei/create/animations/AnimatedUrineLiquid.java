/*
package com.altnoir.poopsky.compat.jei.create.animations;

import com.altnoir.poopsky.init.PoBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class AnimatedUrineLiquid extends AnimatedKinetics {

    public static float getLiquidHeight() {
        return 0.1f * Mth.sin(((AnimationTickHolder.getRenderTime() * 2f) % 360) * (float) Math.PI / 180);
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 0);
        int scale = 24;

        blockElement(PoBlocks.URINE_LIQUID.get().defaultBlockState())
                .atLocal(0, 0, 2)
                .scale(scale)
                .render(graphics);

        matrixStack.popPose();
    }
}
 */