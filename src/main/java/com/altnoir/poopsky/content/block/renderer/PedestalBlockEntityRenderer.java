package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.content.block.AltarRitualAnimation;
import com.altnoir.poopsky.content.block.entity.OminousAltarBlockEntity;
import com.altnoir.poopsky.content.block.entity.PedestalBlockEntity;
import com.altnoir.poopsky.impl.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PedestalBlockEntityRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    private final ItemRenderer itemRenderer;

    public PedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public AABB getRenderBoundingBox(PedestalBlockEntity pedestal) {
        return new AABB(pedestal.getBlockPos()).inflate(5.5D, 0.0D, 5.5D).expandTowards(0, 1, 0);
    }

    @Override
    public void render(PedestalBlockEntity pedestal, float partialTick, PoseStack poses,
                       MultiBufferSource buffers, int light, int overlay) {
        Level level = pedestal.getLevel();
        ItemStack stack = pedestal.getItem();
        if (level == null || stack.isEmpty()) return;
        double time = level.getGameTime() + (double) partialTick;
        Vec3 position = new Vec3(0.5D, AltarRitualAnimation.pedestalItemY(time), 0.5D);
        for (BlockPos offset : OminousAltarBlockEntity.PEDESTAL_OFFSETS) {
            BlockPos pos = pedestal.getBlockPos().offset(offset);
            if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof OminousAltarBlockEntity altar
                    && altar.getClientProgress() > 0 && altar.isClientPedestal(pedestal.getBlockPos())) {
                position = AltarRitualAnimation.orbitPosition(pedestal.getBlockPos().subtract(pos), time,
                        altar.getClientProgress(partialTick)).add(offset.getX(), 0, offset.getZ());
                break;
            }
        }
        var model = itemRenderer.getModel(stack, level, null, 0);
        var offset = ClientUtil.itemTransformOffset(model, ItemDisplayContext.GROUND);
        poses.pushPose();
        poses.translate(position.x, position.y, position.z);
        poses.mulPose(Axis.YP.rotation((float) (time / 20.0D % Mth.TWO_PI)));
        poses.translate(-offset.x(), -offset.y(), -offset.z());
        itemRenderer.render(stack, ItemDisplayContext.GROUND, false, poses, buffers, light, OverlayTexture.NO_OVERLAY, model);
        poses.popPose();
    }
}
