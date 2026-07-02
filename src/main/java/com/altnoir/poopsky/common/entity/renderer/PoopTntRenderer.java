package com.altnoir.poopsky.common.entity.renderer;

import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.common.entity.p.PoopTntEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PoopTntRenderer extends EntityRenderer<PoopTntEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public PoopTntRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(PoopTntEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5F, 0.0F);
        int fuse = p_entity.getFuse();
        if ((float) fuse - partialTick + 1.0F < 10.0F) {
            float f = 1.0F - ((float) fuse - partialTick + 1.0F) / 10.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f *= f;
            f *= f;
            float f1 = 1.0F + f * 0.3F;
            poseStack.scale(f1, f1, f1);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer,
                PBlocks.POOP_TNT.get().defaultBlockState(), poseStack, bufferSource, packedLight, fuse / 5 % 2 == 0);
        poseStack.popPose();
        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PoopTntEntity poopTntEntity) {
        return MissingTextureAtlasSprite.getLocation();
    }

    @Override
    public boolean shouldRender(PoopTntEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
