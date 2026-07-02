package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.entity.p.ToiletPlugEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ToiletPlugItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final ToiletPlugItemRenderer INSTANCE = new ToiletPlugItemRenderer(
            Minecraft.getInstance().getBlockEntityRenderDispatcher()
    );

    private static final ResourceLocation TEXTURE = PoopSky.loc("textures/entity/toilet_plug.png");

    private final EntityModel<ToiletPlugEntity> model;

    private ToiletPlugItemRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher, Minecraft.getInstance().getEntityModels());
        this.model = new ToiletPlugModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ToiletPlugModel.LAYER_LOCATION));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.7F, 0.5F);
        poseStack.scale(0.9F, 0.9F, 0.9F);
        poseStack.scale(1.0F, -1.0F, -1.0F);

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(buffer, this.model.renderType(TEXTURE), false, stack.hasFoil());
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}
