package com.altnoir.poopsky.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.Mth;

public final class PoRenderCompat {
    private PoRenderCompat() {}
    public static BufferBuilder begin(VertexFormat.Mode mode, VertexFormat format) {
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(mode, format);
        return builder;
    }
    public static int colorFromFloat(float alpha, float red, float green, float blue) {
        return channel(alpha) << 24 | channel(red) << 16 | channel(green) << 8 | channel(blue);
    }
    private static int channel(float value) { return Mth.clamp((int) (value * 255.0F), 0, 255); }
}
