package com.altnoir.poopsky;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.entity.PSEntities;
import com.altnoir.poopsky.entity.renderer.ToiletPlugRenderer;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.network.PSNetworking;
import com.altnoir.poopsky.sound.PSSoundEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(PoopSky.MOD_ID)
public class PoopSky {
    public static final String MOD_ID = "poopsky";
    private static final Logger LOGGER = LogUtils.getLogger();


    public PoopSky(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(PSNetworking::register);

        PSBlocks.register(modEventBus);
        PSItems.register(modEventBus);
        PSEntities.register(modEventBus);

        PSItemGroups.register(modEventBus);
        PSSoundEvents.register(modEventBus);

        PSComponents.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(PSEntities.TOILET_PLUG.get(), ToiletPlugRenderer::new);
        }
    }
}
