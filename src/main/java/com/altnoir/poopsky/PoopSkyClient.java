package com.altnoir.poopsky;

import com.altnoir.poopsky.client.IntroController;
import com.altnoir.poopsky.client.PoAnimationController;
import com.altnoir.poopsky.client.PoBedrockModelResources;
import com.altnoir.poopsky.client.ToiletClientBlockExtensions;
import com.altnoir.poopsky.client.creative.PoSectionedCreativeTabRenderer;
import com.altnoir.poopsky.client.model.BakedModelEventHandler;
import com.altnoir.poopsky.client.particle.LeavesParticle;
import com.altnoir.poopsky.client.particle.PoopParticle;
import com.altnoir.poopsky.client.particle.ToiletParticle;
import com.altnoir.poopsky.client.renderer.GachaponItemRenderer;
import com.altnoir.poopsky.client.renderer.TimeBellOverlay;
import com.altnoir.poopsky.client.renderer.ToiletHighlightRenderer;
import com.altnoir.poopsky.client.renderer.ToiletPlugItemRenderer;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.content.block.renderer.MaggotsChunkLoaderBlockEntityRenderer;
import com.altnoir.poopsky.content.entity.model.FlyModel;
import com.altnoir.poopsky.content.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.content.entity.renderer.PoBoatRenderer;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.game.client.ArcadeControlSession;
import com.altnoir.poopsky.game.client.arcade.ArcadeWorldScreenRenderer;
import com.altnoir.poopsky.impl.network.PlugActionPayload;
import com.altnoir.poopsky.impl.network.PlugDismountPayload;
import com.altnoir.poopsky.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import com.altnoir.poopsky.impl.network.PoNetworking;
import org.jetbrains.annotations.NotNull;

public class PoopSkyClient {
    public static void initialize() {
        IEventBus modEventBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_SPEC);

        var gameEventBus = MinecraftForge.EVENT_BUS;
        PoopSkyClient.registerMod(modEventBus);
        PoopSkyClient.registerGame(gameEventBus);
    }

    public static void registerMod(IEventBus modEventBus) {
        BakedModelEventHandler.register(modEventBus);
        modEventBus.addListener(PoKeyBoardInput::registerKeyMappings);
        modEventBus.addListener(PoBedrockModelResources::onRegisterBedrockModels);
        modEventBus.addListener(ClientModEvents::modLoad);
        modEventBus.addListener(ClientModEvents::registerLayers);
        modEventBus.addListener(ClientModEvents::registerFluidRenderTypes);
        modEventBus.addListener(ClientModEvents::registerItemProperties);
        modEventBus.addListener(ClientModEvents::registerRenderTypes);
        modEventBus.addListener(ClientModEvents::registerParticleProviders);
        modEventBus.addListener(ClientModEvents::registerRecipeBookCategories);
        modEventBus.addListener(ClientModEvents::registerBlockColors);
        modEventBus.addListener(ClientModEvents::registerItemColors);
        modEventBus.addListener(ClientModEvents::registerBlockRenderBuffers);
        modEventBus.addListener(ClientModEvents::registerGuiOverlays);
    }

    public static void registerGame(IEventBus modEventBus) {
        modEventBus.addListener(ClientGameEvents::onScreenOpen);
        modEventBus.addListener(ClientGameEvents::onClientTick);
        modEventBus.addListener(ArcadeControlSession::onKeyInput);
        modEventBus.addListener(ClientGameEvents::onComputeFov);
        modEventBus.addListener(ArcadeWorldScreenRenderer::onRenderFrame);
        modEventBus.addListener(ArcadeWorldScreenRenderer::onLoggingOut);
        modEventBus.addListener(PoSectionedCreativeTabRenderer::onRenderForeground);
        modEventBus.addListener(ToiletHighlightRenderer::onRenderLevel);
        modEventBus.addListener(MaggotsChunkLoaderBlockEntityRenderer::onLevelUnload);
        modEventBus.addListener(IntroController::onLoggingOut);
        modEventBus.addListener(IntroController::onSelectMusic);
        modEventBus.addListener(PoAnimationController::onLoggingOut);
        modEventBus.addListener((net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) -> {
            com.altnoir.poopsky.impl.PoTimeFreeze.setClientFrozen(false);
            TimeBellOverlay.setFrozen(false);
        });
    }

    public static class ClientModEvents {
        public static void modLoad(final ModConfigEvent event) {
            ClientConfig.onLoad(event.getConfig());
        }

        public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ToiletPlugModel.LAYER_LOCATION, ToiletPlugModel::createBodyLayer);
            event.registerLayerDefinition(FlyModel.LAYER_LOCATION, FlyModel::createFlyBodyLayer);
            event.registerLayerDefinition(FlyModel.MAGGOT_LAYER_LOCATION, FlyModel::createMaggotBodyLayer);
            event.registerLayerDefinition(PoBoatRenderer.boatLayer("ginkgo"), BoatModel::createBodyModel);
            event.registerLayerDefinition(PoBoatRenderer.chestBoatLayer("ginkgo"), ChestBoatModel::createBodyModel);
            event.registerLayerDefinition(PoBoatRenderer.boatLayer("primo"), BoatModel::createBodyModel);
            event.registerLayerDefinition(PoBoatRenderer.chestBoatLayer("primo"), ChestBoatModel::createBodyModel);
        }

        public static void registerFluidRenderTypes(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(PoFluids.URINE.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(PoFluids.FLOWING_URINE.get(), RenderType.translucent());
            });
        }

        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerBelow(VanillaGuiOverlay.VIGNETTE.id(), "time_bell_overlay",
                    (gui, graphics, partialTick, width, height) -> TimeBellOverlay.render(graphics));
        }

        public static void registerRenderTypes(RegisterNamedRenderTypesEvent event) {
            event.register("poop_empty_log", RenderType.cutout(), RenderType.entityCutout(PoBlocks.POOP_EMPTY_LOG.getId()));
        }

        public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
            event.registerRecipeCategoryFinder(PoRecipes.SIEVE.type().get(), recipe -> RecipeBookCategories.UNKNOWN);
            event.registerRecipeCategoryFinder(PoRecipes.FLY_BARREL.type().get(), recipe -> RecipeBookCategories.UNKNOWN);
            event.registerRecipeCategoryFinder(PoRecipes.BREEDING_CHEST.type().get(), recipe -> RecipeBookCategories.UNKNOWN);
            event.registerRecipeCategoryFinder(PoRecipes.POP_EXPLOSION.type().get(), recipe -> RecipeBookCategories.UNKNOWN);
            event.registerRecipeCategoryFinder(PoRecipes.ANAL_PRESSING.type().get(), recipe -> RecipeBookCategories.UNKNOWN);
        }

        public static void registerItemProperties(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                for (Item item : PoItems.getAllItems()) {
                    if (item instanceof ToiletBlockItem && item != Items.AIR) {
                        ItemProperties.register(item, PoopSky.loc("toilet_type"),
                                (stack, level, entity, seed) -> {
                                    ToiletType type = PoComponents.TOILET_TYPE.get(stack);
                                    if (type == null) return 0;
                                    var categoryTypes = ToiletType.getByCategory(type.category());
                                    int localIndex = 0;
                                    for (var entry : categoryTypes.entrySet()) {
                                        if (entry.getValue().equals(type)) return (float) localIndex;
                                        localIndex++;
                                    }
                                    return 0;
                                });
                    }
                }
            });
        }

        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(PoParticles.POOP_PARTICLE.get(), PoopParticle.Provider::new);
            event.registerSpriteSet(PoParticles.TOILET_PARTICLE.get(), ToiletParticle.Provider::new);
            event.registerSpriteSet(PoParticles.LEAVES_PARTICLE.get(), LeavesParticle.provider());
        }

        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
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
            }, PoBlocks.WATER_COMPOOPER.get());
        }

        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register((stack, tintIndex) -> tintIndex == 1 ? 0x3F76E4 : -1, PoBlocks.WATER_COMPOOPER.get());
        }

        public static void registerBlockRenderBuffers(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                var source = Minecraft.getInstance().renderBuffers().bufferSource();
                var buffers = ((com.altnoir.poopsky.mixin.BufferSourceAccessor) source).poopsky$fixedBuffers();
                for (RenderType type : new RenderType[]{RenderType.translucent(), RenderType.lightning(), PoRenderTypes.chunkLoaderGlow()}) {
                    buffers.computeIfAbsent(type, key -> new com.mojang.blaze3d.vertex.BufferBuilder(key.bufferSize()));
                }
            });
        }

    }

    public static class ClientGameEvents {
        public static Holder<WorldPreset> originalDefaultWorldPreset;

        public static void onScreenOpen(ScreenEvent.Opening event) {
            if (ArcadeControlSession.isActive()) {
                ArcadeControlSession.exit();
            }
            if (event.getNewScreen() instanceof CreateWorldScreen screen) {
                var uiState = screen.getUiState();
                var originalPreset = uiState.getWorldType().preset();

                if (originalPreset != null) {
                    if (originalDefaultWorldPreset == null) {
                        originalDefaultWorldPreset = originalPreset;
                    }
                    if (originalDefaultWorldPreset.unwrapKey().equals(originalPreset.unwrapKey())) {
                        var voidWorldPreset = uiState.getSettings().worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET).getHolder(PoWorldPreset.overrideDefaultWorldPreset()).orElse(null);
                        uiState.setWorldType(new WorldCreationUiState.WorldTypeEntry(voidWorldPreset));
                    }
                }
            }
        }

        public static void onComputeFov(ViewportEvent.ComputeFov event) {
            double multiplier = TimeBellOverlay.getFovMultiplier() * ArcadeControlSession.getFovMultiplier();
            if (multiplier != 1.0) {
                event.setFOV(Math.min(event.getFOV() * multiplier, TimeBellOverlay.MAX_FOV));
            }
        }

        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;
            var mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) {
                ArcadeControlSession.clear();
                return;
            }
            ArcadeControlSession.tick(mc);

            boolean isRidingPlug = mc.player.getVehicle() instanceof ToiletPlugEntity;

            while (PoKeyBoardInput.USE_PLUG_KEY.consumeClick()) {
                PoNetworking.sendToServer(new PlugActionPayload());
            }
            if (isRidingPlug && PoKeyBoardInput.DISMOUNT_PLUG_KEY.consumeClick()) {
                PoNetworking.sendToServer(new PlugDismountPayload());
            }
        }
    }
}
