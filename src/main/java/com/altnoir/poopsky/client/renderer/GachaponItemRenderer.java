package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.renderer.GachaponRenderer;
import com.altnoir.poopsky.content.item.p.GachaponItem;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResourceSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class GachaponItemRenderer implements SpecialModelRenderer<String> {
    @Override
    public void submit(@Nullable String color, PoseStack poseStack, SubmitNodeCollector collector,
                       int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        var resources = BedrockModelResourceSet.getInstance();
        var model = resources == null ? null : resources.getModel(PoBedrockModelResources.GACHAPON);
        if (model == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        collector.submitCustomGeometry(
                poseStack,
                RenderTypes.entityCutout(GachaponRenderer.getTexture(color == null ? GachaponItem.PINK : color)),
                (pose, consumer) -> {
                    PoseStack modelPose = new PoseStack();
                    modelPose.last().set(pose);
                    model.renderToBuffer(modelPose, consumer, lightCoords, OverlayTexture.NO_OVERLAY);
                });
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(0.0F, 0.0F, 0.0F));
        output.accept(new Vector3f(1.0F, 1.0F, 1.0F));
    }

    @Override
    public String extractArgument(ItemStack stack) {
        return GachaponItem.getColor(stack);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<String> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public GachaponItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new GachaponItemRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
