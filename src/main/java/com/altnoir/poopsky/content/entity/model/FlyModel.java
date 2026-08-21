package com.altnoir.poopsky.content.entity.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.render.FlyRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class FlyModel extends EntityModel<FlyRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(PoopSky.loc("fly"), "main");
    public static final ModelLayerLocation MAGGOT_LAYER_LOCATION = new ModelLayerLocation(PoopSky.loc("maggot"), "main");

    private final ModelPart bone;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLeg;
    private final ModelPart midLeg;
    private final ModelPart backLeg;
    private ModelPart maggot;
    public FlyModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
        this.body = this.bone.getChild("body");
        this.rightWing = this.body.getChild("rightwing_bone");
        this.leftWing = this.body.getChild("leftwing_bone");
        this.frontLeg = this.body.getChild("leg_front");
        this.midLeg = this.body.getChild("leg_mid");
        this.backLeg = this.body.getChild("leg_back");
        this.maggot = null;
    }

    public FlyModel(ModelPart root, ModelPart maggot) {
        this(root);
        this.maggot = maggot;
    }

    public static LayerDefinition createMaggotBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot().addOrReplaceChild("maggot", CubeListBuilder.create(), PartPose.offset(0, 20, 0));
        float[][] boxes = {{-1.5F,0,-3.5F,3,2,2},{-2,0,-1.5F,4,3,2},{-3,0,1,6,4,3},{-1.5F,0,4,3,3,3},{-1,0,7,2,2,3},{-1,0,9.5F,2,1,2},{-.5F,0,11.5F,1,1,2}};
        for (int i=0;i<boxes.length;i++) { float[] b=boxes[i]; root.addOrReplaceChild("segment"+i, CubeListBuilder.create().texOffs(0, i*4).addBox(b[0],b[1],b[2],b[3],b[4],b[5]), PartPose.ZERO); }
        return LayerDefinition.create(mesh, 32, 32);
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

    public void setupAnim(FlyRenderState state) {
        this.resetPose();
        if (state.isBaby && this.maggot != null) {
            this.maggot.xRot = Mth.sin(state.ageInTicks * 0.3F) * 0.08F;
            return;
        }
        if (state.isStationaryOnGround) {
            this.rightWing.yRot = -0.2618F;
            this.rightWing.zRot = 0.0F;
            this.leftWing.xRot = 0.0F;
            this.leftWing.yRot = 0.2618F;
            this.leftWing.zRot = 0.0F;
            this.frontLeg.xRot = 0.0F;
            this.midLeg.xRot = 0.0F;
            this.backLeg.xRot = 0.0F;
        } else {
            float f = state.ageInTicks * 120.32113F * (float) (Math.PI / 180.0);
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
}
