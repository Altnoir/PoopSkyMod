package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.model.ToiletPlugModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class ToiletPlugItemRenderer implements NoDataSpecialModelRenderer {
    private static final Identifier TEXTURE = PoopSky.loc("textures/entity/toilet_plug.png");
    private final ModelPart plug;

    public ToiletPlugItemRenderer(ModelPart root) {
        this.plug = root.getChild("toilet_plug");
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords,
                       int overlayCoords, boolean hasFoil, int outlineColor) {
        submitNodeCollector.submitModelPart(plug, poseStack, RenderTypes.entityCutout(TEXTURE), lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        plug.getExtentsForGui(new PoseStack(), output);
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public ToiletPlugItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new ToiletPlugItemRenderer(context.entityModelSet().bakeLayer(ToiletPlugModel.LAYER_LOCATION));
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
