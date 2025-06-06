package com.altnoir.poopsky.entity.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ToiletPlugModel<T extends ToiletPlugEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "toilet_plug"), "main");
    private final ModelPart plug;

    public ToiletPlugModel(ModelPart root) {
        this.plug = root.getChild("toilet_plug");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition plug = partdefinition.addOrReplaceChild("toilet_plug", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -3.0F, -8.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = plug.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(16, 17).addBox(-2.0F, -6.0F, 7.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.75F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r2 = plug.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(16, 23).addBox(-2.0F, -2.5981F, -0.5F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -6.0F, -2.618F, 0.0F, -1.5708F));

        PartDefinition cube_r3 = plug.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(16, 23).addBox(-2.0F, -2.5981F, -0.5F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -6.0F, -2.618F, 0.0F, 0.0F));

        PartDefinition cube_r4 = plug.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(16, 23).addBox(-2.0F, -2.5981F, -0.5F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -6.0F, -2.618F, 0.0F, 1.5708F));

        PartDefinition cube_r5 = plug.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(16, 23).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -6.0F, -2.618F, 0.0F, 3.1416F));

        PartDefinition cube_r6 = plug.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.05F))
                .texOffs(0, 17).addBox(-3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(1, 0).addBox(-1.0F, -4.0F, -10.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
    @Override
    public void setupAnim(@NotNull ToiletPlugEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        plug.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public ModelPart getPlug() {return plug;}
}
