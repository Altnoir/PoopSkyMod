package com.altnoir.poopsky.content.entity.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
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
    public static final ModelLayerLocation MAGGOT_LAYER_LOCATION = new ModelLayerLocation(PoopSky.loc("maggot"), "main");

    private static final int MAGGOT_SEGMENT_COUNT = 7;

    private final ModelPart bone;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLeg;
    private final ModelPart midLeg;
    private final ModelPart backLeg;
    private final ModelPart maggot;
    private final ModelPart[] maggotSegments = new ModelPart[MAGGOT_SEGMENT_COUNT];

    public FlyModel(ModelPart root, ModelPart maggotRoot) {
        super(false, 24.0F, 0.0F, 2.0F, 1.5F, 12.0F);
        this.bone = root.getChild("bone");
        ModelPart body = this.bone.getChild("body");
        this.rightWing = body.getChild("rightwing_bone");
        this.leftWing = body.getChild("leftwing_bone");
        this.frontLeg = body.getChild("leg_front");
        this.midLeg = body.getChild("leg_mid");
        this.backLeg = body.getChild("leg_back");
        this.maggot = maggotRoot;
        for (int i = 0; i < MAGGOT_SEGMENT_COUNT; i++) {
            this.maggotSegments[i] = this.maggot.getChild("segment" + i);
        }
    }

    public static LayerDefinition createFlyBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = addPart(
                partdefinition,
                "bone",
                CubeListBuilder.create(),
                PartPose.offset(0.5F, 19.0F, 0.0F)
        );
        PartDefinition body = addPart(bone, "body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.5F, -1.0F, -7.0F, 2.0F, 4.0F, 2.0F)
                        .texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 6.0F, 6.0F, 10.0F),
                PartPose.ZERO
        );
        addPart(body, "rightwing_bone",
                CubeListBuilder.create().texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F),
                PartPose.offsetAndRotation(-1.5F, -4.0F, -3.0F, 0.2618F, -0.2618F, 0.0F)
        );
        addPart(body, "leftwing_bone",
                CubeListBuilder.create().texOffs(9, 24).addBox(-1.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F),
                PartPose.offsetAndRotation(1.5F, -4.0F, -3.0F, 0.2618F, 0.2618F, 0.0F)
        );
        addPart(body, "leg_front",
                CubeListBuilder.create().addBox("leg_front", -5.0F, -1.5F, 0.0F, 7, 3, 0, 22, 0),
                PartPose.offset(1.5F, 3.0F, -2.0F)
        );
        addPart(body, "leg_mid",
                CubeListBuilder.create().addBox("leg_mid", -5.0F, -1.5F, 0.0F, 7, 3, 0, 22, 2),
                PartPose.offset(1.5F, 3.0F, 0.0F)
        );
        addPart(body, "leg_back",
                CubeListBuilder.create().addBox("leg_back", -5.0F, -1.5F, 0.0F, 7, 3, 0, 22, 4),
                PartPose.offset(1.5F, 3.0F, 2.0F)
        );

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createMaggotBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition maggot = addPart(partdefinition, "maggot", CubeListBuilder.create(), PartPose.ZERO);
        addMaggotSegment(maggot, 0, -1.5F, 0.0F, -1.0F, 3.0F, 2.0F, 2.0F, 0, 0, 0.0F, 22.0F, -3.5F);
        addMaggotSegment(maggot, 1, -2.0F, 0.0F, -1.0F, 4.0F, 3.0F, 2.0F, 0, 4, 0.0F, 21.0F, -1.5F);
        addMaggotSegment(maggot, 2, -3.0F, 0.0F, -1.5F, 6.0F, 4.0F, 3.0F, 0, 9, 0.0F, 20.0F, 1.0F);
        addMaggotSegment(maggot, 3, -1.5F, 0.0F, -1.5F, 3.0F, 3.0F, 3.0F, 0, 16, 0.0F, 21.0F, 4.0F);
        addMaggotSegment(maggot, 4, -1.0F, 0.0F, -1.5F, 2.0F, 2.0F, 3.0F, 0, 22, 0.0F, 22.0F, 7.0F);
        addMaggotSegment(maggot, 5, -1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, 11, 0, 0.0F, 23.0F, 9.5F);
        addMaggotSegment(maggot, 6, -0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, 13, 4, 0.0F, 23.0F, 11.5F);
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    private static PartDefinition addPart(
            PartDefinition parent,
            String name,
            CubeListBuilder cubes,
            PartPose pose
    ) {
        return parent.addOrReplaceChild(name, cubes, pose);
    }

    private static void addMaggotSegment(
            PartDefinition maggot,
            int index,
            float x,
            float y,
            float z,
            float width,
            float height,
            float depth,
            int texU,
            int texV,
            float offsetX,
            float offsetY,
            float offsetZ
    ) {
        addPart(
                maggot,
                "segment" + index,
                CubeListBuilder.create().texOffs(texU, texV).addBox(x, y, z, width, height, depth),
                PartPose.offset(offsetX, offsetY, offsetZ)
        );
    }

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isBaby()) {
            this.setupMaggotAnim(ageInTicks);
            return;
        }

        this.bone.xRot = 0.0F;
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
    }

    private void setupMaggotAnim(float ageInTicks) {
        for (int i = 0; i < MAGGOT_SEGMENT_COUNT; i++) {
            ModelPart part = this.maggotSegments[i];
            float wave = ageInTicks * 0.9F + i * 0.15F * (float) Math.PI;
            part.yRot = Mth.cos(wave) * (float) Math.PI * 0.05F * (float) (1 + Math.abs(i - 2));
            part.x = Mth.sin(wave) * (float) Math.PI * 0.2F * (float) Math.abs(i - 2);
        }
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return this.young ? ImmutableList.of(this.maggot) : ImmutableList.of(this.bone);
    }
}