package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.p.BasiliskEntity;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.animation.BedrockAnimation;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
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

    private final WeakHashMap<BasiliskEntity, BakedModelInstance> instances = new WeakHashMap<>();
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

        BakedModelInstance instance = this.instances.computeIfAbsent(entity, ignored -> this.model.createInstance());
        instance.resetPose();
        BedrockAnimation animation = this.selectAnimation(entity);
        float animationTime = this.getAnimationTime(entity, animation, partialTick);
        Pose animatedPose = animation.evaluate(animationTime);
        instance.applyPose(BLENDER.blend(instance.getBindPose(), animatedPose));

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

    private BedrockAnimation selectAnimation(BasiliskEntity entity) {
        if (entity.getAttackAnimationTicks() > 0) {
            return this.animations.get("attack");
        }
        if (entity.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4D) {
            return this.animations.get("walking");
        }
        return this.animations.get("idle");
    }

    private float getAnimationTime(BasiliskEntity entity, BedrockAnimation animation, float partialTick) {
        if (entity.getAttackAnimationTicks() > 0 && "attack".equals(animation.getName())) {
            return (BasiliskEntity.getAttackAnimationDuration() - entity.getAttackAnimationTicks() + partialTick) / 20.0F;
        }
        float length = animation.getSpecifiedEndTimeS();
        float elapsed = (entity.tickCount + partialTick) / 20.0F;
        return length > 0.0F ? elapsed % length : elapsed;
    }
}
