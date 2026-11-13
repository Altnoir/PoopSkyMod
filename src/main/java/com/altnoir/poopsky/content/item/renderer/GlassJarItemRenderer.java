package com.altnoir.poopsky.content.item.renderer;

import com.altnoir.poopsky.content.block.renderer.GlassJarBlockEntityRenderer;
import com.altnoir.poopsky.content.item.p.GlassJarBlockItem;
import com.altnoir.poopsky.init.PoBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;

public class GlassJarItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final RegistryAccess BUILTIN_REGISTRIES = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    public GlassJarItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers,
                             int light, int overlay) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getBlockRenderer().renderSingleBlock(PoBlocks.GLASS_JAR.getDefaultState(), poses, buffers,
                light, overlay, ModelData.EMPTY, null);
        var registries = minecraft.level != null ? minecraft.level.registryAccess() : BUILTIN_REGISTRIES;
        GlassJarBlockEntityRenderer.renderFluid(GlassJarBlockItem.getStoredFluid(stack, registries), poses, buffers, light);
    }
}
