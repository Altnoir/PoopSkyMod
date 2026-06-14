package com.altnoir.poopsky;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.entity.renderer.*;
import com.altnoir.poopsky.event.PSClientGameEvents;
import com.altnoir.poopsky.event.PSClientModEvents;
import com.altnoir.poopsky.event.PSKeyBoardInput;
import com.altnoir.poopsky.init.PFluidTypes;
import com.altnoir.poopsky.init.PParticles;
import com.altnoir.poopsky.misc.particle.PoopParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

@Mod(value = PoopSky.MOD_ID, dist = Dist.CLIENT)
public class PoopSkyClient {
    public PoopSkyClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        var gameEventBus = NeoForge.EVENT_BUS;
        PoopSkyClient.registerMod(modEventBus);
        PoopSkyClient.registerGame(gameEventBus);
    }

    public static void registerMod(IEventBus modEventBus) {
        modEventBus.addListener(PSClientModEvents::registerLayers);
        modEventBus.addListener(PSClientModEvents::registerBlockEntityRenderers);
        modEventBus.addListener(PSKeyBoardInput::onRegisterKeyMappings);
        modEventBus.addListener(ClientModEvents::registerRenderTypes);
        modEventBus.addListener(ClientModEvents::registerEntityRenderers);
        modEventBus.addListener(ClientModEvents::registerParticleProviders);
        modEventBus.addListener(ClientModEvents::onRegisterBlockColors);
        modEventBus.addListener(ClientModEvents::onRegisterItemColors);
        modEventBus.addListener(ClientModEvents::onRegisterBlockRenderBuffers);
        modEventBus.addListener(ClientModEvents::registerClientExtensions);
    }

    public static void registerGame(IEventBus modEventBus) {
        modEventBus.addListener(PSClientGameEvents::onScreenOpen);
        modEventBus.addListener(PSClientGameEvents::onClientTick);
    }

    public static class ClientModEvents {
        public static void registerRenderTypes(RegisterNamedRenderTypesEvent event) {
            event.register(PoopSky.loc("poop_empty_log"), RenderType.cutout(), RenderType.entityCutout(PSBlocks.POOP_EMPTY_LOG.getId()));
        }

        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(PEntityType.TOILET_PLUG.get(), ToiletPlugRenderer::new);
            event.registerEntityRenderer(PEntityType.POOLIME.get(), PoolimeRenderer::new);
            event.registerEntityRenderer(PEntityType.FLY.get(), FlyRenderer::new);
            event.registerEntityRenderer(PEntityType.STOOL.get(), ChairRenderer::new);
            event.registerEntityRenderer(PEntityType.TOILET.get(), ToiletRenderer::new);
            event.registerEntityRenderer(PEntityType.POOP_TNT.get(), PoopTntRenderer::new);
        }

        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(PParticles.POOP_PARTICLE.get(), PoopParticle.Provider::new);
        }

        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register((state, world, pos, tintIndex) -> {
                if (tintIndex == 1) {
                    if (state.getValue(AbstractCompooperBlock.LEVEL) != AbstractCompooperBlock.MIN_LEVEL) {
                        return world != null && pos != null
                                ? BiomeColors.getAverageWaterColor(world, pos)
                                : 0x3F76E4;
                    }
                    return 0x47311A;
                }
                return -1;
            }, PSBlocks.WATER_COMPOOPER.get());
        }

        public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
            event.register((stack, tintIndex) -> tintIndex == 1 ? 0x3F76E4 : -1, PSBlocks.WATER_COMPOOPER.get());
        }

        public static void onRegisterBlockRenderBuffers(net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent event) {
            event.registerRenderBuffer(RenderType.translucent());
        }

        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public @NotNull ResourceLocation getStillTexture() {
                    return PFluidTypes.POOP_STILL_TEXTURE;
                }

                @Override
                public @NotNull ResourceLocation getFlowingTexture() {
                    return PFluidTypes.POOP_FLOWING_TEXTURE;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return null;
                }
            }, PFluidTypes.POOP_FLUID_TYPE.get());
        }
    }
}
