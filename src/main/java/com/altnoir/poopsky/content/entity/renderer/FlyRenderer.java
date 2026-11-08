package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.content.entity.model.FlyModel;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import com.maydaymemory.mae.basic.ArrayPoseBuilder;
import com.maydaymemory.mae.basic.BoneTransform;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.ZYXBoneTransformFactory;
import com.maydaymemory.mae.blend.EulerAdditiveBlender;
import com.maydaymemory.mae.blend.SimpleEulerAdditiveBlender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import org.joml.Vector3f;

import java.util.*;

public class FlyRenderer extends MobRenderer<FlyEntity, FlyModel<FlyEntity>> {
    private static final Map<DyeColor, ResourceLocation> MAGGOT_TEXTURES = new EnumMap<>(DyeColor.class);
    private static final Map<DyeColor, ResourceLocation> FLY_TEXTURES = new EnumMap<>(DyeColor.class);
    private static final ZYXBoneTransformFactory TRANSFORM_FACTORY = new ZYXBoneTransformFactory();
    private static final EulerAdditiveBlender BLENDER = new SimpleEulerAdditiveBlender(TRANSFORM_FACTORY, ArrayPoseBuilder::new);

    static {
        for (DyeColor color : DyeColor.values()) {
            String name = color == DyeColor.WHITE ? "fly" : "fly_" + color.getName();
            FLY_TEXTURES.put(color, PoopSky.loc("textures/entity/fly/" + name + ".png"));
            String maggotName = color == DyeColor.WHITE ? "maggot" : "maggot_" + color.getName();
            MAGGOT_TEXTURES.put(color, PoopSky.loc("textures/entity/maggot/" + maggotName + ".png"));
        }
    }

    private final WeakHashMap<FlyEntity, BakedModelInstance> maggotInstances = new WeakHashMap<>();
    private BakedBedrockModel maggotModel;

    public FlyRenderer(EntityRendererProvider.Context context) {
        super(context, new FlyModel<>(context.bakeLayer(FlyModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(FlyEntity entity) {
        return entity.isBaby()
                ? MAGGOT_TEXTURES.get(entity.getColor())
                : FLY_TEXTURES.get(entity.getColor());
    }

    @Override
    public void render(FlyEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if (!entity.isBaby()) {
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            return;
        }

        this.refreshMaggotResources();
        if (this.maggotModel == null) {
            return;
        }

        BakedModelInstance instance = this.maggotInstances.computeIfAbsent(entity, ignored -> this.maggotModel.createInstance());
        instance.resetPose();
        instance.applyPose(BLENDER.blend(instance.getBindPose(), this.createSilverfishPose(entity.tickCount + partialTick)));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot)));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
        instance.renderToBuffer(poseStack, consumer, packedLight,
                OverlayTexture.pack(0.0F, entity.hurtTime > 0 || entity.deathTime > 0));
        poseStack.popPose();
    }

    private void refreshMaggotResources() {
        BedrockModelResources resources = BedrockModelResources.getInstance();
        if (resources == null) {
            return;
        }
        BakedBedrockModel loadedModel = resources.getBakedModel(PoBedrockModelResources.MAGGOT_MODEL);
        if (loadedModel != this.maggotModel) {
            this.maggotModel = loadedModel;
            this.maggotInstances.clear();
        }
    }

    private Pose createSilverfishPose(float ageInTicks) {
        List<BoneTransform> transforms = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            float phase = ageInTicks * 0.9F + i * 0.15F * (float) Math.PI;
            float rotation = Mth.cos(phase) * (float) Math.PI * 0.05F * (1 + Math.abs(i - 2));
            float x = Mth.sin(phase) * (float) Math.PI * 0.2F * Math.abs(i - 2);
            int boneIndex = this.maggotModel.getIndex("bodyPart_" + i);
            if (boneIndex >= 0) {
                transforms.add(TRANSFORM_FACTORY.createBoneTransform(
                        boneIndex,
                        new Vector3f(-x, 0.0F, 0.0F),
                        new Vector3f(0.0F, -rotation, 0.0F),
                        new Vector3f(1.0F)
                ));
            }
        }
        Collections.sort(transforms);
        ArrayPoseBuilder builder = new ArrayPoseBuilder(transforms.size());
        transforms.forEach(builder::addBoneTransform);
        return builder.toPose();
    }
}
