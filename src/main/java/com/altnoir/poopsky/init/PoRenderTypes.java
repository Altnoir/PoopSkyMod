package com.altnoir.poopsky.init;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public final class PoRenderTypes {
    private PoRenderTypes() {
    }

    public static RenderType chunkLoaderGlow() {
        return RenderTypes.lightning();
    }
}
