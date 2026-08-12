package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.p.FlushToiletCartEntity;
import com.altnoir.poopsky.init.PoEntityType;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.BedrockBone;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.BedrockModel;
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

public class FlushToiletCartRenderer extends EntityRenderer<FlushToiletCartEntity, FlushToiletCartRenderer.RenderState> {
    private static final Identifier TEXTURE = PoopSky.loc("textures/block/flush_toilet_cart.png");
    private static final Identifier GOLDEN_TEXTURE = PoopSky.loc("textures/block/golden_flush_toilet_cart.png");

    public FlushToiletCartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(FlushToiletCartEntity entity, RenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.golden = entity.getType().equals(PoEntityType.GOLDEN_FLUSH_TOILET_CART.get());
        state.yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        state.wheelLeftRotation = entity.getWheelLeftRotation(partialTick);
        state.wheelRightRotation = entity.getWheelRightRotation(partialTick);
        state.hurtTicks = entity.getHurtTime() - partialTick;
        state.hurtDamage = Math.max(0.0F, entity.getDamage() - partialTick);
        state.hurtDirection = entity.getHurtDir();
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        BedrockModelResourceSet resourceSet = BedrockModelResourceSet.getInstance();
        if (resourceSet != null) {
            BedrockModel model = resourceSet.getModel(PoBedrockModelResources.FLUSH_TOILET_CART);
            if (model != null) {
                Identifier texture = state.golden ? GOLDEN_TEXTURE : TEXTURE;
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));
                if (state.hurtTicks > 0.0F) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(
                            Mth.sin(state.hurtTicks) * state.hurtTicks * state.hurtDamage / 10.0F * state.hurtDirection
                    ));
                }
                collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(texture), (pose, consumer) -> {
                    BedrockBone wheelLeft = model.getBone("wheel_l");
                    BedrockBone wheelRight = model.getBone("wheel_r");
                    if (wheelLeft != null) {
                        wheelLeft.rotation.rotationX((float) -Math.toRadians(state.wheelLeftRotation));
                    }
                    if (wheelRight != null) {
                        wheelRight.rotation.rotationX((float) -Math.toRadians(state.wheelRightRotation));
                    }

                    PoseStack modelPose = new PoseStack();
                    modelPose.last().set(pose);
                    model.renderToBuffer(modelPose, consumer, state.lightCoords, OverlayTexture.NO_OVERLAY);
                    if (wheelLeft != null) {
                        wheelLeft.rotation.identity();
                    }
                    if (wheelRight != null) {
                        wheelRight.rotation.identity();
                    }
                });
                poseStack.popPose();
            }
        }
        super.submit(state, poseStack, collector, camera);
    }

    public static class RenderState extends EntityRenderState {
        private boolean golden;
        private float yRot;
        private float wheelLeftRotation;
        private float wheelRightRotation;
        private float hurtTicks;
        private float hurtDamage;
        private int hurtDirection;
    }
}
