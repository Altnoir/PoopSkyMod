package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.p.BasiliskEntity;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.animation.BedrockAnimation;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BoneState;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import com.maydaymemory.mae.basic.ArrayPoseBuilder;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.ZYXBoneTransformFactory;
import com.maydaymemory.mae.blend.EulerAdditiveBlender;
import com.maydaymemory.mae.blend.SimpleEulerAdditiveBlender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class BasiliskRenderer extends EntityRenderer<BasiliskEntity> {
    private static final ResourceLocation TEXTURE = PoopSky.loc("textures/entity/basilisk.png");
    private static final EulerAdditiveBlender BLENDER = new SimpleEulerAdditiveBlender(new ZYXBoneTransformFactory(), ArrayPoseBuilder::new);

    private final WeakHashMap<BasiliskEntity, AnimationState> instances = new WeakHashMap<>();
    private final Map<String, BedrockAnimation> animations = new HashMap<>();
    private BakedBedrockModel model;

    public BasiliskRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.55F;
    }

    @Override
    public ResourceLocation getTextureLocation(BasiliskEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BasiliskEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.refreshResources();
        if (this.model == null) {
            return;
        }

        AnimationState state = this.instances.computeIfAbsent(entity, ignored -> new AnimationState(this.model.createInstance()));
        BakedModelInstance instance = state.instance;
        instance.resetPose();
        BedrockAnimation walking = this.animations.get("walking");
        float walkTime = state.updateWalk(entity, partialTick, walking);
        BedrockAnimation animation = entity.getAttackAnimationTicks() > 0
                ? this.animations.get("attack")
                : walkTime >= 0.0F ? walking : null;
        if (animation != null) {
            float animationTime = animation == walking ? walkTime
                    : (BasiliskEntity.getAttackAnimationDuration() - entity.getAttackAnimationTicks() + partialTick) / 20.0F;
            Pose animatedPose = animation.evaluate(animationTime);
            instance.applyPose(BLENDER.blend(instance.getBindPose(), animatedPose));
        }
        BoneState head = instance.getBone("head");
        if (head != null) {
            float pitch = Mth.clamp(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()), -22.5F, 22.5F);
            head.rotation.rotateX(-pitch * Mth.DEG_TO_RAD);
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot)));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        instance.renderToBuffer(
                poseStack,
                consumer,
                packedLight,
                OverlayTexture.pack(0.0F, entity.hurtTime > 0 || entity.deathTime > 0));
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void refreshResources() {
        BedrockModelResources resources = BedrockModelResources.getInstance();
        if (resources == null) {
            return;
        }
        BakedBedrockModel loadedModel = resources.getBakedModel(PoBedrockModelResources.BASILISK_MODEL);
        if (loadedModel == this.model) {
            return;
        }

        this.model = loadedModel;
        this.instances.clear();
        this.animations.clear();
        if (loadedModel == null) {
            return;
        }
        List<BedrockAnimation> loadedAnimations = resources.getAnimations(
                PoBedrockModelResources.BASILISK_MODEL,
                PoBedrockModelResources.BASILISK_ANIMATION);
        if (loadedAnimations != null) {
            for (BedrockAnimation animation : loadedAnimations) {
                this.animations.put(animation.getName(), animation);
            }
        }
    }

    private static class AnimationState {
        private final BakedModelInstance instance;
        private float lastRenderTick = -1.0F;
        private float walkTime = -1.0F;

        private AnimationState(BakedModelInstance instance) {
            this.instance = instance;
        }

        private float updateWalk(BasiliskEntity entity, float partialTick, BedrockAnimation animation) {
            float renderTick = entity.tickCount + partialTick;
            float elapsed = this.lastRenderTick < 0.0F ? 0.0F : Math.max(0.0F, renderTick - this.lastRenderTick) / 20.0F;
            this.lastRenderTick = renderTick;
            if (animation == null || animation.getSpecifiedEndTimeS() <= 0.0F) {
                this.walkTime = -1.0F;
                return this.walkTime;
            }
            float length = animation.getSpecifiedEndTimeS();
            if (this.walkTime < 0.0F) {
                if (entity.getHopTick() >= 0) {
                    this.walkTime = (entity.getHopTick() + partialTick) / 20.0F % length;
                }
            } else {
                this.walkTime += elapsed;
                if (this.walkTime >= length) {
                    this.walkTime = entity.getHopTick() >= 0 ? this.walkTime % length : -1.0F;
                }
            }
            return this.walkTime;
        }
    }
}
