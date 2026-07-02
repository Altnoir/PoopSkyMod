package com.altnoir.poopsky.entity.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.p.MaggotEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MaggotModel<T extends MaggotEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(PoopSky.loc("maggot"), "main");

    private final ModelPart maggotRoot;
    private final ModelPart bodyFront;
    private final ModelPart head;
    private final ModelPart bodyMid;
    private final ModelPart bodyBack;
    private final ModelPart tailTip;

    public MaggotModel(ModelPart root) {
        this.maggotRoot = root.getChild("maggot_root");
        this.bodyFront = this.maggotRoot.getChild("body_front");
        this.head = this.bodyFront.getChild("head");
        this.bodyMid = this.maggotRoot.getChild("body_mid");
        this.bodyBack = this.maggotRoot.getChild("body_back");
        this.tailTip = this.bodyBack.getChild("tail_tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition maggotRoot = partdefinition.addOrReplaceChild("maggot_root", CubeListBuilder.create(), PartPose.offset(0.0F, 22.5F, 0.0F));

        PartDefinition bodyFront = maggotRoot.addOrReplaceChild("body_front", CubeListBuilder.create()
                        .texOffs(0, 8)
                        .addBox(-1.9F, -1.35F, -1.25F, 3.8F, 2.75F, 2.5F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.15F, -2.75F));

        bodyFront.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(6, 5)
                        .addBox(-1.5F, -1.1F, -1.0F, 3.0F, 2.25F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.25F, -2.25F));

        maggotRoot.addOrReplaceChild("body_mid", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -1.5F, -1.75F, 4.0F, 3.0F, 3.5F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.25F));

        PartDefinition bodyBack = maggotRoot.addOrReplaceChild("body_back", CubeListBuilder.create()
                        .texOffs(0, 5)
                        .addBox(-1.7F, -1.25F, -1.25F, 3.4F, 2.55F, 2.5F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.25F, 3.25F));

        bodyBack.addOrReplaceChild("tail_tip", CubeListBuilder.create()
                        .texOffs(6, 5)
                        .addBox(-1.1F, -0.95F, -0.75F, 2.2F, 1.9F, 1.5F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.3F, 2.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.bodyFront.xRot = 0.0F;
        this.head.xRot = 0.0F;
        this.bodyMid.xRot = 0.0F;
        this.bodyBack.xRot = 0.0F;
        this.tailTip.xRot = 0.0F;

        float speed = entity.isInWaterOrBubble() ? 0.35F : 0.22F;
        float wave = Mth.sin(ageInTicks * speed);
        float follow = Mth.sin(ageInTicks * speed - 0.7F);
        float amount = 0.035F + Mth.clamp(limbSwingAmount, 0.0F, 0.25F) * 0.08F;

        this.bodyFront.xRot = wave * amount;
        this.head.xRot = wave * amount * 0.8F;
        this.bodyMid.xRot = follow * amount * 0.35F;
        this.bodyBack.xRot = -wave * amount * 0.55F;
        this.tailTip.xRot = -follow * amount * 0.75F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.maggotRoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
