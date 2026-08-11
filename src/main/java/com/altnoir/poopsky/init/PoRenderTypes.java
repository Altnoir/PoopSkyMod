package com.altnoir.poopsky.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.rendertype.RenderType;

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

    private PoRenderTypes() {
    }

    public static RenderType chunkLoaderGlow() {
        return CHUNK_LOADER_GLOW;
    }
}
