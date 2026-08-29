package com.altnoir.poopsky.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.jspecify.annotations.Nullable;

public final class ArcadeRenderTargetOverride {
    private static @Nullable RenderTarget target;

    private ArcadeRenderTargetOverride() {
    }

    public static @Nullable RenderTarget get() {
        return target;
    }

    public static void runWith(RenderTarget renderTarget, Runnable action) {
        RenderTarget previous = target;
        target = renderTarget;
        try {
            action.run();
        } finally {
            target = previous;
        }
    }
}
