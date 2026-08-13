package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.PoopSky;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

final class PoolimeOuterLayer extends RenderLayer<SlimeRenderState, SlimeModel> {
    private static final Identifier TEXTURE = PoopSky.loc("textures/entity/poolime.png");
    private final SlimeModel model;

    PoolimeOuterLayer(RenderLayerParent<SlimeRenderState, SlimeModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new SlimeModel(modelSet.bakeLayer(ModelLayers.SLIME_OUTER));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light,
                       SlimeRenderState state, float yRot, float xRot) {
        boolean glowingOutline = state.appearsGlowing() && state.isInvisible;
        if (state.isInvisible && !glowingOutline) {
            return;
        }

        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        collector.order(1).submitModel(
                model,
                state,
                poseStack,
                glowingOutline ? RenderTypes.outline(TEXTURE) : RenderTypes.entityTranslucent(TEXTURE),
                light,
                overlay,
                state.outlineColor,
                null);
    }
}
