package com.altnoir.poopsky.content.entity.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ModelUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class FlyModel<T extends FlyEntity> extends AgeableListModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(PoopSky.loc("fly"), "main");

    private final ModelPart bone;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLeg;
    private final ModelPart midLeg;
    private final ModelPart backLeg;
    private float rollAmount;

    public FlyModel(ModelPart root) {
        super(false, 24.0F, 0.0F);
        this.bone = root.getChild("bone");
        this.body = this.bone.getChild("body");
        this.rightWing = this.body.getChild("rightwing_bone");
        this.leftWing = this.body.getChild("leftwing_bone");
        this.frontLeg = this.body.getChild("leg_front");
        this.midLeg = this.body.getChild("leg_mid");
        this.backLeg = this.body.getChild("leg_back");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.5F, 19.0F, 0.0F));

        PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.5F, -1.0F, -7.0F, 2.0F, 4.0F, 2.0F)
                        .texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 6.0F, 6.0F, 10.0F),
                PartPose.ZERO);

        body.addOrReplaceChild("rightwing_bone", CubeListBuilder.create()
                        .texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F),
                PartPose.offsetAndRotation(-1.5F, -4.0F, -3.0F, 0.2618F, -0.2618F, 0.0F));
        body.addOrReplaceChild("leftwing_bone", CubeListBuilder.create()
                        .texOffs(9, 24).addBox(-1.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F),
                PartPose.offsetAndRotation(1.5F, -4.0F, -3.0F, 0.2618F, 0.2618F, 0.0F));

        body.addOrReplaceChild("leg_front", CubeListBuilder.create()
                        .addBox("leg_front", -5.0F, -1.5F, 0.0F, 7, 3, 0, 22, 0),
                PartPose.offset(1.5F, 3.0F, -2.0F));
        body.addOrReplaceChild("leg_mid", CubeListBuilder.create()
                        .addBox("leg_mid", -5.0F, -1.5F, 0.0F, 7, 3, 0, 22, 2),
                PartPose.offset(1.5F, 3.0F, 0.0F));
        body.addOrReplaceChild("leg_back", CubeListBuilder.create()
                        .addBox("leg_back", -5.0F, -1.5F, 0.0F, 7, 3, 0, 22, 4),
                PartPose.offset(1.5F, 3.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.bone.xRot = 0.0F;
        this.rightWing.visible = !entity.isBaby();
        this.leftWing.visible = !entity.isBaby();
        this.frontLeg.visible = !entity.isBaby();
        this.midLeg.visible = !entity.isBaby();
        this.backLeg.visible = !entity.isBaby();
        this.body.xScale = 1.0F;
        this.body.yScale = 1.0F;
        this.body.zScale = 1.0F;
        if (entity.isBaby()) {
            this.body.xScale = 0.45F;
            this.body.yScale = 0.45F;
            this.body.zScale = 0.8F;
            this.bone.xRot = Mth.sin(ageInTicks * 0.35F) * 0.08F;
            return;
        }
        boolean flag = entity.onGround() && entity.getDeltaMovement().lengthSqr() < 1.0E-7;
        if (flag) {
            this.rightWing.yRot = -0.2618F;
            this.rightWing.zRot = 0.0F;
            this.leftWing.xRot = 0.0F;
            this.leftWing.yRot = 0.2618F;
            this.leftWing.zRot = 0.0F;
            this.frontLeg.xRot = 0.0F;
            this.midLeg.xRot = 0.0F;
            this.backLeg.xRot = 0.0F;
        } else {
            float f = ageInTicks * 120.32113F * (float) (Math.PI / 180.0);
            this.rightWing.yRot = 0.0F;
            this.rightWing.zRot = Mth.cos(f) * (float) Math.PI * 0.15F;
            this.leftWing.xRot = this.rightWing.xRot;
            this.leftWing.yRot = this.rightWing.yRot;
            this.leftWing.zRot = -this.rightWing.zRot;
            this.frontLeg.xRot = (float) (Math.PI / 4);
            this.midLeg.xRot = (float) (Math.PI / 4);
            this.backLeg.xRot = (float) (Math.PI / 4);
            this.bone.xRot = 0.0F;
            this.bone.yRot = 0.0F;
            this.bone.zRot = 0.0F;
        }

        if (this.rollAmount > 0.0F) {
            this.bone.xRot = ModelUtils.rotlerpRad(this.bone.xRot, 3.0915928F, this.rollAmount);
        }
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.bone);
    }
}
