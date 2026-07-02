package com.altnoir.poopsky.entity.renderer;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.model.MaggotModel;
import com.altnoir.poopsky.entity.p.MaggotEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class MaggotRenderer extends MobRenderer<MaggotEntity, MaggotModel<MaggotEntity>> {
    public MaggotRenderer(EntityRendererProvider.Context context) {
        super(context, new MaggotModel<>(context.bakeLayer(MaggotModel.LAYER_LOCATION)), 0.F);
    }

    @Override
    public void render(MaggotEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        if (this.isOnPoopBlock(entity)) {
            poseStack.translate(0.0F, 0.125F, 0.0F);
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MaggotEntity entity) {
        return PoopSky.loc("textures/entity/maggot.png");
    }

    private boolean isOnPoopBlock(MaggotEntity entity) {
        BlockPos pos = entity.blockPosition();
        return isPoopBlock(entity.level().getBlockState(pos))
                || isPoopBlock(entity.level().getBlockState(pos.below()));
    }

    private boolean isPoopBlock(BlockState state) {
        return state.is(PTags.Blocks.POOP_BLOCKS) || state.is(PTags.Blocks.POOP_BLOCK);
    }
}
