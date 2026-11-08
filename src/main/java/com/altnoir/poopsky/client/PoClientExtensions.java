package com.altnoir.poopsky.client;

import com.altnoir.poopsky.client.renderer.GachaponItemRenderer;
import com.altnoir.poopsky.client.renderer.ToiletPlugItemRenderer;
import com.altnoir.poopsky.init.PFluidTypes;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.function.Supplier;

/** 由物品与流体的 initializeClient 回调调用，专用服务器不会加载渲染器。 */
public final class PoClientExtensions {
    private PoClientExtensions() {}
    public static IClientItemExtensions toiletPlug() { return item(ToiletPlugItemRenderer::new); }
    public static IClientItemExtensions gachapon() { return item(GachaponItemRenderer::new); }
    private static IClientItemExtensions item(Supplier<BlockEntityWithoutLevelRenderer> factory) {
        return new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = factory.get();
                return renderer;
            }
        };
    }
    public static IClientFluidTypeExtensions urine() {
        return new IClientFluidTypeExtensions() {
            @Override public ResourceLocation getStillTexture() { return PFluidTypes.URINE_STILL_TEXTURE; }
            @Override public ResourceLocation getFlowingTexture() { return PFluidTypes.URINE_FLOWING_TEXTURE; }
        };
    }
}
