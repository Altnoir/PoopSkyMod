package com.altnoir.poopsky.init;

import com.altnoir.poopsky.client.renderer.AltarShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class PoRenderTypes {
    private static final RenderType CHUNK_LOADER_GLOW = RenderType.create(
            "poopsky_chunk_loader_glow",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_LIGHTNING_SHADER)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
                    .createCompositeState(false)
    );

    private static final RenderType ALTAR_BEAM = RenderType.create(
            "poopsky_altar_beam",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            8192,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(AltarShaders::beam))
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)
    );

    private PoRenderTypes() {
    }

    public static RenderType chunkLoaderGlow() {
        return CHUNK_LOADER_GLOW;
    }

    public static RenderType altarBeam() {
        return ALTAR_BEAM;
    }
}
