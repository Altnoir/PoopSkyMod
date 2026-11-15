package com.altnoir.poopsky.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class SnailShellArmorModel extends HumanoidModel<LivingEntity> {
    public SnailShellArmorModel() {
        super(createRoot());
    }

    private static ModelPart createRoot() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0);
        PartDefinition root = mesh.getRoot();
        for (String part : new String[]{"head", "hat", "body", "right_arm", "left_arm", "right_leg", "left_leg"}) {
            root.addOrReplaceChild(part, CubeListBuilder.create(), PartPose.ZERO);
        }
        root.getChild("body").addOrReplaceChild("shell", CubeListBuilder.create()
                        .texOffs(0, 14).addBox(-3.5F, -8, -4, 7, 8, 8)
                        .texOffs(30, 25).addBox(-3.5F, -1.6F, -1, 7, 2, 6, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0, 6, 2.1F, -(float) Math.PI / 2, 0, 0));
        return LayerDefinition.create(mesh, 64, 64).bakeRoot();
    }
}
