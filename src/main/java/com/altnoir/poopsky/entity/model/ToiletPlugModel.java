package com.altnoir.poopsky.entity.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.ToiletPlugEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ToiletPlugModel extends EntityModel<ToiletPlugEntity> {
    public static final EntityModelLayer TOILET_PLUG_LAYER = new EntityModelLayer(Identifier.of(PoopSky.MOD_ID, "toilet_plug"), "main");
    private final ModelPart toilet_plug;

    public ToiletPlugModel() {
        super(RenderLayer::getEntityTranslucent);
        this.toilet_plug = getTexturedModelData().createModel();
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData toilet_plug = modelPartData.addChild("toilet_plug", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -3.0F, -8.0F, 0.0F, 0.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = toilet_plug.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 2.0F, new Dilation(0.05F))
                .uv(0, 17).cuboid(-3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 2.0F, new Dilation(0.0F))
                .uv(1, 0).cuboid(-1.0F, -4.0F, -10.0F, 2.0F, 2.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r2 = toilet_plug.addChild("cube_r2", ModelPartBuilder.create().uv(16, 23).cuboid(-2.0F, -2.5981F, -0.5F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, -6.0F, -2.618F, 0.0F, -1.5708F));

        ModelPartData cube_r3 = toilet_plug.addChild("cube_r3", ModelPartBuilder.create().uv(16, 23).cuboid(-2.0F, -2.5981F, -0.5F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, -6.0F, -2.618F, 0.0F, 0.0F));

        ModelPartData cube_r4 = toilet_plug.addChild("cube_r4", ModelPartBuilder.create().uv(16, 23).cuboid(-2.0F, -2.5981F, -0.5F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, -6.0F, -2.618F, 0.0F, 1.5708F));

        ModelPartData cube_r5 = toilet_plug.addChild("cube_r5", ModelPartBuilder.create().uv(16, 17).cuboid(-2.0F, -6.0F, 7.0F, 4.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 2.75F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r6 = toilet_plug.addChild("cube_r6", ModelPartBuilder.create().uv(16, 23).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.0F, -6.0F, -2.618F, 0.0F, 3.1416F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(ToiletPlugEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        toilet_plug.render(matrices, vertices, light, overlay, color);
    }
    public ModelPart getToilet_plug() {
        return toilet_plug;
    }
}
