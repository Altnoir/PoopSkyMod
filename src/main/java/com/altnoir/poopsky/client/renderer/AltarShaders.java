package com.altnoir.poopsky.client.renderer;

import com.altnoir.poopsky.PoopSky;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.io.UncheckedIOException;

public final class AltarShaders {
    private static ShaderInstance beam;

    private AltarShaders() {
    }

    public static ShaderInstance beam() {
        return beam;
    }

    public static void register(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), PoopSky.loc("altar_beam"),
                    DefaultVertexFormat.POSITION_TEX_COLOR), shader -> beam = shader);
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to load altar beam shader", exception);
        }
    }
}
