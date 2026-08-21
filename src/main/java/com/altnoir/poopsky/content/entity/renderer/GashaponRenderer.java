package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.p.GashaponEntity;
import com.altnoir.poopsky.content.item.p.GashaponItem;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResourceSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class GashaponRenderer extends EntityRenderer<GashaponEntity, GashaponRenderer.RenderState> {
    private static final String[] COLORS = {GashaponItem.PINK, GashaponItem.RED, GashaponItem.YELLOW, GashaponItem.BLUE};

    public GashaponRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.35F;
    }

    @Override public RenderState createRenderState() { return new RenderState(); }

    @Override
    public void extractRenderState(GashaponEntity entity, RenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.texture = getTexture(COLORS[Math.floorMod(entity.getVariant(), COLORS.length)]);
        state.yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        var resources = BedrockModelResourceSet.getInstance();
        var model = resources == null ? null : resources.getModel(PoBedrockModelResources.GASHAPON);
        if (model != null) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(state.texture), (pose, consumer) -> {
                PoseStack modelPose = new PoseStack();
                modelPose.last().set(pose);
                model.renderToBuffer(modelPose, consumer, state.lightCoords, OverlayTexture.NO_OVERLAY);
            });
            poseStack.popPose();
        }
        super.submit(state, poseStack, collector, camera);
    }

    public static Identifier getTexture(String color) {
        String suffix = switch (color) {
            case GashaponItem.BLUE -> "blue";
            case GashaponItem.YELLOW -> "yellow";
            case GashaponItem.RED -> "red";
            default -> "pink";
        };
        return PoopSky.loc("textures/entity/gashapon/gashapon_" + suffix + ".png");
    }

    public static class RenderState extends EntityRenderState {
        private Identifier texture;
        private float yRot;
    }
}
