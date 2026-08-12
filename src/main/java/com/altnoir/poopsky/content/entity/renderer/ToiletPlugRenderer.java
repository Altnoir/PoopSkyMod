package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.render.ToiletPlugRenderState;
import com.altnoir.poopsky.content.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class ToiletPlugRenderer extends EntityRenderer<ToiletPlugEntity, ToiletPlugRenderState> {
    private static final Identifier TEXTURE = PoopSky.loc("textures/entity/toilet_plug.png");
    private final ToiletPlugModel plugModel;

    public ToiletPlugRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.plugModel = new ToiletPlugModel(context.bakeLayer(ToiletPlugModel.LAYER_LOCATION));
    }

    @Override
    public ToiletPlugRenderState createRenderState() {
        return new ToiletPlugRenderState();
    }

    @Override
    public void extractRenderState(ToiletPlugEntity entity, ToiletPlugRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.viewYRot = entity.getViewYRot(partialTick);
        state.viewXRot = entity.getViewXRot(partialTick);
        state.floatingValue = entity.getFloatingValue(partialTick);
        state.hurtTicks = entity.getHurtTime() - partialTick;
        state.hurtDamage = Math.max(0.0F, entity.getDamage() - partialTick);
        state.hurtDirection = entity.getHurtDir();
    }

    @Override
    public void submit(ToiletPlugRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.0D, state.floatingValue - 1.0D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.viewYRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.viewXRot));
        if (state.hurtTicks > 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(
                    Mth.sin(state.hurtTicks) * state.hurtTicks * state.hurtDamage / 10.0F * state.hurtDirection
            ));
        }

        collector.submitModel(this.plugModel, state, poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
