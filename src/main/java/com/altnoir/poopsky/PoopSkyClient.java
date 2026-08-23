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
import com.altnoir.poopsky.client.renderer.*;
import com.altnoir.poopsky.compat.jei.PSJeiRecipeCache;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.content.block.renderer.MaggotsChunkLoaderBlockEntityRenderer;
import com.altnoir.poopsky.content.entity.model.FlyModel;
import com.altnoir.poopsky.content.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.content.entity.renderer.PoBoatRenderer;
import com.altnoir.poopsky.game.client.ArcadeControlSession;
import com.altnoir.poopsky.game.client.arcade.ArcadeWorldScreenRenderer;
import com.altnoir.poopsky.impl.network.PlugActionPayload;
import com.altnoir.poopsky.impl.network.PlugDismountPayload;
import com.altnoir.poopsky.init.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;
import java.util.Set;

@Mod(value = PoopSky.MOD_ID, dist = Dist.CLIENT)
public class PoopSkyClient {
    public PoopSkyClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        var gameEventBus = NeoForge.EVENT_BUS;
        PoopSkyClient.registerMod(modEventBus);
        PoopSkyClient.registerGame(gameEventBus);
    }

    public static void registerMod(IEventBus modEventBus) {
        BakedModelEventHandler.register(modEventBus);
        modEventBus.addListener(PoKeyBoardInput::registerKeyMappings);
        modEventBus.addListener(PoBedrockModelResources::onRegisterBedrockModels);
        modEventBus.addListener(ClientModEvents::modLoad);
        modEventBus.addListener(ClientModEvents::registerLayers);
        modEventBus.addListener(ClientModEvents::registerSpecialModelRenderers);
        modEventBus.addListener(ClientModEvents::registerItemProperties);
        modEventBus.addListener(ClientModEvents::registerRenderTypes);
        modEventBus.addListener(PoGuiRenderPipelines::register);
        modEventBus.addListener(ClientModEvents::registerParticleProviders);
        modEventBus.addListener(ClientModEvents::registerBlockColors);
        modEventBus.addListener(ClientModEvents::registerItemColors);
        modEventBus.addListener(ClientModEvents::registerBlockRenderBuffers);
        modEventBus.addListener(ClientModEvents::registerRecipeBookCategories);
        modEventBus.addListener(ClientModEvents::registerGuiOverlays);
        modEventBus.addListener(ClientModEvents::registerClientExtensions);
        modEventBus.addListener(ConfigureMainRenderTargetEvent.class, IntroGlyphRenderState::configureMainRenderTarget);
        modEventBus.addListener(RegisterRenderPipelinesEvent.class, IntroGlyphRenderState::registerPipelines);
    }

    public static void registerGame(IEventBus modEventBus) {
        modEventBus.addListener(ClientGameEvents::onScreenOpen);
        modEventBus.addListener(ClientGameEvents::onClientTick);
        modEventBus.addListener(ClientGameEvents::onComputeFov);
        modEventBus.addListener(PoSectionedCreativeTabRenderer::onRenderForeground);
        modEventBus.addListener(ToiletHighlightRenderer::onRenderLevel);
        modEventBus.addListener(MaggotsChunkLoaderBlockEntityRenderer::onLevelUnload);
        modEventBus.addListener(IntroController::onLoggingOut);
        modEventBus.addListener(IntroController::onSelectMusic);
        modEventBus.addListener(PoAnimationController::onLoggingOut);
        modEventBus.addListener(ArcadeControlSession::onKeyInput);
        modEventBus.addListener(ArcadeWorldScreenRenderer::onRenderFrame);
        modEventBus.addListener(ArcadeWorldScreenRenderer::onLoggingOut);
        modEventBus.addListener(PSJeiRecipeCache::update);
    }

    public static class ClientModEvents {
        public static void modLoad(final ModConfigEvent event) {
            ClientConfig.onLoad(event.getConfig());
        }

        public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ToiletPlugModel.LAYER_LOCATION, ToiletPlugModel::createBodyLayer);
            event.registerLayerDefinition(FlyModel.LAYER_LOCATION, FlyModel::createBodyLayer);
            event.registerLayerDefinition(FlyModel.MAGGOT_LAYER_LOCATION, FlyModel::createMaggotBodyLayer);
            event.registerLayerDefinition(PoBoatRenderer.boatLayer("ginkgo"), BoatModel::createBoatModel);
            event.registerLayerDefinition(PoBoatRenderer.chestBoatLayer("ginkgo"), BoatModel::createChestBoatModel);
            event.registerLayerDefinition(PoBoatRenderer.boatLayer("primo"), BoatModel::createBoatModel);
            event.registerLayerDefinition(PoBoatRenderer.chestBoatLayer("primo"), BoatModel::createChestBoatModel);
        }

        public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
            event.register(PoopSky.loc("toilet_plug"), ToiletPlugItemRenderer.Unbaked.MAP_CODEC);
            event.register(PoopSky.loc("gachapon"), GachaponItemRenderer.Unbaked.MAP_CODEC);
        }

        public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
            event.registerBelow(VanillaGuiLayers.CAMERA_OVERLAYS, PoopSky.loc("time_bell_overlay"), TimeBellOverlay::render);
        }

        public static void registerItemProperties(RegisterRangeSelectItemModelPropertyEvent event) {
            event.register(PoopSky.loc("toilet_type"), ToiletTypeProperty.MAP_CODEC);
        }

        public static void registerRenderTypes(RegisterRenderBuffersEvent event) {
        }

        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(PoParticles.POOP_PARTICLE.get(), PoopParticle.Provider::new);
            event.registerSpriteSet(PoParticles.TOILET_PARTICLE.get(), ToiletParticle.Provider::new);
            event.registerSpriteSet(PoParticles.LEAVES_PARTICLE.get(), LeavesParticle.Provider::new);
        }

        public static void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
            event.register(List.of(BlockTintSources.constant(-1), new BlockTintSource() {
                @Override
                public int color(BlockState state) {
                    return state.getValue(AbstractCompooperBlock.LEVEL) == AbstractCompooperBlock.MIN_LEVEL
                            ? 0x47311A
                            : 0x3F76E4;
                }

                @Override
                public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                    return state.getValue(AbstractCompooperBlock.LEVEL) == AbstractCompooperBlock.MIN_LEVEL
                            ? 0x47311A
                            : BiomeColors.getAverageWaterColor(level, pos);
                }

                @Override
                public Set<Property<?>> relevantProperties() {
                    return Set.of(AbstractCompooperBlock.LEVEL);
                }
            }), PoBlocks.WATER_COMPOOPER.get());
        }

        public static void registerItemColors(RegisterColorHandlersEvent.ItemTintSources event) {
            event.register(PoopSky.loc("water_compooper"), WaterCompooperTintSource.MAP_CODEC);
        }

        public static void registerBlockRenderBuffers(RegisterRenderBuffersEvent event) {
            event.registerRenderBuffer(PoRenderTypes.chunkLoaderGlow());
        }

        public static void registerRecipeBookCategories(RegisterRecipeBookSearchCategoriesEvent event) {
        }

        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerBlock(ToiletClientBlockExtensions.INSTANCE, PoBlocks.WOODEN_TOILET.get(), PoBlocks.HARD_TOILET.get());

        }
    }

    private static final class ToiletTypeProperty implements RangeSelectItemModelProperty {
        private static final MapCodec<ToiletTypeProperty> MAP_CODEC = MapCodec.unit(new ToiletTypeProperty());

        @Override
        public float get(ItemStack stack, ClientLevel level, ItemOwner owner, int seed) {
            ToiletType type = stack.get(PoComponents.TOILET_TYPE.get());
            if (type == null) {
                return 0.0F;
            }
            int index = 0;
            for (ToiletType candidate : ToiletType.getByCategory(type.category()).values()) {
                if (candidate.equals(type)) {
                    return index;
                }
                index++;
            }
            return 0.0F;
        }

        @Override
        public MapCodec<ToiletTypeProperty> type() {
            return MAP_CODEC;
        }
    }

    public static final class WaterCompooperTintSource implements ItemTintSource {
        public static final WaterCompooperTintSource INSTANCE = new WaterCompooperTintSource();
        public static final MapCodec<WaterCompooperTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

        private WaterCompooperTintSource() {
        }

        @Override
        public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
            return ARGB.opaque(0x3F76E4);
        }

        @Override
        public MapCodec<WaterCompooperTintSource> type() {
            return MAP_CODEC;
        }
    }

    public static class ClientGameEvents {
        public static Holder<WorldPreset> originalDefaultWorldPreset;

        public static void onScreenOpen(ScreenEvent.Opening event) {
            if (event.getNewScreen() instanceof CreateWorldScreen screen) {
                var uiState = screen.getUiState();
                var originalPreset = uiState.getWorldType().preset();

                if (originalPreset != null) {
                    if (originalDefaultWorldPreset == null) {
                        originalDefaultWorldPreset = originalPreset;
                    }
                    if (originalDefaultWorldPreset.unwrapKey().equals(originalPreset.unwrapKey())) {
                        uiState.getSettings()
                                .worldgenLoadContext()
                                .lookupOrThrow(Registries.WORLD_PRESET)
                                .get(PoWorldPreset.overrideDefaultWorldPreset())
                                .ifPresent(voidWorldPreset -> uiState.setWorldType(
                                        new WorldCreationUiState.WorldTypeEntry(voidWorldPreset)));
                    }
                }
            }
        }

        public static void onComputeFov(ViewportEvent.ComputeFov event) {
            double multiplier = TimeBellOverlay.getFovMultiplier() * ArcadeControlSession.getFovMultiplier();
            if (multiplier != 1.0) {
                event.setFOV((float) Math.min(event.getFOV() * multiplier, TimeBellOverlay.MAX_FOV));
            }
        }

        public static void onClientTick(ClientTickEvent.Pre event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            boolean isRidingPlug = mc.player.getVehicle() instanceof ToiletPlugEntity;

            while (PoKeyBoardInput.USE_PLUG_KEY.consumeClick()) {
                ClientPacketDistributor.sendToServer(new PlugActionPayload());
            }
            if (isRidingPlug && PoKeyBoardInput.DISMOUNT_PLUG_KEY.consumeClick()) {
                ClientPacketDistributor.sendToServer(new PlugDismountPayload());
            }
        }
    }
}
