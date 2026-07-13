package com.altnoir.poopsky;

import com.altnoir.poopsky.client.ToiletClientBlockExtensions;
import com.altnoir.poopsky.client.model.ToiletModelEventHandler;
import com.altnoir.poopsky.client.particle.LeavesParticle;
import com.altnoir.poopsky.client.particle.PoopParticle;
import com.altnoir.poopsky.client.particle.ToiletParticle;
import com.altnoir.poopsky.client.renderer.TimeBellOverlay;
import com.altnoir.poopsky.client.renderer.ToiletHighlightRenderer;
import com.altnoir.poopsky.client.renderer.ToiletPlugItemRenderer;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.content.entity.model.FlyModel;
import com.altnoir.poopsky.content.entity.model.ToiletPlugModel;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.impl.event.PSKeyBoardInput;
import com.altnoir.poopsky.impl.network.PlugActionPayload;
import com.altnoir.poopsky.impl.network.PlugDismountPayload;
import com.altnoir.poopsky.impl.util.PHooks;
import com.altnoir.poopsky.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
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
        ToiletModelEventHandler.register(modEventBus);
        modEventBus.addListener(PSKeyBoardInput::registerKeyMappings);
        modEventBus.addListener(ClientModEvents::registerLayers);
        modEventBus.addListener(ClientModEvents::registerItemProperties);
        modEventBus.addListener(ClientModEvents::registerRenderTypes);
        modEventBus.addListener(ClientModEvents::registerParticleProviders);
        modEventBus.addListener(ClientModEvents::registerRecipeBookCategories);
        modEventBus.addListener(ClientModEvents::registerBlockColors);
        modEventBus.addListener(ClientModEvents::registerItemColors);
        modEventBus.addListener(ClientModEvents::registerBlockRenderBuffers);
        modEventBus.addListener(ClientModEvents::registerGuiOverlays);
        modEventBus.addListener(ClientModEvents::registerClientExtensions);
    }

    public static void registerGame(IEventBus modEventBus) {
        modEventBus.addListener(ClientGameEvents::onScreenOpen);
        modEventBus.addListener(ClientGameEvents::onClientTick);
        modEventBus.addListener(ToiletHighlightRenderer::onRenderLevel);
    }

    public static class ClientModEvents {
        public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ToiletPlugModel.LAYER_LOCATION, ToiletPlugModel::createBodyLayer);
            event.registerLayerDefinition(FlyModel.LAYER_LOCATION, FlyModel::createBodyLayer);
        }

        public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
            event.registerBelow(VanillaGuiLayers.CAMERA_OVERLAYS, PoopSky.loc("time_bell_overlay"), TimeBellOverlay::render);
        }

        public static void registerRenderTypes(RegisterNamedRenderTypesEvent event) {
            event.register(PoopSky.loc("poop_empty_log"), RenderType.cutout(), RenderType.entityCutout(PoBlocks.POOP_EMPTY_LOG.getId()));
        }

        public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
            event.registerRecipeCategoryFinder(PoRecipes.SIEVE.type().get(), recipe -> RecipeBookCategories.UNKNOWN);
            event.registerRecipeCategoryFinder(PoRecipes.FLY_BARREL.type().get(), recipe -> RecipeBookCategories.UNKNOWN);
            event.registerRecipeCategoryFinder(PoRecipes.BREEDING_CHEST.type().get(), recipe -> RecipeBookCategories.UNKNOWN);
        }

        public static void registerItemProperties(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemProperties.register(PoItems.FLY.get(), PoopSky.loc("fly_type"),
                        (stack, level, entity, seed) -> {
                            String id = stack.get(PoComponents.FLY_TYPE.get());
                            return (float) FlyType.getIndex(id != null ? id : FlyTypes.NORMAL.id());
                        });

                for (Item item : PoItems.getAllItems()) {
                    if (item instanceof ToiletBlockItem && item != Items.AIR) {
                        ItemProperties.register(item, PoopSky.loc("toilet_type"),
                                (stack, level, entity, seed) -> {
                                    ToiletType type = stack.get(PoComponents.TOILET_TYPE.get());
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

        public static void registerBlockRenderBuffers(net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent event) {
            event.registerRenderBuffer(RenderType.translucent());
        }

        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerBlock(ToiletClientBlockExtensions.INSTANCE, PoBlocks.WOODEN_TOILET.get(), PoBlocks.HARD_TOILET.get());

            var toiletPlugRenderer = new ToiletPlugItemRenderer();
            event.registerItem(new IClientItemExtensions() {
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return toiletPlugRenderer;
                }
            }, PoItems.TOILET_PLUG.get());

            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public @NotNull ResourceLocation getStillTexture() {
                    return PFluidTypes.URINE_STILL_TEXTURE;
                }

                @Override
                public @NotNull ResourceLocation getFlowingTexture() {
                    return PFluidTypes.URINE_FLOWING_TEXTURE;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return null;
                }
            }, PFluidTypes.URINE_FLUID_TYPE.get());
        }
    }

    public class ClientGameEvents {
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
                        var voidWorldPreset = uiState.getSettings().worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET).getHolder(PHooks.overrideDefaultWorldPreset()).orElse(null);
                        uiState.setWorldType(new WorldCreationUiState.WorldTypeEntry(voidWorldPreset));
                    }
                }
            }
        }

        public static void onClientTick(ClientTickEvent.Pre event) {
            var mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            boolean isRidingPlug = mc.player.getVehicle() instanceof ToiletPlugEntity;

            while (PSKeyBoardInput.USE_PLUG_KEY.consumeClick()) {
                PacketDistributor.sendToServer(new PlugActionPayload());
            }
            if (isRidingPlug && PSKeyBoardInput.DISMOUNT_PLUG_KEY.consumeClick()) {
                PacketDistributor.sendToServer(new PlugDismountPayload());
            }
        }
    }
}