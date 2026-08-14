package com.altnoir.poopsky.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

public record IntroGlyphRenderState(Matrix3x2f pose, TextRenderable glyph, TextureSetup textureSetup) implements GuiElementRenderState {
    private static final int FULL_BRIGHT = 0xF000F0;

    public IntroGlyphRenderState(Matrix3x2f pose, TextRenderable glyph) {
        this(new Matrix3x2f(pose), glyph, TextureSetup.singleTextureWithLightmap(
                glyph.textureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST)));
    }

    @Override
    public void buildVertices(VertexConsumer consumer) {
        this.glyph.render(new Matrix4f().mul(this.pose), consumer, FULL_BRIGHT, true);
    }

    @Override
    public RenderPipeline pipeline() {
        return this.glyph.guiPipeline();
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
