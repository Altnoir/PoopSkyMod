package com.altnoir.poopsky;

import com.altnoir.poopsky.client.IntroController;
import com.altnoir.poopsky.client.PoAnimationController;
import com.altnoir.poopsky.client.creative.PoSectionedCreativeTabRenderer;
import com.altnoir.poopsky.client.model.BakedModelEventHandler;
import com.altnoir.poopsky.client.particle.LeavesParticle;
import com.altnoir.poopsky.client.particle.PoopParticle;
import com.altnoir.poopsky.client.particle.ToiletParticle;
import com.altnoir.poopsky.client.renderer.ContextualShitItemRenderer;
import com.altnoir.poopsky.client.renderer.TimeBellOverlay;
import com.altnoir.poopsky.client.renderer.ToiletHighlightRenderer;
import com.altnoir.poopsky.client.renderer.ToiletPlugItemRenderer;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.renderer.SieveBlockEntityRenderer;
import com.altnoir.poopsky.content.entity.model.FlyModel;
import com.altnoir.poopsky.content.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.content.entity.renderer.GinkgoBoatRenderer;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.fabric.PoFabricated;
import com.altnoir.poopsky.impl.event.PSKeyBoardInput;
import com.altnoir.poopsky.impl.network.PlugActionPayload;
import com.altnoir.poopsky.impl.network.PlugDismountPayload;
import com.altnoir.poopsky.init.*;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class PoopSkyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PSKeyBoardInput.register();
        ClientConfig.onLoad();
        NeoForgeConfigRegistry.INSTANCE.register(PoopSky.MOD_ID, ModConfig.Type.CLIENT, ClientConfig.CLIENT_SPEC);
        ConfigScreenFactoryRegistry.INSTANCE.register(PoopSky.MOD_ID, ConfigurationScreen::new);

        PoFabricated.clientInit();

        BakedModelEventHandler.register();
        registerLayers();
        registerBlockEntityRenderers();
        registerItemProperties();
        registerParticles();
        registerColors();
        registerRenderLayers();
        registerItemRenderers();
        registerFluidRenderers();

        HudRenderCallback.EVENT.register(TimeBellOverlay::render);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ToiletHighlightRenderer::onRenderLevel);
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            ClientGameEvents.onScreenOpen(screen);
            if (screen instanceof CreativeModeInventoryScreen creativeScreen) {
                ScreenEvents.afterRender(screen).register((renderedScreen, graphics, mouseX, mouseY, tickDelta) ->
                        PoSectionedCreativeTabRenderer.onRender(creativeScreen, graphics));
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            IntroController.onLoggingOut();
            PoAnimationController.onLoggingOut();
        });
        ClientTickEvents.START_CLIENT_TICK.register(ClientGameEvents::onClientTick);
    }

    private static void registerLayers() {
        EntityModelLayerRegistry.registerModelLayer(ToiletPlugModel.LAYER_LOCATION, ToiletPlugModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(FlyModel.LAYER_LOCATION, FlyModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(GinkgoBoatRenderer.BOAT_LAYER, BoatModel::createBodyModel);
        EntityModelLayerRegistry.registerModelLayer(GinkgoBoatRenderer.CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel);
    }

    private static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(PoBlockEntityType.SIEVE_BLOCK_ENTITY.get(), SieveBlockEntityRenderer::new);
    }

    private static void registerItemProperties() {
        ItemProperties.register(PoItems.FLY.get(), PoopSky.loc("fly_type"),
                (stack, level, entity, seed) -> {
                    String id = stack.get(PoComponents.FLY_TYPE.get());
                    return FlyType.getIndex(id != null ? id : FlyTypes.NORMAL.id());
                });

        registerToiletItemProperty(PoBlocks.WOODEN_TOILET.asItem());
        registerToiletItemProperty(PoBlocks.HARD_TOILET.asItem());
    }

    private static void registerToiletItemProperty(Item item) {
        ItemProperties.register(item, PoopSky.loc("toilet_type"),
                (stack, level, entity, seed) -> {
                    ToiletType type = stack.get(PoComponents.TOILET_TYPE.get());
                    if (type == null && item instanceof ToiletBlockItem toiletItem
                            && toiletItem.getBlock() instanceof AbstractToiletBlock toiletBlock) {
                        type = toiletBlock.getDefaultToiletType();
                    }
                    return type == null ? 0.0F : ToiletType.getCategoryIndex(type);
                });
    }

    private static void registerParticles() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(PoParticles.POOP_PARTICLE.get(), PoopParticle.Provider::new);
        registry.register(PoParticles.TOILET_PARTICLE.get(), ToiletParticle.Provider::new);
        registry.register(PoParticles.LEAVES_PARTICLE.get(), LeavesParticle.Provider::new);
    }

    private static void registerColors() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (tintIndex == 1) {
                if (state.getValue(AbstractCompooperBlock.LEVEL) != AbstractCompooperBlock.MIN_LEVEL) {
                    return world != null && pos != null
                            ? BiomeColors.getAverageWaterColor(world, pos)
                            : 0x3F76E4;
                }
                return 0x47311A;
            }
            return -1;
        }, PoBlocks.WATER_COMPOOPER.get());
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? 0xFF3F76E4 : -1,
                PoBlocks.WATER_COMPOOPER.get());
    }

    private static void registerRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                PoBlocks.POOP_EMPTY_LOG.get(),
                PoBlocks.STRIPPED_POOP_EMPTY_LOG.get(),
                PoBlocks.POOP_DOOR.get(),
                PoBlocks.POOP_TRAPDOOR.get(),
                PoBlocks.POOP_SAPLING.get(),
                PoBlocks.GINKGO_DOOR.get(),
                PoBlocks.GINKGO_TRAPDOOR.get(),
                PoBlocks.GINKGO_SAPLING.get(),
                PoBlocks.GINKGO_LEAVES.get(),
                PoBlocks.SIEVE.get(),
                PoBlocks.MAGGOTS.get(),
                PoBlocks.SALTPETER_CLUSTER.get(),
                PoBlocks.LARGE_SALTPETER_BUD.get(),
                PoBlocks.MEDIUM_SALTPETER_BUD.get(),
                PoBlocks.SMALL_SALTPETER_BUD.get(),
                PoBlocks.ROUNDWORM_VINES.get(),
                PoBlocks.ROUNDWORM_VINES_PLANT.get(),
                PoBlocks.CHILI_VINES.get(),
                PoBlocks.CHILI_VINES_PLANT.get(),
                PoBlocks.GINKGO_TOILET.get(),
                PoBlocks.PORTABLE_TOILET.get(),
                PoBlocks.FLUSH_TOILET.get(),
                PoBlocks.GOLDEN_FLUSH_TOILET.get());
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(),
                PoBlocks.STOOL.get(),
                PoBlocks.WATER_COMPOOPER.get(),
                PoBlocks.POOLIME_BLOCK.get());
        BlockRenderLayerMap.INSTANCE.putBlock(PoFluids.URINE_LIQUID.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(),
                PoFluids.URINE.get(), PoFluids.FLOWING_URINE.get());
    }

    private static void registerItemRenderers() {
        ToiletPlugItemRenderer renderer = new ToiletPlugItemRenderer();
        BuiltinItemRendererRegistry.INSTANCE.register(PoItems.TOILET_PLUG.get(), renderer::renderByItem);
        BuiltinItemRendererRegistry.INSTANCE.register(PoBlocks.SHIT.asItem(), new ContextualShitItemRenderer("shit"));
        BuiltinItemRendererRegistry.INSTANCE.register(PoBlocks.CHILI_SHIT.asItem(), new ContextualShitItemRenderer("chili_shit"));
        BuiltinItemRendererRegistry.INSTANCE.register(PoBlocks.GOLDEN_SHIT.asItem(), new ContextualShitItemRenderer("golden_shit"));
    }

    private static void registerFluidRenderers() {
        SimpleFluidRenderHandler renderer = new SimpleFluidRenderHandler(
                PoFluids.URINE_STILL_TEXTURE, PoFluids.URINE_FLOWING_TEXTURE);
        FluidRenderHandlerRegistry.INSTANCE.register(PoFluids.URINE.get(), PoFluids.FLOWING_URINE.get(), renderer);
    }

    public static final class ClientGameEvents {
        private static Holder<WorldPreset> originalDefaultWorldPreset;

        private ClientGameEvents() {
        }

        public static void onScreenOpen(Screen newScreen) {
            if (!(newScreen instanceof CreateWorldScreen screen)) return;

            WorldCreationUiState uiState = screen.getUiState();
            Holder<WorldPreset> originalPreset = uiState.getWorldType().preset();
            if (originalPreset == null) return;

            if (originalDefaultWorldPreset == null) {
                originalDefaultWorldPreset = originalPreset;
            }
            if (!originalDefaultWorldPreset.unwrapKey().equals(originalPreset.unwrapKey())) return;

            uiState.getSettings()
                    .worldgenLoadContext()
                    .registryOrThrow(Registries.WORLD_PRESET)
                    .getHolder(PoWorldPreset.overrideDefaultWorldPreset()).ifPresent(voidWorldPreset -> uiState.setWorldType(new WorldCreationUiState.WorldTypeEntry(voidWorldPreset)));
        }

        public static void onClientTick(Minecraft minecraft) {
            IntroController.tick();
            if (minecraft.player == null || minecraft.level == null) return;

            boolean isRidingPlug = minecraft.player.getVehicle() instanceof ToiletPlugEntity;
            while (PSKeyBoardInput.USE_PLUG_KEY.consumeClick()) {
                ClientPlayNetworking.send(new PlugActionPayload());
            }
            if (isRidingPlug && PSKeyBoardInput.DISMOUNT_PLUG_KEY.consumeClick()) {
                ClientPlayNetworking.send(new PlugDismountPayload());
            }
        }
    }
}
