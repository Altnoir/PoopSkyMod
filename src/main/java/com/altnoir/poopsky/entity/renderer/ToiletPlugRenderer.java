package com.altnoir.poopsky.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.ToiletPlugEntity;
import com.altnoir.poopsky.entity.model.ToiletPlugModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class ToiletPlugRenderer extends EntityRenderer<ToiletPlugEntity> {
    private final ToiletPlugModel toiletPlugModel = new ToiletPlugModel();
    public ToiletPlugRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
    @Override
    public Identifier getTexture(ToiletPlugEntity entity) {
        return Identifier.of(PoopSky.MOD_ID, "textures/entity/toilet_plug.png");
    }
    @Override
    public void render(ToiletPlugEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        RenderLayer renderlayer =toiletPlugModel.getLayer(getTexture(entity));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderlayer);

        float y = -entity.getYaw(tickDelta);
        float p = entity.getPitch(tickDelta);
        float h = entity.getFloatingValue(tickDelta);

        matrices.translate(0, h-1, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(y));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(p));
        toiletPlugModel.render(matrices,vertexConsumer,light, OverlayTexture.DEFAULT_UV);
        matrices.scale(1.0f, 1.0f, 1.0f); // 缩放
        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
