package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.p.SnailEntity;
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

    private final WeakHashMap<SnailEntity, BakedModelInstance> instances = new WeakHashMap<>();
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

        BakedModelInstance instance = this.instances.computeIfAbsent(entity, ignored -> this.model.createInstance());
        instance.resetPose();
        if (this.walkAnimation != null) {
            float elapsed = (entity.tickCount + partialTick) / 20.0F;
            float length = this.walkAnimation.getSpecifiedEndTimeS();
            Pose pose = this.walkAnimation.evaluate(length > 0.0F ? elapsed % length : elapsed);
            instance.applyPose(BLENDER.blend(instance.getBindPose(), pose));
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot)));
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
}
