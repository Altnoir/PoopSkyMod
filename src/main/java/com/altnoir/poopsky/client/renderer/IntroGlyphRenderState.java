package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.PoopSky;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.neoforged.neoforge.client.event.ConfigureMainRenderTargetEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.stencil.StencilOperation;
import net.neoforged.neoforge.client.stencil.StencilPerFaceTest;
import net.neoforged.neoforge.client.stencil.StencilTest;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

public record IntroGlyphRenderState(Matrix3x2f pose, TextRenderable glyph, TextureSetup textureSetup,
                                    RenderPipeline pipeline) implements GuiElementRenderState {
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final StencilTest WRITE_MASK = stencil(CompareOp.ALWAYS_PASS, StencilOperation.REPLACE, 0xFF, 1);
    private static final StencilTest TEST_MASK = stencil(CompareOp.EQUAL, StencilOperation.KEEP, 0, 1);

    private static final RenderPipeline TEXT_MASK = RenderPipelines.GUI_TEXT.toBuilder()
            .withLocation(PoopSky.loc("pipeline/intro_text_mask"))
            .withStencilTest(WRITE_MASK)
            .build();

    private static final RenderPipeline TEXT_INTENSITY_MASK = RenderPipelines.GUI_TEXT_INTENSITY.toBuilder()
            .withLocation(PoopSky.loc("pipeline/intro_text_intensity_mask"))
            .withStencilTest(WRITE_MASK)
            .build();

    public static final RenderPipeline TEXTURE_MASK = RenderPipelines.GUI_TEXTURED.toBuilder()
            .withLocation(PoopSky.loc("pipeline/intro_texture_mask"))
            .withStencilTest(TEST_MASK)
            .build();

    public IntroGlyphRenderState(Matrix3x2f pose, TextRenderable glyph) {
        this(pose, glyph, false);
    }

    public IntroGlyphRenderState(Matrix3x2f pose, TextRenderable glyph, boolean mask) {
        this(new Matrix3x2f(pose), glyph, TextureSetup.singleTextureWithLightmap(
                        glyph.textureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST)),
                mask ? maskPipeline(glyph.guiPipeline()) : glyph.guiPipeline());
    }

    public static void configureMainRenderTarget(ConfigureMainRenderTargetEvent event) {
        event.enableStencil();
    }

    public static void registerPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(TEXT_MASK);
        event.registerPipeline(TEXT_INTENSITY_MASK);
        event.registerPipeline(TEXTURE_MASK);
    }

    private static RenderPipeline maskPipeline(RenderPipeline pipeline) {
        return pipeline == RenderPipelines.GUI_TEXT_INTENSITY ? TEXT_INTENSITY_MASK : TEXT_MASK;
    }

    private static StencilTest stencil(CompareOp compare, StencilOperation pass, int writeMask, int reference) {
        StencilPerFaceTest test = new StencilPerFaceTest(
                StencilOperation.KEEP, StencilOperation.KEEP, pass, compare);
        return new StencilTest(test, 0xFF, writeMask, reference);
    }

    @Override
    public void buildVertices(VertexConsumer consumer) {
        this.glyph.render(new Matrix4f().mul(this.pose), consumer, FULL_BRIGHT, true);
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
