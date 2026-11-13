package com.altnoir.poopsky.content.block.renderer;

import com.altnoir.poopsky.content.block.entity.GlassJarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Map;
import java.util.WeakHashMap;

public class GlassJarBlockEntityRenderer implements BlockEntityRenderer<GlassJarBlockEntity> {
    private static final float MIN = 3.5F / 16.0F;
    private static final float MAX = 12.5F / 16.0F;
    private static final float BOTTOM = 0.25F / 16.0F;
    private static final float FULL_TOP = 13.5F / 16.0F;
    private static final Map<TextureAtlasSprite, Integer> SPRITE_COLORS = new WeakHashMap<>();

    public GlassJarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void renderFluid(FluidStack fluid, PoseStack poses, MultiBufferSource buffers, int light) {
        if (fluid.isEmpty()) return;
        IClientFluidTypeExtensions extension = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = getFluidSprite(fluid, extension);
        if (sprite == null) return;
        int color = extension.getTintColor(fluid);
        float fill = Mth.clamp((float) fluid.getAmount() / GlassJarBlockEntity.CAPACITY, 0.0F, 1.0F);
        float top = Mth.lerp(fill, BOTTOM, FULL_TOP);
        float textureWidth = sprite.getU1() - sprite.getU0();
        float textureHeight = sprite.getV1() - sprite.getV0();
        float uMin = sprite.getU0() + MIN * textureWidth;
        float uMax = sprite.getU0() + MAX * textureWidth;
        float vMin = sprite.getV0() + MIN * textureHeight;
        float vMax = sprite.getV0() + MAX * textureHeight;
        float vBottom = sprite.getV1() - BOTTOM * textureHeight;
        float vTop = sprite.getV1() - top * textureHeight;
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose pose = poses.last();

        fluidQuad(consumer, pose, sprite, color, light,
                MIN, top, MIN, MIN, top, MAX, MAX, top, MAX, MAX, top, MIN,
                uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax, 0, 1, 0);
        fluidQuad(consumer, pose, sprite, color, light,
                MIN, BOTTOM, MIN, MAX, BOTTOM, MIN, MAX, BOTTOM, MAX, MIN, BOTTOM, MAX,
                uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax, 0, -1, 0);
        fluidQuad(consumer, pose, sprite, color, light,
                MIN, BOTTOM, MIN, MIN, top, MIN, MAX, top, MIN, MAX, BOTTOM, MIN,
                uMin, vBottom, uMin, vTop, uMax, vTop, uMax, vBottom, 0, 0, -1);
        fluidQuad(consumer, pose, sprite, color, light,
                MAX, BOTTOM, MAX, MAX, top, MAX, MIN, top, MAX, MIN, BOTTOM, MAX,
                uMin, vBottom, uMin, vTop, uMax, vTop, uMax, vBottom, 0, 0, 1);
        fluidQuad(consumer, pose, sprite, color, light,
                MIN, BOTTOM, MAX, MIN, top, MAX, MIN, top, MIN, MIN, BOTTOM, MIN,
                uMin, vBottom, uMin, vTop, uMax, vTop, uMax, vBottom, -1, 0, 0);
        fluidQuad(consumer, pose, sprite, color, light,
                MAX, BOTTOM, MIN, MAX, top, MIN, MAX, top, MAX, MAX, BOTTOM, MAX,
                uMin, vBottom, uMin, vTop, uMax, vTop, uMax, vBottom, 1, 0, 0);
    }

    static int getFluidColor(FluidStack fluid) {
        var extension = IClientFluidTypeExtensions.of(fluid.getFluid());
        int tint = extension.getTintColor(fluid);
        var sprite = getFluidSprite(fluid, extension);
        return sprite == null ? tint : FastColor.ARGB32.multiply(tint,
                SPRITE_COLORS.computeIfAbsent(sprite, GlassJarBlockEntityRenderer::averageSpriteColor));
    }

    private static TextureAtlasSprite getFluidSprite(FluidStack fluid, IClientFluidTypeExtensions extension) {
        var texture = extension.getStillTexture(fluid);
        return texture == null ? null : Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
    }

    private static int averageSpriteColor(TextureAtlasSprite sprite) {
        var contents = sprite.contents();
        int frame = contents.getUniqueFrames().findFirst().orElse(0);
        int alpha = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        int width = Math.min(16, contents.width());
        int height = Math.min(16, contents.height());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = sprite.getPixelRGBA(frame, x * contents.width() / width, y * contents.height() / height);
                int weight = FastColor.ABGR32.alpha(pixel);
                alpha += weight;
                red += FastColor.ABGR32.red(pixel) * weight;
                green += FastColor.ABGR32.green(pixel) * weight;
                blue += FastColor.ABGR32.blue(pixel) * weight;
            }
        }
        return alpha == 0 ? -1 : FastColor.ARGB32.color(255, red / alpha, green / alpha, blue / alpha);
    }

    private static void fluidQuad(VertexConsumer consumer, PoseStack.Pose pose, TextureAtlasSprite sprite, int color, int light,
                                  float x1, float y1, float z1, float x2, float y2, float z2,
                                  float x3, float y3, float z3, float x4, float y4, float z4,
                                  float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4,
                                  float nx, float ny, float nz) {
        vertex(consumer, pose, x1, y1, z1, u1, v1, color, light, nx, ny, nz);
        vertex(consumer, pose, x2, y2, z2, u2, v2, color, light, nx, ny, nz);
        vertex(consumer, pose, x3, y3, z3, u3, v3, color, light, nx, ny, nz);
        vertex(consumer, pose, x4, y4, z4, u4, v4, color, light, nx, ny, nz);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z,
                               float u, float v, int color, int light, float nx, float ny, float nz) {
        consumer.addVertex(pose, x, y, z)
                .setColor(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color),
                        FastColor.ARGB32.blue(color), FastColor.ARGB32.alpha(color))
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }

    @Override
    public void render(GlassJarBlockEntity jar, float partialTick, PoseStack poses, MultiBufferSource buffers,
                       int light, int overlay) {
        renderFluid(jar.getTank().getFluid(), poses, buffers, light);
    }
}
