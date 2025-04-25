package com.altnoir.poopsky;

import com.altnoir.poopsky.Fluid.PSFluids;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.keybinding.ComboConfig;
import com.altnoir.poopsky.keybinding.ComboHUD;
import com.altnoir.poopsky.keybinding.ComboHandler;
import com.altnoir.poopsky.particle.PSParticle;
import com.altnoir.poopsky.particle.PoopParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

public class PoopSkyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ComboConfig.init();
        ComboHandler.init();
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> ComboHUD.render(drawContext));

        BlockRenderLayerMap.INSTANCE.putBlock(PSBlocks.POOP_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PSBlocks.POOP_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PSBlocks.POOP_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PSBlocks.POOP_LOG, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PSBlocks.STRIPPED_POOP_LOG, RenderLayer.getCutout());

        Block[] ccb = {
                ToiletBlocks.OAK_TOILET,
                ToiletBlocks.SPRUCE_TOILET,
                ToiletBlocks.BIRCH_TOILET,
                ToiletBlocks.JUNGLE_TOILET,
                ToiletBlocks.ACACIA_TOILET,
                ToiletBlocks.CHERRY_TOILET,
                ToiletBlocks.DARK_OAK_TOILET,
                ToiletBlocks.MANGROVE_TOILET,
                ToiletBlocks.BAMBOO_TOILET,
                ToiletBlocks.CRIMSON_TOILET,
                ToiletBlocks.WARPED_TOILET,
                ToiletBlocks.WHITE_CONCRETE_TOILET,
                ToiletBlocks.ORANGE_CONCRETE_TOILET,
                ToiletBlocks.MAGENTA_CONCRETE_TOILET,
                ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET,
                ToiletBlocks.YELLOW_CONCRETE_TOILET,
                ToiletBlocks.LIME_CONCRETE_TOILET,
                ToiletBlocks.PINK_CONCRETE_TOILET,
                ToiletBlocks.GRAY_CONCRETE_TOILET,
                ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET,
                ToiletBlocks.CYAN_CONCRETE_TOILET,
                ToiletBlocks.PURPLE_CONCRETE_TOILET,
                ToiletBlocks.BLUE_CONCRETE_TOILET,
                ToiletBlocks.BROWN_CONCRETE_TOILET,
                ToiletBlocks.GREEN_CONCRETE_TOILET,
                ToiletBlocks.RED_CONCRETE_TOILET,
                ToiletBlocks.BLACK_CONCRETE_TOILET,
                ToiletBlocks.RAINBOW_TOILET
        };

        for (Block block : ccb) {
            ColorProviderRegistry.BLOCK.register(
                    (state, world, pos, tintIndex) -> 0x47311A,
                    block
            );
            ColorProviderRegistry.ITEM.register(
                    (stack, tintIndex) -> 0x47311A,
                    block
            );
        }

        FluidRenderHandlerRegistry.INSTANCE.register(PSFluids.URINE, PSFluids.FLOWING_URINE,
                new SimpleFluidRenderHandler(
                        Identifier.of("minecraft:block/water_still"),
                        Identifier.of("minecraft:block/water_flow"),
                        0x47311A
                )
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), PSFluids.URINE, PSFluids.FLOWING_URINE);
        ParticleFactoryRegistry.getInstance().register(PSParticle.POOP_PARTICLE, PoopParticle.Factory::new);
    }
}
