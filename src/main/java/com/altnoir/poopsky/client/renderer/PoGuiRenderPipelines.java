package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.PoopSky;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.Optional;

public final class PoGuiRenderPipelines {
    public static final RenderPipeline INTRO_DEPTH_CLEAR = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(PoopSky.loc("pipeline/intro_depth_clear"))
            .withColorTargetState(new ColorTargetState(Optional.empty(), ColorTargetState.WRITE_NONE))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
            .build();
    public static final RenderPipeline INTRO_GLYPH_MASK = RenderPipeline.builder(RenderPipelines.GUI_TEXT_SNIPPET)
            .withLocation(PoopSky.loc("pipeline/intro_glyph_mask"))
            .withVertexShader("core/rendertype_text")
            .withFragmentShader("core/rendertype_text")
            .withSampler("Sampler0")
            .withSampler("Sampler2")
            .withColorTargetState(new ColorTargetState(Optional.empty(), ColorTargetState.WRITE_NONE))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
            .build();
    public static final RenderPipeline INTRO_GLYPH_INTENSITY_MASK = RenderPipeline.builder(RenderPipelines.GUI_TEXT_SNIPPET)
            .withLocation(PoopSky.loc("pipeline/intro_glyph_intensity_mask"))
            .withVertexShader("core/rendertype_text_intensity")
            .withFragmentShader("core/rendertype_text_intensity")
            .withSampler("Sampler0")
            .withSampler("Sampler2")
            .withColorTargetState(new ColorTargetState(Optional.empty(), ColorTargetState.WRITE_NONE))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
            .build();
    public static final RenderPipeline INTRO_DEPTH_TEXTURE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(PoopSky.loc("pipeline/intro_depth_texture"))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
            .build();

    private PoGuiRenderPipelines() {
    }

    public static void register(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(INTRO_DEPTH_CLEAR);
        event.registerPipeline(INTRO_GLYPH_MASK);
        event.registerPipeline(INTRO_GLYPH_INTENSITY_MASK);
        event.registerPipeline(INTRO_DEPTH_TEXTURE);
    }
}
