package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.p.SnailEntity;
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
import org.joml.Quaternionf;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class SnailRenderer extends EntityRenderer<SnailEntity> {
    private static final EulerAdditiveBlender BLENDER = new SimpleEulerAdditiveBlender(new ZYXBoneTransformFactory(), ArrayPoseBuilder::new);
    private static final Map<SnailEntity.Color, ResourceLocation> TEXTURES = new EnumMap<>(SnailEntity.Color.class);

    static {
        for (SnailEntity.Color color : SnailEntity.Color.values()) {
            TEXTURES.put(color, PoopSky.loc("textures/entity/snail/" + color.name().toLowerCase() + "_snail.png"));
        }
    }

    private final WeakHashMap<SnailEntity, AnimationState> instances = new WeakHashMap<>();
    private BakedBedrockModel model;
    private BedrockAnimation walkAnimation;

    public SnailRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.4F;
    }

    @Override
    public ResourceLocation getTextureLocation(SnailEntity entity) {
        return TEXTURES.get(entity.getColor());
    }

    @Override
    public void render(SnailEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        this.refreshResources();
        if (this.model == null) {
            return;
        }

        AnimationState animationState = this.instances.computeIfAbsent(entity, ignored -> new AnimationState(this.model.createInstance()));
        BakedModelInstance instance = animationState.instance;
        instance.resetPose();
        BoneState head = instance.getBone("head");
        Quaternionf restingHeadRotation = head == null ? null : new Quaternionf(head.rotation);
        double movedX = entity.getX() - entity.xo;
        double movedY = entity.getY() - entity.yo;
        double movedZ = entity.getZ() - entity.zo;
        double movedSqr = movedX * movedX + movedZ * movedZ;
        if (entity.isClimbing()) {
            movedSqr += movedY * movedY;
        }
        if (this.walkAnimation != null) {
            float length = this.walkAnimation.getSpecifiedEndTimeS();
            float elapsed = animationState.update(entity.tickCount, partialTick, movedSqr > 1.0E-6D, length);
            Pose pose = this.walkAnimation.evaluate(elapsed);
            instance.applyPose(BLENDER.blend(instance.getBindPose(), pose));
        }
        float climbProgress = animationState.updateClimb(entity, partialTick);
        if (head != null) {
            head.rotation.slerp(restingHeadRotation, climbProgress);
            float pitch = Mth.clamp(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()), -22.5F, 22.5F);
            head.rotation.rotateX(-pitch * (1.0F - climbProgress) * Mth.DEG_TO_RAD);
        }

        poseStack.pushPose();
        float groundYaw = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        float bodyYaw = Mth.rotLerp(climbProgress, groundYaw, entity.getWallDirection().toYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.translate(0.0F, entity.getBbWidth() * climbProgress, -entity.getBbWidth() * 0.5F * climbProgress);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F * climbProgress));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
        instance.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.pack(0.0F, entity.hurtTime > 0 || entity.deathTime > 0));
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void refreshResources() {
        BedrockModelResources resources = BedrockModelResources.getInstance();
        if (resources == null) {
            return;
        }
        BakedBedrockModel loadedModel = resources.getBakedModel(PoBedrockModelResources.SNAIL_MODEL);
        if (loadedModel == this.model) {
            return;
        }
        this.model = loadedModel;
        this.instances.clear();
        List<BedrockAnimation> animations = resources.getAnimations(
                PoBedrockModelResources.SNAIL_MODEL, PoBedrockModelResources.SNAIL_ANIMATION);
        this.walkAnimation = animations == null ? null : animations.stream()
                .filter(animation -> "walk".equals(animation.getName()))
                .findFirst()
                .orElse(null);
    }

    private static class AnimationState {
        private final BakedModelInstance instance;
        private int lastTick = -1;
        private int lastMovingTick = Integer.MIN_VALUE / 2;
        private float previousTime;
        private float time;
        private float lastRenderTick = -1.0F;
        private float climbProgress;

        private AnimationState(BakedModelInstance instance) {
            this.instance = instance;
        }

        private float updateClimb(SnailEntity entity, float partialTick) {
            float renderTick = entity.tickCount + partialTick;
            float target = entity.isClimbing() ? 1.0F : 0.0F;
            if (this.lastRenderTick < 0.0F) {
                this.climbProgress = target;
            } else {
                float step = Math.max(0.0F, renderTick - this.lastRenderTick) / 5.0F;
                this.climbProgress = Mth.clamp(target, this.climbProgress - step, this.climbProgress + step);
            }
            this.lastRenderTick = renderTick;
            return this.climbProgress;
        }

        private float update(int tick, float partialTick, boolean moving, float length) {
            if (tick != this.lastTick) {
                this.lastTick = tick;
                this.previousTime = this.time;
                if (moving) {
                    this.lastMovingTick = tick;
                }
                if (length > 0.0F && (tick - this.lastMovingTick <= 5 || this.time > 0.0F)) {
                    this.time += 1.0F / 20.0F;
                    if (this.time >= length) {
                        this.time = tick - this.lastMovingTick <= 5 ? this.time - length : 0.0F;
                    }
                }
            }
            float interpolated = Mth.lerp(partialTick, this.previousTime, this.time < this.previousTime ? this.time + length : this.time);
            return length > 0.0F ? interpolated % length : 0.0F;
        }
    }
}
