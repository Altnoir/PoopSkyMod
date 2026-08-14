package com.altnoir.poopsky.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

public final class GuiQuadRenderState implements GuiElementRenderState {
    private final RenderPipeline pipeline;
    private final TextureSetup textureSetup;
    private final float[] positions;
    private final float[] uvs;
    private final float z;
    private final int color;

    public GuiQuadRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose,
                              float x0, float y0, float x1, float y1,
                              float u0, float v0, float u1, float v1, float z, int color) {
        this(pipeline, textureSetup, pose,
                x0, y0, x0, y1, x1, y1, x1, y0,
                u0, v0, u0, v1, u1, v1, u1, v0, z, color);
    }

    public GuiQuadRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose,
                              float x0, float y0, float x1, float y1,
                              float x2, float y2, float x3, float y3,
                              float u0, float v0, float u1, float v1,
                              float u2, float v2, float u3, float v3, float z, int color) {
        this.pipeline = pipeline;
        this.textureSetup = textureSetup;
        this.positions = new float[]{x0, y0, x1, y1, x2, y2, x3, y3};
        Vector2f point = new Vector2f();
        for (int index = 0; index < 4; index++) {
            pose.transformPosition(this.positions[index * 2], this.positions[index * 2 + 1], point);
            this.positions[index * 2] = point.x;
            this.positions[index * 2 + 1] = point.y;
        }
        this.uvs = new float[]{u0, v0, u1, v1, u2, v2, u3, v3};
        this.z = z;
        this.color = color;
    }

    @Override
    public void buildVertices(VertexConsumer consumer) {
        for (int index = 0; index < 4; index++) {
            consumer.addVertex(this.positions[index * 2], this.positions[index * 2 + 1], this.z)
                    .setUv(this.uvs[index * 2], this.uvs[index * 2 + 1])
                    .setColor(this.color);
        }
    }

    @Override
    public RenderPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public TextureSetup textureSetup() {
        return this.textureSetup;
    }

    @Override
    public ScreenRectangle scissorArea() {
        return null;
    }

    @Override
    public ScreenRectangle bounds() {
        return null;
    }
}
